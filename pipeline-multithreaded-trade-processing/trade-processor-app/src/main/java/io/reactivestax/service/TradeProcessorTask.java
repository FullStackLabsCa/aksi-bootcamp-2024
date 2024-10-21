package io.reactivestax.service;

import io.reactivestax.factory.BeanFactory;
import io.reactivestax.interfaces.TradeProcessing;
import io.reactivestax.model.Trade;
import io.reactivestax.repo.interfaces.JournalEntryRepo;
import io.reactivestax.repo.interfaces.PositionsRepo;
import io.reactivestax.repo.interfaces.RawPayloadRepo;
import io.reactivestax.repo.interfaces.SecuritiesReferenceRepo;
import io.reactivestax.utility.exceptions.NullPayloadException;
import io.reactivestax.utility.exceptions.OptimisticLockingException;
import io.reactivestax.utility.exceptions.ReadFromQueueFailedException;
import io.reactivestax.utility.exceptions.TradeCreationFailedException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;

public class TradeProcessorTask implements Runnable, TradeProcessing {
    LinkedBlockingDeque<String> tradeIdQueue;

    public TradeProcessorTask(LinkedBlockingDeque<String> tradeIdQueue) {
        this.tradeIdQueue = tradeIdQueue;
    }

    @Override
    public void run() {
        while (true) {
            String tradeID;
            try {
                tradeID = readTradeIdFromQueue();
                if (tradeID == null || tradeID.trim().isEmpty()) break;
                String payload = readPayload(tradeID);
                if ((payload != null) && (!payload.isEmpty())) {
                    Trade trade = validatePayloadAndCreateTrade(payload);
                    processTrade(trade);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ReadFromQueueFailedException(e);
            }
        }
    }

    @Override
    public String readTradeIdFromQueue() throws InterruptedException {
        return BeanFactory.getMessageReceiver().receiveMessage();
    }

    private String readPayload(String tradeID) {
        return readPayloadFromRawDatabase(tradeID);
    }

    @Override
    public String readPayloadFromRawDatabase(String tradeID) {
        RawPayloadRepo rawPayloadRepo = BeanFactory.getRawPayloadRepo();
        return rawPayloadRepo.readPayloadFromRawPayloadsTable(tradeID);
    }

    @Override
    public Trade validatePayloadAndCreateTrade(String payload) {
        if (payload == null) {
            throw new NullPayloadException("Payload Validation Failed. Payload NULL!");
        }
        try {
            Trade trade = new Trade();
            String[] payloadData = payload.split(",");
            trade.setTradeID(payloadData[0]);
            trade.setTransactionTime(convertStringToSqlDate(payloadData[1]));
            trade.setAccountNumber(payloadData[2]);
            trade.setCusip(payloadData[3]);
            trade.setActivity(payloadData[4]);
            trade.setQuantity(Integer.parseInt(payloadData[5]));
            trade.setPrice(Double.parseDouble(payloadData[6]));

            return trade;

        } catch (NumberFormatException e) {
            throw new TradeCreationFailedException("Trade Object Creation Failed!!!");
        }
    }

    public static Date convertStringToSqlDate(String dateString) {
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
        SecuritiesReferenceRepo securitiesReferenceRepo = BeanFactory.getSecuritiesReferenceRepo();
        return securitiesReferenceRepo.checkIfValidCusip(trade);
    }

    private void updateTradeSecurityLookupInPayloadTable(Trade trade, String lookupStatus) {
        RawPayloadRepo rawPayloadRepo = BeanFactory.getRawPayloadRepo();
        rawPayloadRepo.updateSecurityLookupStatusInRawPayloadsTable(trade, lookupStatus);
    }

    private void updateJournalEntryAndPositions(Trade trade, String lookupStatus){
        if (lookupStatus.equals("Valid")) {
            BeanFactory.getTransactionUtil().startTransaction();
            try {
                writeToJournalTable(trade);
                updatePayloadDbForJournalEntry(trade);

                writeToPositionsTable(trade);
                updateJEForPositionsUpdate(trade);

                BeanFactory.getTransactionUtil().commitTransaction();

            } catch (OptimisticLockingException e) {
                BeanFactory.getTransactionUtil().rollbackTransaction();
//                TradesStream.checkRetryCountAndManageDLQ(trade, tradeIdQueue);
            }

        }
        //Disabled Logging to the Log File - No one looks at error log files
    }

    @Override
    public void writeToJournalTable(Trade trade) {
        JournalEntryRepo journalEntryRepo = BeanFactory.getJournalEntryRepo();
        journalEntryRepo.writeTradeToJournalEntryTable(trade);
    }

    @Override
    public void writeToPositionsTable(Trade trade) throws OptimisticLockingException {
        PositionsRepo positionsRepo = BeanFactory.getPositionsRepo();
        positionsRepo.updatePositionsTable(trade);
    }

    private void updatePayloadDbForJournalEntry(Trade trade) {
        RawPayloadRepo rawPayloadRepo = BeanFactory.getRawPayloadRepo();
        rawPayloadRepo.updateJournalEntryStatusInRawPayloadsTable(trade);
    }

    private void updateJEForPositionsUpdate(Trade trade) {
        JournalEntryRepo journalEntryRepo = BeanFactory.getJournalEntryRepo();
        journalEntryRepo.updateJournalEntryForPositionUpdateStatus(trade);
    }

}

