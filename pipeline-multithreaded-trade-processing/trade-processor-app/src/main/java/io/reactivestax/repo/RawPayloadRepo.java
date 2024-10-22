package io.reactivestax.repo;

import io.reactivestax.model.Trade;
import io.reactivestax.utility.exceptions.UpdateJournalEntryStatusInRawPayloadFailed;

public interface RawPayloadRepo {

    void writeToRawPayloadTable(String tradeID, String payload, String validityStatus);
    String readPayloadFromRawPayloadsTable(String tradeID);
    void updateSecurityLookupStatusInRawPayloadsTable(Trade trade, String lookupStatus);
    void updateJournalEntryStatusInRawPayloadsTable(Trade trade) throws UpdateJournalEntryStatusInRawPayloadFailed;
}
