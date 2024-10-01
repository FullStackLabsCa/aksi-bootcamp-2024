package io.reactivestax.service;

import multithread_trade_processing.interfaces.ChunkProcessing;
import multithread_trade_processing.interfaces.tradeIdAndAccNum;
import multithread_trade_processing.repo.PayloadDatabaseRepo;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import static multithread_trade_processing.service.ChunkProcessor.*;

public class ChunkProcessorTask implements Runnable, ChunkProcessing {

    String filePath;

    public ChunkProcessorTask(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void run() {
            processChunk(filePath);
    }

    @Override
    public void processChunk(String filePath){
        try (Scanner chunkReader = new Scanner(new FileReader(filePath))) {
            while (chunkReader.hasNextLine()) {
                processPayload(chunkReader.nextLine());
            }
            System.out.println("End Of File Reached!!!");
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
            int queueMapping = getQueueMapping(tradeIdentifiers.accountNumber());
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
    public int getQueueMapping(String accountNumber) {
        if (accToQueueMap.containsKey(accountNumber)) {
            return accToQueueMap.get(accountNumber);
        } else {
            int randomQueueNum = (int) (Math.random() * 3) + 1;
            accToQueueMap.put(accountNumber, randomQueueNum);
            return randomQueueNum;
        }
    }

    @Override
    public void writeToQueue(String tradeID, int queueID) {
        try {
            switch (queueID) {
                case 1:
                    tradeIdQueue1.putFirst(tradeID);
                    break;
                case 2:
                    tradeIdQueue2.putFirst(tradeID);
                    break;
                case 3:
                    tradeIdQueue3.putFirst(tradeID);
                    break;
                default:
                    System.out.println("Unable to add to Queue. Please Debug the Issue.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
