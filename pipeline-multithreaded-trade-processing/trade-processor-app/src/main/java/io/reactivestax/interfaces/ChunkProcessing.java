package io.reactivestax.interfaces;

public interface ChunkProcessing {

    void processChunk(String filePath);
    void processPayload(String payload);
    String checkPayloadValidity(String payload);
    TradeIdAndAccNum getIdentifierFromPayload(String payload);
    void writePayloadToPayloadDatabase(String tradeID, String payload, String tradeStatus);
    void writeToQueue(TradeIdAndAccNum tradeIdentifiers);

}
