package io.reactivestax.interfaces;

public interface ChunkProcessing {

    void processChunk(String filePath);
    void processPayload(String payload);
    String checkPayloadValidity(String payload);
    tradeIdAndAccNum getIdentifierFromPayload(String payload);
    void writePayloadToPayloadDatabase(String tradeID, String tradeStatus, String payload);
    int getQueueMapping(String accountNumber);
    void writeToQueue(String tradeID, int queueID);

}
