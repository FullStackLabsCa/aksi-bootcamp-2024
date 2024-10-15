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

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.*;

public class TradeProcessorTask implements Runnable, TradeProcessing {
    LinkedBlockingDeque<String> tradeIdQueue;
    PayloadDatabaseRepo payloadDbAccess;
    TradesDBRepo tradesDbAccess;

    public TradeProcessorTask(LinkedBlockingDeque<String> tradeIdQueue) {
        this.tradeIdQueue = tradeIdQueue;
        payloadDbAccess = new PayloadDatabaseRepo();
        tradesDbAccess = new TradesDBRepo();
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
        String payload=  "";
        try(Connection connection = dataSource.getConnection()) {
            payload = readPayloadFromRawDatabase(tradeID, connection);
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return payload;
    }

    private void processTrade(Trade trade) throws InterruptedException {
        if (trade != null) {
            String lookupStatus;
            try (Connection connection = dataSource.getConnection()) {

                lookupStatus = validateBusinessLogic(trade, connection);
                updateTradeSecurityLookupInPayloadTable(trade, lookupStatus, connection);

                updateJournalEntryAndPositions(lookupStatus, connection, trade);

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void updateJournalEntryAndPositions(String lookupStatus, Connection connection, Trade trade) throws SQLException, InterruptedException {
        boolean originalAutoCommit = connection.getAutoCommit();
        if (lookupStatus.equals("Valid")) {
            try {
                connection.setAutoCommit(false);
                writeToJournalTable(trade, connection);
                writeToPositionsTable(trade, connection);
                connection.commit();
            } catch (OptimisticLockingException | SQLException e) {
                connection.rollback();
                TradesStream.checkRetryCountAndManageDLQ(trade, tradeIdQueue);
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }

            updatePayloadDbForJournalEntry(trade, connection);
            updateJEForPositionsUpdate(trade, connection);

        }
        //Disabled Logging to the Log File - No one looks at error log files
    }

    @Override
    public String readTradeIdFromQueue() throws InterruptedException {
        String exchangeName = readPropertiesFile().getProperty("rabbitMQ.exchangeName");
        String queueName = readPropertiesFile().getProperty("rabbitMQ.queueName");
        String routingKey = readPropertiesFile().getProperty("rabbitMQ.routingKey");

        return readFromRabbitMQ(exchangeName, queueName, routingKey);
    }

    private static String readFromRabbitMQ(String exchangeName, String queueName, String routingKey){
        try (com.rabbitmq.client.Connection connection = rabbitMQFactory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(exchangeName, "direct");
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, exchangeName, routingKey);

            System.out.println(" [*] Waiting for messages in '" + queueName + "'.");

            GetResponse response = channel.basicGet(queueName, false);  // Fetch one message without auto-acknowledgment
            if (response != null) {
                String message = new String(response.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");

                // Manually acknowledge the message after processing
                channel.basicAck(response.getEnvelope().getDeliveryTag(), false);

                // Return the received message
                return message;
            } else {
                System.out.println(" [x] No messages available in the queue.");
                return null;  // No message was available at the moment
            }
        } catch (Exception e) {
            System.out.println("Some issues in RabbitMQ Consumer...");
            throw new RuntimeException(e);
        }
    }

    @Override
    public String readPayloadFromRawDatabase(String tradeID, Connection connection) {
        return payloadDbAccess.readPayloadFromDB(tradeID, connection);
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
    public String validateBusinessLogic(Trade trade, Connection connection) {
        return tradesDbAccess.checkIfValidCUSIP(trade, connection);
    }

    @Override
    public void writeToJournalTable(Trade trade, Connection connection) {
        tradesDbAccess.writeTradeToJournalTable(trade, connection);
    }

    @Override
    public void writeToPositionsTable(Trade trade, Connection connection) throws OptimisticLockingException {
        tradesDbAccess.updatePositionsTable(trade, connection);
    }

    public void updateTradeSecurityLookupInPayloadTable(Trade trade, String lookupStatus, Connection connection){
        payloadDbAccess.updateSecurityLookupStatus(trade, lookupStatus, connection);
    }

    public void updatePayloadDbForJournalEntry(Trade trade, Connection connection){
        payloadDbAccess.updateJournalEntryStatus(trade, connection);
    }

    public void updateJEForPositionsUpdate(Trade trade, Connection connection){
        tradesDbAccess.updateJEForPositionsUpdate(trade, connection);
    }
}
