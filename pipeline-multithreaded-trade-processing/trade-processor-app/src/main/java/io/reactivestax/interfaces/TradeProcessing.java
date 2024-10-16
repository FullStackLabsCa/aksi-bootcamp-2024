package io.reactivestax.interfaces;

import io.reactivestax.model.Trade;

import java.sql.Connection;
import org.hibernate.Session;

public interface TradeProcessing {

    String readTradeIdFromQueue() throws InterruptedException;
    String readPayloadFromRawDatabase(Session hibernateSession, String tradeID);
    Trade validatePayloadAndCreateTrade(String payload);
    String validateBusinessLogic(Session hibernateSession, Trade trade);
    void writeToJournalTable(Session hibernateSession, Trade trade);
    void writeToPositionsTable(Session hibernateSession, Trade trade);

}
