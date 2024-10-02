package io.reactivestax.service;

import io.reactivestax.utility.MultithreadTradeProcessorUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import static io.reactivestax.utility.MultithreadTradeProcessorUtility.readPropertiesFile;

public class TradeProcessor {
    public static final List<LinkedBlockingDeque<String>> listOfQueues = new ArrayList<>();
    int numberOfQueues = Integer.parseInt(readPropertiesFile().getProperty("numberOfQueues"));
    int threadPoolSize = Integer.parseInt(readPropertiesFile().getProperty("threadPoolSizeOfTradeProcessor"));
    ExecutorService executorServiceTradeProcessor = Executors.newFixedThreadPool(threadPoolSize);
    public static final ConcurrentHashMap<String, Integer> retryCountMapping = new ConcurrentHashMap<>();
    public static final LinkedBlockingDeque<String> dlq = new LinkedBlockingDeque<>();

    public void startTradeProcessingFromQueues(){
        bringUpQueues();

        for (int i = 0; i < numberOfQueues; i++) {
            executorServiceTradeProcessor.submit(new TradeProcessorTask(listOfQueues.get(i % numberOfQueues)));
        }

        executorServiceTradeProcessor.shutdown();
    }

    public static void bringUpQueues(){
        int numberOfQueues = Integer.parseInt(MultithreadTradeProcessorUtility.readPropertiesFile().getProperty("numberOfQueues"));

        for (int i = 0; i < numberOfQueues; i++) {
            listOfQueues.add(new LinkedBlockingDeque<>());
        }
    }
}
