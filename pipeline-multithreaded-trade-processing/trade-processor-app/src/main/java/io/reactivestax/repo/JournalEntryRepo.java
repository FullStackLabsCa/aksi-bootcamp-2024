package io.reactivestax.repo;

import io.reactivestax.model.Trade;
import io.reactivestax.utility.exceptions.UpdatePositionStatusInJournalEntryFailed;
import io.reactivestax.utility.exceptions.WriteToJournalEntryFailed;

public interface JournalEntryRepo {

    void writeTradeToJournalEntryTable(Trade trade) throws WriteToJournalEntryFailed;
    void updatePositionPostedStatusInJournalEntry(Trade trade) throws UpdatePositionStatusInJournalEntryFailed;

}
