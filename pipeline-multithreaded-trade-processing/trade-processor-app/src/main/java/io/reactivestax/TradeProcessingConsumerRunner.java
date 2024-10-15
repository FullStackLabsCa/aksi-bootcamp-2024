package io.reactivestax;

import io.reactivestax.service.TradeProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.*;

public class TradeProcessingConsumerRunner {

    public static void main(String[] args) {

        configureHikariCP(readPropertiesFile().getProperty("dbPortNum"), readPropertiesFile().getProperty("dbName"));
        configureRabbitMQ(readPropertiesFile().getProperty("rabbitMQ.hostName"), readPropertiesFile().getProperty("rabbitMQ.guest"), readPropertiesFile().getProperty("rabbitMQ.pass"));

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        executorService.submit(new TradeProcessorRunner());

        executorService.shutdown();
    }
}

class TradeProcessorRunner implements Runnable{
    @Override
    public void run(){
        TradeProcessor processor = new TradeProcessor();
        processor.startTradeProcessingFromQueues();
    }
}
