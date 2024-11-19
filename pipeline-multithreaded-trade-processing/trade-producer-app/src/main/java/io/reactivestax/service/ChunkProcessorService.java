package io.reactivestax.service;

import io.reactivestax.factory.BeanFactory;
import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.service.interfaces.ChunkProcessing;
import io.reactivestax.service.interfaces.TradeIdAndAccNum;
import io.reactivestax.utility.exceptions.InvalidChunkPathException;
import io.reactivestax.utility.messaging.MessageSender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ChunkProcessorService implements ChunkProcessing {
    private static ChunkProcessorService instance;
    static String invalidString = "Invalid";

    private ChunkProcessorService() {
    }

    public static synchronized ChunkProcessorService getInstance(){
        if(instance == null) instance = new ChunkProcessorService();
        return instance;
    }

    @Override
    public void processChunk(String filePath) {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
                lines.forEach(this::processPayload);
        } catch (IOException e) {
            throw new InvalidChunkPathException("Unable to find chunk at the provided path");
        }
    }

    @Override
    public void processPayload(String payload) {
        String tradeValidity = checkPayloadValidity(payload);
        TradeIdAndAccNum tradeIdentifiers = getIdentifierFromPayload(payload);

        writePayloadToPayloadDatabase(tradeIdentifiers.tradeID(), payload, tradeValidity);

        if (tradeValidity.equals("Valid")) {
            sendForProcessing(tradeIdentifiers);
        }
    }

    @Override
    public String checkPayloadValidity(String payload) {
        try {
            return (payload.split(",").length == 7) ? "Valid" : invalidString;
        } catch (Exception e) {
            System.out.println("Failed to Check Payload Validity because " + e.getMessage());
            return invalidString;
        }
    }

    @Override
    public TradeIdAndAccNum getIdentifierFromPayload(String payload) {
        if (payload == null || payload.trim().isEmpty()) {
            return new TradeIdAndAccNum(invalidString, invalidString);
        }

        String[] fieldsOfTrade = payload.split(",");

        String tradeId = (fieldsOfTrade.length > 0 && fieldsOfTrade[0] != null)
                ? fieldsOfTrade[0]
                : invalidString;

        String accountNumber = (fieldsOfTrade.length > 2 && fieldsOfTrade[2] != null)
                ? fieldsOfTrade[2]
                : invalidString;

        return new TradeIdAndAccNum(tradeId, accountNumber);
    }

    @Override
    public void writePayloadToPayloadDatabase(String tradeID, String payload, String tradeStatus) {
        RawPayloadRepo payloadRepo = BeanFactory.getRawPayloadRepo();
        payloadRepo.writeToRawPayloadTable(tradeID, payload, tradeStatus);
    }

    @Override
    public void sendForProcessing(TradeIdAndAccNum tradeIdentifiers) {
        MessageSender<TradeIdAndAccNum> sender = BeanFactory.getMessageSender();
        sender.sendMessage(tradeIdentifiers);
    }
}
