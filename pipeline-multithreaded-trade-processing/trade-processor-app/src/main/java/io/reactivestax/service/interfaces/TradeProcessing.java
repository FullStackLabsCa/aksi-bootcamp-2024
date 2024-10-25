package io.reactivestax.service.interfaces;

import io.reactivestax.model.Trade;
import io.reactivestax.utility.exceptions.OptimisticLockingExceptionThrowable;
import io.reactivestax.utility.exceptions.WriteToJournalEntryFailed;
import io.reactivestax.utility.messaging.MessageProvider;

public interface TradeProcessing {

    String getTradeID(MessageProvider messageProvider) throws InterruptedException;
    String readPayloadFromRawDatabase(String tradeID);
    Trade validatePayloadAndCreateTrade(String payload);
    String validateBusinessLogic(Trade trade);
    void writeToJournalTable(Trade trade) throws WriteToJournalEntryFailed;
    void writeToPositionsTable(Trade trade) throws OptimisticLockingExceptionThrowable;

}
