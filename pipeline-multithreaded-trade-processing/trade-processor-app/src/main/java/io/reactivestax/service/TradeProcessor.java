package io.reactivestax.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.getFileProperty;

public class TradeProcessor {
    int numberOfQueues = Integer.parseInt(getFileProperty("numberOfQueues"));
    int threadPoolSize = Integer.parseInt(getFileProperty("threadPoolSizeOfTradeProcessor"));
    ExecutorService executorServiceTradeProcessor = Executors.newFixedThreadPool(threadPoolSize);

    public void startTradeProcessingFromQueues(){

        int threadsRunning=0;
        while(threadsRunning < threadPoolSize) {
                executorServiceTradeProcessor.submit(new TradeProcessorTask(null));
                threadsRunning++;
        }

        executorServiceTradeProcessor.shutdown();
    }


}
