package io.reactivestax.service.interfaces;

import io.reactivestax.model.Trade;
import io.reactivestax.utility.exceptions.OptimisticLockingException;
import io.reactivestax.utility.exceptions.WriteToJournalEntryFailed;
import io.reactivestax.utility.messaging.MessageProvider;

import java.util.Optional;

public interface TradeProcessing {

    Optional<String> getTradeID(MessageProvider messageProvider) throws InterruptedException;
    Optional<String> readPayloadFromRawDatabase(String tradeID);
    Trade validatePayloadAndCreateTrade(String payload);
    String validateBusinessLogic(Trade trade);
    void writeToJournalTable(Trade trade) throws WriteToJournalEntryFailed;
    void writeToPositionsTable(Trade trade) throws OptimisticLockingException;

}
