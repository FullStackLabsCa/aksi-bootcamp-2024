package io.reactivestax.service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import io.reactivestax.interfaces.TradeProcessing;
import io.reactivestax.model.Trade;
import io.reactivestax.repo.PayloadDatabaseRepo;
import io.reactivestax.repo.TradesDBRepo;
import io.reactivestax.utility.NullPayloadException;
import io.reactivestax.utility.OptimisticLockingException;
import io.reactivestax.utility.ReadFromQueueFailedException;
import io.reactivestax.utility.TradeCreationFailedException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import static io.reactivestax.service.TradesStream.*;
import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.*;

public class TradeProcessorTask implements Runnable, TradeProcessing {
    LinkedBlockingDeque<String> tradeIdQueue;
    PayloadDatabaseRepo payloadDbAccess;
    TradesDBRepo tradesDbAccess;
    Session hibernateSession;
    Connection sqlConnection;

    public TradeProcessorTask(LinkedBlockingDeque<String> tradeIdQueue) {
        this.tradeIdQueue = tradeIdQueue;
        payloadDbAccess = new PayloadDatabaseRepo();
        tradesDbAccess = new TradesDBRepo();
        this.hibernateSession = hibernateSessionFactory.openSession();
        try {
            this.sqlConnection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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

    private String readPayload(String tradeID) {
        return readPayloadFromRawDatabase(hibernateSession, tradeID);
    }

    private void processTrade(Trade trade) throws InterruptedException {
        if (trade != null) {
            String lookupStatus;
            lookupStatus = validateBusinessLogic(sqlConnection, trade);
            updateTradeSecurityLookupInPayloadTable(hibernateSession, trade, lookupStatus);

            try {
                updateJournalEntryAndPositions(hibernateSession, lookupStatus, trade);
            } catch (SQLException e) {
                System.out.println("Failed updateJournalEntryAndPositions...");
                throw new RuntimeException(e);
            }

        }
    }

    private void updateJournalEntryAndPositions(Session hibernateSession, String lookupStatus, Trade trade) throws SQLException, InterruptedException {
        if (lookupStatus.equals("Valid")) {
            Transaction transaction;
            try {
                transaction = hibernateSession.beginTransaction();
            } catch (Exception e) {
                transaction = hibernateSession.getTransaction();
            }
            try {
                writeToJournalTable(hibernateSession, sqlConnection, trade);
//                writeToPositionsTable(hibernateSession, trade);
                transaction.commit();
            } catch (OptimisticLockingException e) {
                if(transaction!=null) {
                    transaction.rollback();
                }
//                TradesStream.checkRetryCountAndManageDLQ(trade, tradeIdQueue);
            }

//            updatePayloadDbForJournalEntry(hibernateSession, trade);
//            updateJEForPositionsUpdate(hibernateSession, trade);

        }
        //Disabled Logging to the Log File - No one looks at error log files
    }

    @Override
    public String readTradeIdFromQueue() throws InterruptedException {
        return readFromRabbitMQ(getFileProperty("rabbitMQ.exchangeName"),
                                getFileProperty("rabbitMQ.queueName"),
                                getFileProperty("rabbitMQ.routingKey"));
    }

    @Override
    public String readPayloadFromRawDatabase(Session hibernateSession, String tradeID) {
        return payloadDbAccess.readPayloadFromDBUsingHibernate(hibernateSession, tradeID);
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

    @Override
    public String validateBusinessLogic(Connection sqlConnection, Trade trade) {
        return tradesDbAccess.checkIfValidCUSIP(sqlConnection, trade);
    }

    @Override
    public void writeToJournalTable(Session hibernateSession, Connection sqlConnection, Trade trade) {
        tradesDbAccess.writeTradeToJournalTableUsingHibernate(hibernateSession, sqlConnection, trade);
    }

    @Override
    public void writeToPositionsTable(Session hibernateSession, Trade trade) throws OptimisticLockingException {
        tradesDbAccess.updatePositionsTableUsingHibernate(hibernateSession, trade);
    }

    private void updateTradeSecurityLookupInPayloadTable(Session hibernateSession, Trade trade, String lookupStatus) {
        payloadDbAccess.updateSecurityLookupStatusUsingHibernate(hibernateSession, trade, lookupStatus);
    }

    private void updatePayloadDbForJournalEntry(Session hibernateSession, Trade trade) {
        payloadDbAccess.updateJournalEntryStatusUsingHibernate(hibernateSession, trade);
    }

    private void updateJEForPositionsUpdate(Session hibernateSession, Trade trade) {
        tradesDbAccess.updateJEForPositionsUpdateUsingHibernate(hibernateSession, trade);
    }

}

