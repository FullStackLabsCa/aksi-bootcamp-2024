package io.reactivestax.interfaces;

import com.rabbitmq.client.Channel;

public interface ChunkProcessing {

    void processChunk(String filePath);
    void processPayload(String payload, Channel channel);
    String checkPayloadValidity(String payload);
    TradeIdAndAccNum getIdentifierFromPayload(String payload);
    void writePayloadToPayloadDatabase(String tradeID, String tradeStatus, String payload);
    void writeToQueue(TradeIdAndAccNum tradeIdentifiers, Channel channel);

}
