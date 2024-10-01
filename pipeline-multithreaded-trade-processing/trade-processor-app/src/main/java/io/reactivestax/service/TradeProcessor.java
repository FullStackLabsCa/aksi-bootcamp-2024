package io.reactivestax.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static multithread_trade_processing.service.ChunkProcessor.*;

public class TradeProcessor {

    public void startTradeProcessingFromQueues(){
        //Start 3 Threads and assign them a TradeProcessorTask with the queue_id Input
        int numberOfQueues = 3;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfQueues);

        executorService.submit(new TradeProcessorTask(tradeIdQueue1));
        executorService.submit(new TradeProcessorTask(tradeIdQueue2));
        executorService.submit(new TradeProcessorTask(tradeIdQueue3));

        executorService.shutdown();

    }
}
