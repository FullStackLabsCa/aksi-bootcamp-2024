package io.reactivestax.repo.interfaces;

import io.reactivestax.model.Trade;

public interface JournalEntryRepo {

    void writeTradeToJournalEntryTable(Trade trade);
    void updateJournalEntryForPositionUpdateStatus(Trade trade);

}
