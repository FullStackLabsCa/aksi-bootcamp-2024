package io.reactivestax.repo;

import io.reactivestax.model.Trade;

public interface JournalEntryRepo {

    void writeTradeToJournalEntryTable(Trade trade) throws Exception;
    void updateJournalEntryForPositionUpdateStatus(Trade trade) throws Exception;

}
