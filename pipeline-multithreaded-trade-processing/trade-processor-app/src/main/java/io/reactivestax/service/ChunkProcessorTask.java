package io.reactivestax.service;

import io.reactivestax.interfaces.ChunkProcessing;
import io.reactivestax.interfaces.tradeIdAndAccNum;
import io.reactivestax.repo.PayloadDatabaseRepo;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import static io.reactivestax.service.ChunkProcessor.*;
import static io.reactivestax.service.TradeProcessor.listOfQueues;
import static io.reactivestax.utility.MultithreadTradeProcessorUtility.readPropertiesFile;

public class ChunkProcessorTask implements Runnable, ChunkProcessing {

    String filePath;
    static int counter = 0;
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
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void processPayload(String payload) {

        String tradeValidity = checkPayloadValidity(payload);
        tradeIdAndAccNum tradeIdentifiers = getIdentifierFromPayload(payload);

        writePayloadToPayloadDatabase(tradeIdentifiers.tradeID(), tradeValidity, payload);

        if (tradeValidity.equals("Valid")) {
            String criteria = readPropertiesFile().getProperty("tradeDistributionCriteria");
            int queueMapping;
            if (criteria.equals("tradeID")) {
                queueMapping = getQueueMapping(tradeIdentifiers.tradeID());
            } else {
                queueMapping = getQueueMapping(tradeIdentifiers.accountNumber());
            }
            writeToQueue(tradeIdentifiers.tradeID(), queueMapping);
        }
    }

    @Override
    public String checkPayloadValidity(String payload) {
        String[] fieldsOfTrade = payload.split(",");
        if (fieldsOfTrade.length != 7) {
            return "Invalid";
        } else return "Valid";
    }

    @Override
    public tradeIdAndAccNum getIdentifierFromPayload(String payload) {
        String[] fieldsOfTrade = payload.split(",");
        if ((fieldsOfTrade[0] != null) && (fieldsOfTrade[1] != null))
            return new tradeIdAndAccNum(fieldsOfTrade[0], fieldsOfTrade[2]);
        else if (fieldsOfTrade[0] == null) return new tradeIdAndAccNum("Invalid", fieldsOfTrade[1]);
        else if (fieldsOfTrade[1] == null) return new tradeIdAndAccNum(fieldsOfTrade[0], "Invalid");
        else return new tradeIdAndAccNum("Invalid", "Invalid");
    }

    @Override
    public void writePayloadToPayloadDatabase(String tradeID, String tradeStatus, String payload) {
        PayloadDatabaseRepo payloadRepo = new PayloadDatabaseRepo();
        payloadRepo.writeToDatabase(tradeID, tradeStatus, payload);
    }

    @Override
    public int getQueueMapping(String criteria) {
        if (accToQueueMap.containsKey(criteria)) {
            return accToQueueMap.get(criteria);
        } else {
            int randomQueueNum = (int) (Math.random() * Integer.parseInt(readPropertiesFile().getProperty("numberOfQueues")));
            accToQueueMap.put(criteria, randomQueueNum);
            return randomQueueNum;
        }
    }

    @Override
    public void writeToQueue(String tradeID, int queueID) {
        try {
            listOfQueues.get(queueID).put(tradeID);
        } catch (InterruptedException |  NullPointerException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
