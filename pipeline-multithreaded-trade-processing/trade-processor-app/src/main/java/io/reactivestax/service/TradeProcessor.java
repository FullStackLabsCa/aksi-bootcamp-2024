package io.reactivestax.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.*;

public class TradeProcessor {
    int numberOfQueues = Integer.parseInt(getFileProperty("trade.processor.queue.count"));
    int threadPoolSize = Integer.parseInt(getFileProperty("thread.pool.size.trade.processor"));
    ExecutorService executorServiceTradeProcessor = Executors.newFixedThreadPool(threadPoolSize);

    public void startTradeProcessingFromQueues(){

            int threadsRunning = 0;
            while (threadsRunning < threadPoolSize) {
                executorServiceTradeProcessor.submit(new TradeProcessorRunnable());
                threadsRunning++;
            }

        executorServiceTradeProcessor.shutdown();
    }


}

class TradeProcessorRunnable implements Runnable{
    @Override
    public void run() {
        TradeProcessorService tradeProcessorService = TradeProcessorService.getInstance();
        tradeProcessorService.runTradeProcessor();
    }
}
