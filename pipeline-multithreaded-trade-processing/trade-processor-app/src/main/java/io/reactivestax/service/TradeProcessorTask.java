package io.reactivestax.service;

import io.reactivestax.interfaces.TradeProcessing;
import io.reactivestax.model.Trade;
import io.reactivestax.repo.PayloadDatabaseRepo;
import io.reactivestax.repo.TradesDBRepo;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;

import static io.reactivestax.utility.MultithreadTradeProcessorUtility.*;

@SuppressWarnings("java:S2189")
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

                String payload = readPayloadFromRawDatabase(tradeID);
                Trade trade = validatePayloadAndCreateTrade(payload);
                if (trade != null) {
                    try (Connection connection = dataSource.getConnection()) {
                        if (validateBusinessLogic(trade, connection).equals("Valid")) {
                            try {
                                connection.setAutoCommit(false);
                                writeToJournalTable(trade, connection);
                                writeToPositionsTable(trade, connection);
                                connection.commit();
                            } catch (SQLException e) {
                                System.out.println(e.getMessage());
                                connection.rollback();
                            }
                        } else {
                            logger.info(trade.toString());
                        }
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String readTradeIdFromQueue() throws InterruptedException {
        return tradeIdQueue.takeFirst();
    }

    @Override
    public String readPayloadFromRawDatabase(String tradeID) {
        PayloadDatabaseRepo payloadDbAccess = new PayloadDatabaseRepo();
        return payloadDbAccess.readPayloadFromDB(tradeID);
    }

    @Override
    public Trade validatePayloadAndCreateTrade(String payload) {
        if (payload == null) {
            throw new RuntimeException("Payload Validation Failed. Payload NULL!");
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
            throw new RuntimeException("Trade Object Creation Failed!!!");
        }
    }

    public Date parseStringToDate(String dateString) {
        String format = "yyyy-MM-dd HH:mm:ss";

        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String validateBusinessLogic(Trade trade, Connection connection) {
        TradesDBRepo tradeDbAccess = new TradesDBRepo();

        return tradeDbAccess.checkIfValidCUSIP(trade, connection);
    }

    @Override
    public void writeToJournalTable(Trade trade, Connection connection) {
        TradesDBRepo tradeDbAccess = new TradesDBRepo();
        tradeDbAccess.writeTradeToJournalTable(trade, connection);
    }

    @Override
    public void writeToPositionsTable(Trade trade, Connection connection) {
        TradesDBRepo tradeDbAccess = new TradesDBRepo();
        tradeDbAccess.updatePositionsTable(trade, connection);
    }

}
