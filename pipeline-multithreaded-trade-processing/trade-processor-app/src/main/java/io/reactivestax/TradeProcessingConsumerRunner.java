package io.reactivestax;

import io.reactivestax.service.TradeProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.*;

public class TradeProcessingConsumerRunner {

    public static void main(String[] args) {

        configureHikariCP(getFileProperty("dbPortNum"), getFileProperty("dbName"));
        configureRabbitMQ(getFileProperty("rabbitMQ.hostName"), getFileProperty("rabbitMQ.guest"), getFileProperty("rabbitMQ.pass"));
        readPropertiesFile();

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
