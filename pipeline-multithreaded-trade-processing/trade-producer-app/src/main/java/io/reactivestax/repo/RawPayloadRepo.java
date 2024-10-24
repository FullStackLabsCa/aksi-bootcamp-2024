package io.reactivestax.repo;

public interface RawPayloadRepo {

    void writeToRawPayloadTable(String tradeID, String payload, String validityStatus);
}
