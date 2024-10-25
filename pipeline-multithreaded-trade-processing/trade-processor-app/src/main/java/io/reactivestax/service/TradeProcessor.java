package io.reactivestax.service;

import io.reactivestax.factory.BeanFactory;
import io.reactivestax.utility.messaging.MessageProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.reactivestax.utility.ApplicationPropertyUtils.*;

public class TradeProcessor {
    int numberOfQueues = Integer.parseInt(getFileProperty("trade.processor.queue.count"));
    int threadPoolSize = Integer.parseInt(getFileProperty("thread.pool.size.trade.processor"));
    ExecutorService executorServiceTradeProcessor = Executors.newFixedThreadPool(threadPoolSize);

    private MessageProvider getMessageProvider(int queueIndex){
        return BeanFactory.getMessageProvider(queueIndex);
    }

    public void startTradeProcessingFromQueues(){

            int threadsRunning = 0;
            while (threadsRunning < threadPoolSize) {
                for (int i = 0; i < numberOfQueues; i++) {
                    executorServiceTradeProcessor.submit(new TradeProcessorRunnable(getMessageProvider(i % numberOfQueues)));
                    threadsRunning++;
                    if (threadsRunning >= threadPoolSize) break;
                }
            }
        executorServiceTradeProcessor.shutdown();
    }
}

class TradeProcessorRunnable implements Runnable{
    MessageProvider messageProvider;

    public TradeProcessorRunnable(MessageProvider messageProvider) {
        this.messageProvider = messageProvider;
    }

    @Override
    public void run() {
        TradeProcessorService tradeProcessorService = TradeProcessorService.getInstance();
        tradeProcessorService.runTradeProcessor(messageProvider);
    }
}