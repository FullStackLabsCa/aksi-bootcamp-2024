package io.reactivestax.service;

import com.rabbitmq.client.Channel;
import io.reactivestax.factory.BeanFactory;
import io.reactivestax.interfaces.ChunkProcessing;
import io.reactivestax.interfaces.TradeIdAndAccNum;
import io.reactivestax.repo.hibernate.HibernateRawPayloadRepo;
import io.reactivestax.repo.interfaces.RawPayloadRepo;
import io.reactivestax.utility.exceptions.InvalidChunkPathException;
import io.reactivestax.utility.exceptions.RabbitMQException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.*;

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
    public void processChunk(String filePath) {
        try (Scanner chunkReader = new Scanner(new FileReader(filePath));
             Channel channel = getRabbitMQChannel()) {
            while (chunkReader.hasNextLine()) {
                processPayload(chunkReader.nextLine(), channel);
            }
        } catch (IOException e) {
            throw new InvalidChunkPathException("Unable to find chunk at the provided path");
        } catch (Exception e) {
            System.out.println("Some Issues with the RabbitMQ in Process Chunk...");
            throw new RabbitMQException(e);
        }
    }

    @Override
    public void processPayload(String payload, Channel channel) {
        String tradeValidity = checkPayloadValidity(payload);
        TradeIdAndAccNum tradeIdentifiers = getIdentifierFromPayload(payload);

        writePayloadToPayloadDatabase(tradeIdentifiers.tradeID(), payload, tradeValidity);

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
    public void writePayloadToPayloadDatabase(String tradeID, String payload, String tradeStatus) {
        RawPayloadRepo payloadRepo = BeanFactory.getRawPayloadRepo();
        payloadRepo.writeToRawPayloadTable(tradeID, payload, tradeStatus);
    }

    @Override
    public void writeToQueue(TradeIdAndAccNum tradeIdentifiers, Channel channel) {
        String exchangeName = getFileProperty("rabbitMQ.exchangeName");
        TradesStream.insertIntoRabbitMQQueue(exchangeName, tradeIdentifiers, channel);
    }
}
