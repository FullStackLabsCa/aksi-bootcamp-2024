package io.reactivestax.repo;

import io.reactivestax.model.Trade;
import io.reactivestax.utility.exceptions.UpdateJournalEntryStatusInRawPayloadFailed;

import java.util.Optional;

public interface RawPayloadRepo {

    Optional<String> readPayloadFromRawPayloadsTable(String tradeID);
    void updateSecurityLookupStatusInRawPayloadsTable(Trade trade, int securityId);
    void updateJournalEntryStatusInRawPayloadsTable(Trade trade) throws UpdateJournalEntryStatusInRawPayloadFailed;
}
