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

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;

import static io.reactivestax.service.TradesStream.readFromRabbitMQ;
import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.*;

public class TradeProcessorTask implements Runnable, TradeProcessing {
    LinkedBlockingDeque<String> tradeIdQueue;
    PayloadDatabaseRepo payloadDbAccess;
    TradesDBRepo tradesDbAccess;
    Connection sqlConnection;
    com.rabbitmq.client.Connection rabbitMqConnection;

    public TradeProcessorTask(LinkedBlockingDeque<String> tradeIdQueue, Connection sqlConnection, com.rabbitmq.client.Connection rabbitMQConnection) {
        this.tradeIdQueue = tradeIdQueue;
        payloadDbAccess = new PayloadDatabaseRepo();
        tradesDbAccess = new TradesDBRepo();
        this.sqlConnection = sqlConnection;
        this.rabbitMqConnection = rabbitMQConnection;
    }

    @Override
    public void run() {
        while (true) {
            String tradeID;
            try {
                tradeID = readTradeIdFromQueue();
                if(tradeID == null || tradeID.trim().isEmpty()) break;
                String payload = readPayload(tradeID);
                if((payload != null) && (!payload.isEmpty())) {
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
        return readPayloadFromRawDatabase(sqlConnection, tradeID);
    }

    private void processTrade(Trade trade) throws InterruptedException {
        if (trade != null) {
            String lookupStatus;
            lookupStatus = validateBusinessLogic(sqlConnection, trade);
            updateTradeSecurityLookupInPayloadTable(sqlConnection, trade, lookupStatus);

            try {
                updateJournalEntryAndPositions(sqlConnection, lookupStatus, trade);
            } catch (SQLException e) {
                System.out.println("Failed updateJournalEntryAndPositions...");
                throw new RuntimeException(e);
            }

        }
    }

    private void updateJournalEntryAndPositions(Connection connection, String lookupStatus, Trade trade) throws SQLException, InterruptedException {
        boolean originalAutoCommit = connection.getAutoCommit();
        if (lookupStatus.equals("Valid")) {
            try {
                connection.setAutoCommit(false);
                writeToJournalTable(connection, trade);
                writeToPositionsTable(connection, trade);
                connection.commit();
            } catch (OptimisticLockingException | SQLException e) {
                connection.rollback();
                TradesStream.checkRetryCountAndManageDLQ(trade, tradeIdQueue);
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }

            updatePayloadDbForJournalEntry(connection, trade);
            updateJEForPositionsUpdate(connection, trade);

        }
        //Disabled Logging to the Log File - No one looks at error log files
    }

    @Override
    public String readTradeIdFromQueue() throws InterruptedException {
        return readFromRabbitMQ(rabbitMqConnection,
                                getFileProperty("rabbitMQ.exchangeName"),
                                getFileProperty("rabbitMQ.queueName"),
                                getFileProperty("rabbitMQ.routingKey"));
    }

    @Override
    public String readPayloadFromRawDatabase(Connection connection, String tradeID) {
        return payloadDbAccess.readPayloadFromDB(connection, tradeID);
    }

    @Override
    public Trade validatePayloadAndCreateTrade(String payload) {
        if (payload == null) {
            throw new NullPayloadException("Payload Validation Failed. Payload NULL!");
        }
        try {
            String[] payloadData = payload.split(",");
            String tradeId = payloadData[0];
            Date transactionTime = parseStringToDate(payloadData[1]);
            String accountNumber = payloadData[2];
            String cusip = payloadData[3];
            String activity = payloadData[4];
            int quantity = Integer.parseInt(payloadData[5]);
            double price = Double.parseDouble(payloadData[6]);

            return new Trade(tradeId, transactionTime, accountNumber, cusip, activity, quantity, price);

        } catch (NumberFormatException e) {
            throw new TradeCreationFailedException("Trade Object Creation Failed!!!");
        }
    }

    public Date parseStringToDate(String dateString) {
        String format = "yyyy-MM-dd HH:mm:ss";

        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public String validateBusinessLogic(Connection connection, Trade trade) {
        return tradesDbAccess.checkIfValidCUSIP(connection, trade);
    }

    @Override
    public void writeToJournalTable(Connection connection, Trade trade) {
        tradesDbAccess.writeTradeToJournalTable(connection, trade);
    }

    @Override
    public void writeToPositionsTable(Connection connection, Trade trade) throws OptimisticLockingException {
        tradesDbAccess.updatePositionsTable(connection, trade);
    }

    private void updateTradeSecurityLookupInPayloadTable(Connection connection, Trade trade, String lookupStatus){
        payloadDbAccess.updateSecurityLookupStatus(connection, trade, lookupStatus);
    }

    private void updatePayloadDbForJournalEntry(Connection connection, Trade trade){
        payloadDbAccess.updateJournalEntryStatus(connection, trade);
    }

    private void updateJEForPositionsUpdate(Connection connection, Trade trade){
        tradesDbAccess.updateJEForPositionsUpdate(connection, trade);
    }
}
