package io.reactivestax.service.interfaces;

import io.reactivestax.entity.RawPayload;

public interface ChunkProcessing {

    void processChunk(String filePath);
    void processPayload(String payload);
    String checkPayloadValidity(String payload);
    void writePayloadToPayloadDatabase(RawPayload rawPayload);
    void sendForProcessing(TradeIdAndAccNum tradeIdentifiersAsKey, String payloadAsValue);

}
