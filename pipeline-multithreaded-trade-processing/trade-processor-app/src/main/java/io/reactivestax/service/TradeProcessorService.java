package io.reactivestax.service;

import io.reactivestax.factory.BeanFactory;
import io.reactivestax.model.Trade;
import io.reactivestax.repo.JournalEntryRepo;
import io.reactivestax.repo.PositionsRepo;
import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.repo.SecuritiesReferenceRepo;
import io.reactivestax.service.interfaces.TradeProcessing;
import io.reactivestax.utility.database.TransactionUtil;
import io.reactivestax.utility.exceptions.*;
import io.reactivestax.utility.messaging.MessageProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class TradeProcessorService implements TradeProcessing {
    private static TradeProcessorService instance;

    private TradeProcessorService() {
    }

    public static synchronized TradeProcessorService getInstance(){
        if(instance == null) instance = new TradeProcessorService();
        return instance;
    }

    public void runTradeProcessor(MessageProvider messageProvider) {
        while (true) {
            Optional<String> tradeID = getTradeID(messageProvider);
            if(tradeID.isEmpty()) break;
            tradeID.flatMap(this::readPayload)
                    .map(this::validatePayloadAndCreateTrade)
                    .ifPresent(this::processTrade);
        }
    }

    @Override
    public Optional<String> getTradeID(MessageProvider messageProvider){
        return BeanFactory.getMessageReceiver().receiveMessage(messageProvider);
    }

    private Optional<String> readPayload(String tradeID) {
        return readPayloadFromRawDatabase(tradeID);
    }

    @Override
    public Optional<String> readPayloadFromRawDatabase(String tradeID) {
        RawPayloadRepo rawPayloadRepo = BeanFactory.getPersistenceBean(RawPayloadRepo.class);
        return rawPayloadRepo.readPayloadFromRawPayloadsTable(tradeID);
    }

    @Override
    public Trade validatePayloadAndCreateTrade(String payload) {
        if (payload == null) {
            throw new NullPayloadException("Payload Validation Failed. Payload NULL!");
        }
        try {
            String[] payloadData = payload.split(",");

            return Trade.builder()
                    .tradeID(payloadData[0])
                    .transactionTime(convertStringToSqlDate(payloadData[1]))
                    .accountNumber(payloadData[2])
                    .cusip(payloadData[3])
                    .activity(payloadData[4])
                    .quantity(Integer.parseInt(payloadData[5]))
                    .price(Double.parseDouble(payloadData[6]))
                    .build();

        } catch (NumberFormatException e) {
            throw new TradeCreationFailedException("Trade Object Creation Failed!!!");
        }
    }

    private static Date convertStringToSqlDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date utilDate = dateFormat.parse(dateString);
            return new java.sql.Date(utilDate.getTime());
        } catch (ParseException e) {
            System.err.println("Invalid date format: " + e.getMessage());
            return null;
        }
    }

    private void processTrade(Trade trade){
        if (trade != null) {
            String lookupStatus;
            lookupStatus = validateBusinessLogic(trade);
            updateTradeSecurityLookupInPayloadTable(trade, lookupStatus);

            updateJournalEntryAndPositions(trade, lookupStatus);

        }
    }

    @Override
    public String validateBusinessLogic(Trade trade) {
        SecuritiesReferenceRepo securitiesReferenceRepo = BeanFactory.getPersistenceBean(SecuritiesReferenceRepo.class);
        return securitiesReferenceRepo.checkIfValidCusip(trade);
    }

    private void updateTradeSecurityLookupInPayloadTable(Trade trade, String lookupStatus) {
        RawPayloadRepo rawPayloadRepo = BeanFactory.getPersistenceBean(RawPayloadRepo.class);
        rawPayloadRepo.updateSecurityLookupStatusInRawPayloadsTable(trade, lookupStatus);
    }

    private void updateJournalEntryAndPositions(Trade trade, String lookupStatus){
        if ("Valid".equals(lookupStatus)) {
            BeanFactory.getPersistenceBean(TransactionUtil.class).startTransaction();
            try {
                writeToJournalTable(trade);
                updateJEPostedStatusInRawPayload(trade);

                writeToPositionsTable(trade);
                updatePositionPostedStatusInJournalEntry(trade);

                BeanFactory.getPersistenceBean(TransactionUtil.class).commitTransaction();

            } catch (WriteToJournalEntryFailed | UpdateJournalEntryStatusInRawPayloadFailed |
                     OptimisticLockingException | PositionUpdateForJournalEntryFailed |
                     Exception e) {
                e.printStackTrace();
                BeanFactory.getPersistenceBean(TransactionUtil.class).rollbackTransaction();
                BeanFactory.getMessageRetryer().retryMessage(trade);
            }

        }
        //Disabled Logging to the Log File - No one looks at error log files
    }

    @Override
    public void writeToJournalTable(Trade trade) throws WriteToJournalEntryFailed{
        JournalEntryRepo journalEntryRepo = BeanFactory.getPersistenceBean(JournalEntryRepo.class);
        journalEntryRepo.writeTradeToJournalEntryTable(trade);
    }

    @Override
    public void writeToPositionsTable(Trade trade) throws OptimisticLockingException {
        PositionsRepo positionsRepo = BeanFactory.getPersistenceBean(PositionsRepo.class);
        positionsRepo.updatePositionsTable(trade);
    }

    private void updateJEPostedStatusInRawPayload(Trade trade) throws UpdateJournalEntryStatusInRawPayloadFailed{
        RawPayloadRepo rawPayloadRepo = BeanFactory.getPersistenceBean(RawPayloadRepo.class);
        rawPayloadRepo.updateJournalEntryStatusInRawPayloadsTable(trade);
    }

    private void updatePositionPostedStatusInJournalEntry(Trade trade) throws PositionUpdateForJournalEntryFailed {
        JournalEntryRepo journalEntryRepo = BeanFactory.getPersistenceBean(JournalEntryRepo.class);
        journalEntryRepo.updatePositionPostedStatusInJournalEntry(trade);
    }

}

