package io.reactivestax.service;

import io.reactivestax.interfaces.ChunkProcessing;
import io.reactivestax.interfaces.TradeIdAndAccNum;
import io.reactivestax.repo.PayloadDatabaseRepo;
import io.reactivestax.utility.InvalidChunkPathException;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.dataSource;

public class ChunkProcessorTask implements Runnable, ChunkProcessing {

    String filePath;
    String invalidString = "Invalid";
    public ChunkProcessorTask(String filePath) {
        this.filePath = filePath;
    }


    @Override
    public void run() {
            processChunk(this.filePath);
    }

    @Override
    public void processChunk(String filePath){
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

        writePayloadToPayloadDatabase(tradeIdentifiers.tradeID(), tradeValidity, payload);

        if (tradeValidity.equals("Valid")) {
            writeToQueue(tradeIdentifiers);
        }
    }

    @Override
    public String checkPayloadValidity(String payload) {
        String[] fieldsOfTrade = payload.split(",");
        if (fieldsOfTrade.length != 7) {
            return invalidString;
        } else return "Valid";
    }

    @Override
    public TradeIdAndAccNum getIdentifierFromPayload(String payload) {
        String[] fieldsOfTrade = payload.split(",");
        if ((fieldsOfTrade[0] != null) && (fieldsOfTrade[1] != null))
            return new TradeIdAndAccNum(fieldsOfTrade[0], fieldsOfTrade[2]);
        else if (fieldsOfTrade[0] == null) return new TradeIdAndAccNum(invalidString, fieldsOfTrade[1]);
        else if (fieldsOfTrade[1] == null) return new TradeIdAndAccNum(fieldsOfTrade[0], invalidString);
        else return new TradeIdAndAccNum(invalidString, invalidString);
    }

    @Override
    public void writePayloadToPayloadDatabase(String tradeID, String tradeStatus, String payload) {
        PayloadDatabaseRepo payloadRepo = new PayloadDatabaseRepo();
        try(Connection connection = dataSource.getConnection()) {
            payloadRepo.writeToDatabase(tradeID, tradeStatus, payload, connection);
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void writeToQueue(TradeIdAndAccNum tradeIdentifiers) {
        TradesStream.insertIntoRabbitMQQueue(tradeIdentifiers);
    }
}
