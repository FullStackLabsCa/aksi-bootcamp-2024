package io.reactivestax.interfaces;

import io.reactivestax.model.Trade;

public interface TradeProcessing {

    String readTradeIdFromQueue() throws InterruptedException;
    String readPayloadFromRawDatabase(String tradeID);
    Trade validatePayloadAndCreateTrade(String payload);
    String validateBusinessLogic(Trade trade);
    void writeToJournalTable(Trade trade);
    void writeToPositionsTable(Trade trade);

}
