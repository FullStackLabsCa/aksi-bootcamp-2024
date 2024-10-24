package io.reactivestax.service.interfaces;

import io.reactivestax.model.Trade;
import io.reactivestax.utility.exceptions.OptimisticLockingExceptionThrowable;
import io.reactivestax.utility.exceptions.WriteToJournalEntryFailed;

public interface TradeProcessing {

    String getTradeID() throws InterruptedException;
    String readPayloadFromRawDatabase(String tradeID);
    Trade validatePayloadAndCreateTrade(String payload);
    String validateBusinessLogic(Trade trade);
    void writeToJournalTable(Trade trade) throws WriteToJournalEntryFailed;
    void writeToPositionsTable(Trade trade) throws OptimisticLockingExceptionThrowable;

}
