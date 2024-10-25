package io.reactivestax.service;

import io.reactivestax.factory.BeanFactory;
import io.reactivestax.utility.messaging.MessageProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.reactivestax.utility.ApplicationPropertyUtils.*;

public class TradeProcessor {
    int numberOfProviders = Integer.parseInt(getFileProperty("trade.processor.provider.count"));
    int threadPoolSize = Integer.parseInt(getFileProperty("thread.pool.size.trade.processor"));
    ExecutorService executorServiceTradeProcessor = Executors.newFixedThreadPool(threadPoolSize);

    private MessageProvider getMessageProvider(int providerIndex){
        return BeanFactory.getMessageProvider(providerIndex);
    }

    public void startTradesProcessing(){

        for (int i = 0; i < numberOfProviders; i++) {
            executorServiceTradeProcessor.submit(new TradeProcessorRunnable(getMessageProvider(i % numberOfProviders)));
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