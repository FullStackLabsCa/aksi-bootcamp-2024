package io.reactivestax.service;

import io.reactivestax.entity.RawPayload;
import io.reactivestax.factory.BeanFactory;
import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.service.interfaces.ChunkProcessing;
import io.reactivestax.service.interfaces.TradeIdAndAccNum;
import io.reactivestax.utility.exceptions.FilepathProcessingException;
import io.reactivestax.utility.messaging.MessageSender;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ChunkProcessorService implements ChunkProcessing {
    private static final String splitter = ",";
    private static final String VALID = "Valid";
    private static final String INVALID = "Invalid";
    private static final String NOT_POSTED = "Not Posted";
    private static ChunkProcessorService instance;

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
        } catch (NullPointerException | IOException | UncheckedIOException e) {
            throw new FilepathProcessingException("Failed to process provided chunk: " + filePath);
        }
    }

    @Override
    public void processPayload(String payload) {
        RawPayload rawPayload = getRawPayloadFromStringPayload(payload);
        TradeIdAndAccNum tradeIdentifiers = getIdentifierFromPayload(payload);

        writePayloadToPayloadDatabase(rawPayload);

        if (VALID.equals(rawPayload.getStatus())) {
            sendForProcessing(tradeIdentifiers);
        }
    }

    @Override
    public String checkPayloadValidity(String payload) {
        try {
            return (payload.split(splitter).length == 7) ? VALID : INVALID;
        } catch (Exception e) {
            System.out.println("Failed to Check Payload Validity because " + e.getMessage());
            return INVALID;
        }
    }

    public TradeIdAndAccNum getIdentifierFromPayload(String payload) {
        if (payload == null || payload.trim().isEmpty()) {
            return new TradeIdAndAccNum(INVALID, INVALID);
        }

        String[] fieldsOfTrade = payload.split(splitter);

        String tradeId = (fieldsOfTrade.length > 0 && fieldsOfTrade[0] != null)
                ? fieldsOfTrade[0]
                : INVALID;

        String accountNumber = (fieldsOfTrade.length > 2 && fieldsOfTrade[2] != null)
                ? fieldsOfTrade[2]
                : INVALID;

        return new TradeIdAndAccNum(tradeId, accountNumber);
    }

    // This can be refactored into the repo class?
    public RawPayload getRawPayloadFromStringPayload(String payload){
        if(payload == null || payload.trim().isEmpty())
            return RawPayload.builder().build();
        String[] fieldsOfPayload = payload.split(splitter);
        return RawPayload.builder()
                .tradeID(fieldsOfPayload[0])
                .payload(payload)
                .status(checkPayloadValidity(payload))
                .lookupStatus(NOT_POSTED)
                .postedStatus(NOT_POSTED)
                .build();
    }

    @Override
    public void writePayloadToPayloadDatabase(RawPayload rawPayload) {
        RawPayloadRepo payloadRepo = BeanFactory.getRawPayloadRepo();
        payloadRepo.writeToRawPayloadTable(rawPayload);
    }

    @Override
    public void sendForProcessing(TradeIdAndAccNum tradeIdentifiers) {
        MessageSender<TradeIdAndAccNum> sender = BeanFactory.getMessageSender();
        sender.sendMessage(tradeIdentifiers);
    }
}
