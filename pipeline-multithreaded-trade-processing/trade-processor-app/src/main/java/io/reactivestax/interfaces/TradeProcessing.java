package io.reactivestax.interfaces;

import io.reactivestax.model.Trade;

import java.sql.Connection;

public interface TradeProcessing {

    String readTradeIdFromQueue() throws InterruptedException;
    String readPayloadFromRawDatabase(Connection connection, String tradeID);
    Trade validatePayloadAndCreateTrade(String payload);
    String validateBusinessLogic(Connection connection, Trade trade);
    void writeToJournalTable(Connection connection, Trade trade);
    void writeToPositionsTable(Connection connection, Trade trade);

}
