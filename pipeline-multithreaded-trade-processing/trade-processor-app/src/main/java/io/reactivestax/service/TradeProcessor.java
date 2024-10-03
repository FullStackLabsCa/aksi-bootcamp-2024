package io.reactivestax.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.reactivestax.utility.MultithreadTradeProcessorUtility.readPropertiesFile;

public class TradeProcessor {
    int numberOfQueues = Integer.parseInt(readPropertiesFile().getProperty("numberOfQueues"));
    int threadPoolSize = Integer.parseInt(readPropertiesFile().getProperty("threadPoolSizeOfTradeProcessor"));
    ExecutorService executorServiceTradeProcessor = Executors.newFixedThreadPool(threadPoolSize);

    public void startTradeProcessingFromQueues(){

        for (int i = 0; i < numberOfQueues; i++) {
            executorServiceTradeProcessor.submit(new TradeProcessorTask(TradesStream.getQueue(i % numberOfQueues)));
        }

        executorServiceTradeProcessor.shutdown();
    }


}
