package io.reactivestax.service;

import io.reactivestax.factory.BeanFactory;
import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.service.interfaces.ChunkProcessing;
import io.reactivestax.service.interfaces.TradeIdAndAccNum;
import io.reactivestax.utility.exceptions.InvalidChunkPathException;
import io.reactivestax.utility.messaging.MessageSender;

import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

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
        try (Scanner chunkReader = new Scanner(new FileReader(filePath))) {
            while (chunkReader.hasNextLine()) {
                processPayload(chunkReader.nextLine());
            }
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
        return (payload.split(",").length == 7) ? "Valid" : invalidString;
    }

    @Override
    public TradeIdAndAccNum getIdentifierFromPayload(String payload) {
        String[] fieldsOfTrade = payload.split(",");

        String tradeId = Optional.ofNullable(fieldsOfTrade[0])
                                    .orElse(invalidString);

        String accountNumber = Optional.ofNullable(fieldsOfTrade[1])
                                        .orElse(invalidString);

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
