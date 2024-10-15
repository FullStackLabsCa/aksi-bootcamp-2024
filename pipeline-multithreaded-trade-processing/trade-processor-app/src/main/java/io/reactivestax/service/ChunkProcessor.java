package io.reactivestax.service;

import io.reactivestax.utility.MultiThreadTradeProcessorUtility;

import java.util.concurrent.*;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.rabbitMQFactory;

public class ChunkProcessor{

    int numberOfThreads = Integer.parseInt(MultiThreadTradeProcessorUtility.getFileProperty("threadPoolSizeOfChunkProcessor"));
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

    public void startChunkProcessorPool() {

        //Giving 1 RabbitMQ Connection to 1 Java Program
        try(com.rabbitmq.client.Connection connection = rabbitMQFactory.newConnection()) {
            while (true) {
                String chunkPath = ChunksStream.getRecentPostedChunkPath();
                if (chunkPath == null) break;
                executorService.submit(new ChunkProcessorTask(chunkPath, connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        executorService.shutdown();
    }

}
