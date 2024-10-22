package io.reactivestax.repo;

import io.reactivestax.model.Trade;
import io.reactivestax.utility.exceptions.PositionUpdateForJournalEntryFailed;
import io.reactivestax.utility.exceptions.WriteToJournalEntryFailed;

public interface JournalEntryRepo {

    void writeTradeToJournalEntryTable(Trade trade) throws WriteToJournalEntryFailed;
    void updateJournalEntryForPositionUpdateStatus(Trade trade) throws PositionUpdateForJournalEntryFailed;

}
