package io.reactivestax.repo;

import io.reactivestax.entity.RawPayload;

public interface RawPayloadRepo {

    void writeToRawPayloadTable(RawPayload rawPayload);
}
