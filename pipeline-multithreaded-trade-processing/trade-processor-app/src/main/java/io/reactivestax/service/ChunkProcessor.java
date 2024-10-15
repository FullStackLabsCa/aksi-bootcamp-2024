package io.reactivestax.service;

import io.reactivestax.utility.MultiThreadTradeProcessorUtility;

import java.sql.Connection;
import java.util.concurrent.*;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.dataSource;
import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.rabbitMQFactory;

public class ChunkProcessor{

    int numberOfThreads = Integer.parseInt(MultiThreadTradeProcessorUtility.getFileProperty("threadPoolSizeOfChunkProcessor"));
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

    public void startChunkProcessorPool() {

        //Giving 1 RabbitMQ and SQL Connection to 1 Java Program
        try(com.rabbitmq.client.Connection rabbitMQConnection = rabbitMQFactory.newConnection();
        Connection sqlConnection = dataSource.getConnection()) {
            while (true) {
                String chunkPath = ChunksStream.getRecentPostedChunkPath();
                if (chunkPath == null) break;
                executorService.submit(new ChunkProcessorTask(chunkPath, rabbitMQConnection, sqlConnection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        executorService.shutdown();
    }

}
