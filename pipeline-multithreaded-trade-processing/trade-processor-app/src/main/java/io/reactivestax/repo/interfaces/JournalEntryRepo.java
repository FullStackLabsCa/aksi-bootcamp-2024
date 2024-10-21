package io.reactivestax.repo.interfaces;

import io.reactivestax.model.Trade;

public interface JournalEntryRepo {

    void writeTradeToJournalEntryTable(Trade trade) throws Exception;
    void updateJournalEntryForPositionUpdateStatus(Trade trade) throws Exception;

}
