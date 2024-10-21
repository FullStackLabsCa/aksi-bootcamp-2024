package io.reactivestax.service.interfaces;

import io.reactivestax.model.Trade;
import io.reactivestax.utility.exceptions.OptimisticLockingExceptionThrowable;

public interface TradeProcessing {

    String getTradeID() throws InterruptedException;
    String readPayloadFromRawDatabase(String tradeID);
    Trade validatePayloadAndCreateTrade(String payload);
    String validateBusinessLogic(Trade trade);
    void writeToJournalTable(Trade trade) throws Exception;
    void writeToPositionsTable(Trade trade) throws OptimisticLockingExceptionThrowable;

}
