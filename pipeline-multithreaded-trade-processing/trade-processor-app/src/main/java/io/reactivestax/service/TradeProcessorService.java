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
            System.out.println("Processing Trade: " + trade.getTradeID());
            int securityId = validateBusinessLogic(trade);
            updateTradeSecurityLookupInRawPayloadTable(trade, securityId); // TODO:: This could be merged into updateJEPostedStatusInRawPayload
            updateJournalEntryAndPositions(trade, securityId);
            System.out.println("Finished Processing Trade: " + trade.getTradeID());
        }
    }

    @Override
    public int validateBusinessLogic(Trade trade) {
        SecuritiesReferenceRepo securitiesReferenceRepo = BeanFactory.getPersistenceBean(SecuritiesReferenceRepo.class);
        return securitiesReferenceRepo.checkIfValidCusip(trade);
    }

    private void updateTradeSecurityLookupInRawPayloadTable(Trade trade, int securityId) {
        RawPayloadRepo rawPayloadRepo = BeanFactory.getPersistenceBean(RawPayloadRepo.class);
        rawPayloadRepo.updateSecurityLookupStatusInRawPayloadsTable(trade, securityId);
    }

    private void updateJournalEntryAndPositions(Trade trade, int securityId){
        if (securityId != -1) {
            BeanFactory.getPersistenceBean(TransactionUtil.class).startTransaction();
            try {
                writeToJournalTable(trade, securityId);
                updateJEPostedStatusInRawPayload(trade);
                writeToPositionsTable(trade, securityId);
                updatePositionPostedStatusInJournalEntry(trade);
                BeanFactory.getPersistenceBean(TransactionUtil.class).commitTransaction();
            } catch (WriteToJournalEntryFailed | UpdateJournalEntryStatusInRawPayloadFailed |
                     OptimisticLockingOccurrence | PositionUpdateFailed | UpdatePositionStatusInJournalEntryFailed e) {
                System.out.println("Failed to Update Journal Entry and Positions");
                BeanFactory.getPersistenceBean(TransactionUtil.class).rollbackTransaction();
//                BeanFactory.getMessageRetryer().retryMessage(trade);
            }
        }
//        else
//            System.out.println("Invalid Cusip, Not Being Processed for Journal Entry and Positions Update");
    }

    @Override
    public void writeToJournalTable(Trade trade, int securityId) throws WriteToJournalEntryFailed{
        JournalEntryRepo journalEntryRepo = BeanFactory.getPersistenceBean(JournalEntryRepo.class);
        journalEntryRepo.writeTradeToJournalEntryTable(trade, securityId);
    }

    @Override
    public void writeToPositionsTable(Trade trade, int securityId) throws OptimisticLockingOccurrence, PositionUpdateFailed {
        PositionsRepo positionsRepo = BeanFactory.getPersistenceBean(PositionsRepo.class);
        positionsRepo.updatePositionsTable(trade, securityId);
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

