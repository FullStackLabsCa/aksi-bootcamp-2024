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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TradeProcessorService implements TradeProcessing {
    private static TradeProcessorService instance;

    private TradeProcessorService() {
    }

    public static synchronized TradeProcessorService getInstance(){
        if(instance == null) instance = new TradeProcessorService();
        return instance;
    }

    @Override
    public Trade validatePayloadAndCreateTrade(String payload) {
        if (payload == null || payload.trim().isEmpty()) {
            throw new TradeCreationFailedException("Payload NULL!");
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

    public void processTrade(Trade trade){
        if (trade != null) {
            String lookupStatus = validateBusinessLogic(trade); // TODO:: This could Return the SecurityID if Valid otherwise Invalid and I could create DTO below and pass it over to UpdateJEAndPositionsTable
            updateTradeSecurityLookupInPayloadTable(trade, lookupStatus); // TODO:: This could be merged into updateJEPostedStatusInRawPayload
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
                     OptimisticLockingOccurrence | PositionUpdateFailed | UpdatePositionStatusInJournalEntryFailed e) {
                System.out.println("Failed to Update Journal Entry and Positions");
                BeanFactory.getPersistenceBean(TransactionUtil.class).rollbackTransaction();
                BeanFactory.getMessageRetryer().retryMessage(trade);
            }
        }
//        else
//            System.out.println("Invalid Cusip, Not Being Processed for Journal Entry and Positions Update");
    }

    @Override
    public void writeToJournalTable(Trade trade) throws WriteToJournalEntryFailed{
        JournalEntryRepo journalEntryRepo = BeanFactory.getPersistenceBean(JournalEntryRepo.class);
        journalEntryRepo.writeTradeToJournalEntryTable(trade);
    }

    @Override
    public void writeToPositionsTable(Trade trade) throws OptimisticLockingOccurrence, PositionUpdateFailed {
        PositionsRepo positionsRepo = BeanFactory.getPersistenceBean(PositionsRepo.class);
        positionsRepo.updatePositionsTable(trade);
    }

    private void updateJEPostedStatusInRawPayload(Trade trade) throws UpdateJournalEntryStatusInRawPayloadFailed{
        RawPayloadRepo rawPayloadRepo = BeanFactory.getPersistenceBean(RawPayloadRepo.class);
        rawPayloadRepo.updateJournalEntryStatusInRawPayloadsTable(trade);
    }

    private void updatePositionPostedStatusInJournalEntry(Trade trade) throws UpdatePositionStatusInJournalEntryFailed {
        JournalEntryRepo journalEntryRepo = BeanFactory.getPersistenceBean(JournalEntryRepo.class);
        journalEntryRepo.updatePositionPostedStatusInJournalEntry(trade);
    }
}

