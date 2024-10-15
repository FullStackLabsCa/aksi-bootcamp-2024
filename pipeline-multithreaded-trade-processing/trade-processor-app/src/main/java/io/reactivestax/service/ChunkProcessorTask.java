package io.reactivestax.service;

import com.rabbitmq.client.Channel;
import io.reactivestax.interfaces.ChunkProcessing;
import io.reactivestax.interfaces.TradeIdAndAccNum;
import io.reactivestax.repo.PayloadDatabaseRepo;
import io.reactivestax.utility.InvalidChunkPathException;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.Scanner;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.*;

public class ChunkProcessorTask implements Runnable, ChunkProcessing {

    String filePath;
    String invalidString = "Invalid";
    com.rabbitmq.client.Connection rabbitMQConnection;
    Connection sqlConnection;

    public ChunkProcessorTask(String filePath, com.rabbitmq.client.Connection rabbitMQConnection, Connection sqlConnection) {
        this.filePath = filePath;
        this.rabbitMQConnection = rabbitMQConnection;
        this.sqlConnection = sqlConnection;
    }


    @Override
    public void run() {
        processChunk(this.filePath);
    }

    @Override
    public void processChunk(String filePath) {
        try (Scanner chunkReader = new Scanner(new FileReader(filePath));
             Channel channel = rabbitMQConnection.createChannel()) {
            while (chunkReader.hasNextLine()) {
                processPayload(chunkReader.nextLine(), channel);
            }
        } catch (IOException e) {
            throw new InvalidChunkPathException("Unable to find chunk at the provided path");
        } catch (Exception e) {
            System.out.println("Some Issues with the RabbitMQ in Process Chunk...");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void processPayload(String payload, Channel channel) {
        String tradeValidity = checkPayloadValidity(payload);
        TradeIdAndAccNum tradeIdentifiers = getIdentifierFromPayload(payload);

        writePayloadToPayloadDatabase(sqlConnection, tradeIdentifiers.tradeID(), tradeValidity, payload);

        if (tradeValidity.equals("Valid")) {
            writeToQueue(tradeIdentifiers, channel);
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
    public void writePayloadToPayloadDatabase(Connection connection, String tradeID, String tradeStatus, String payload) {
        PayloadDatabaseRepo payloadRepo = new PayloadDatabaseRepo();
        payloadRepo.writeToDatabase(connection, tradeID, tradeStatus, payload);
    }

    @Override
    public void writeToQueue(TradeIdAndAccNum tradeIdentifiers, Channel channel) {
        String exchangeName = getFileProperty("rabbitMQ.exchangeName");
        TradesStream.insertIntoRabbitMQQueue(exchangeName, tradeIdentifiers, channel);
    }
}
