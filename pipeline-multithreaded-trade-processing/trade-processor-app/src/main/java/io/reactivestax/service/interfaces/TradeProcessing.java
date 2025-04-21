package io.reactivestax.service.interfaces;

import io.reactivestax.model.Trade;
import io.reactivestax.utility.exceptions.OptimisticLockingOccurrence;
import io.reactivestax.utility.exceptions.PositionUpdateFailed;
import io.reactivestax.utility.exceptions.WriteToJournalEntryFailed;

public interface TradeProcessing {

    Trade validatePayloadAndCreateTrade(String payload);
    int validateBusinessLogic(Trade trade);
    void writeToJournalTable(Trade trade, int securityId) throws WriteToJournalEntryFailed;
    void writeToPositionsTable(Trade trade, int securityId) throws OptimisticLockingOccurrence, PositionUpdateFailed;

}
