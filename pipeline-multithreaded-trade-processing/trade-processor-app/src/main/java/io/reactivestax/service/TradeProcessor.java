package io.reactivestax.service;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.*;

public class TradeProcessor {
    int numberOfQueues = Integer.parseInt(getFileProperty("numberOfQueues"));
    int threadPoolSize = Integer.parseInt(getFileProperty("threadPoolSizeOfTradeProcessor"));
    ExecutorService executorServiceTradeProcessor = Executors.newFixedThreadPool(threadPoolSize);

    public void startTradeProcessingFromQueues(){

        try(Connection sqlConnection = dataSource.getConnection();
            com.rabbitmq.client.Connection rabbitMQConnection = rabbitMQFactory.newConnection()) {
            int threadsRunning = 0;
            while (threadsRunning < threadPoolSize) {
                executorServiceTradeProcessor.submit(new TradeProcessorTask(null, sqlConnection, rabbitMQConnection));
                threadsRunning++;
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("startTradeProcessingFromQueues");
        }

        executorServiceTradeProcessor.shutdown();
    }


}
