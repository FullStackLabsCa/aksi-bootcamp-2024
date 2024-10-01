package io.reactivestax.interfaces;

import io.reactivestax.model.Trade;

import java.sql.Connection;

public interface TradeProcessing {

    String readTradeIdFromQueue() throws InterruptedException;
    String readPayloadFromRawDatabase(String tradeID);
    Trade validatePayloadAndCreateTrade(String payload);
    String validateBusinessLogic(Trade trade, Connection connection);
    void writeToJournalTable(Trade trade, Connection connection);
    void writeToPositionsTable(Trade trade, Connection connection);

}
