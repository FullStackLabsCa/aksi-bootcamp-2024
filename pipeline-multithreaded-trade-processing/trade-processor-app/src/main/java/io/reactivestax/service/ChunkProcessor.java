package io.reactivestax.service;

import io.reactivestax.utility.MultiThreadTradeProcessorUtility;

import java.util.concurrent.*;

public class ChunkProcessor {

    int numberOfThreads = Integer.parseInt(MultiThreadTradeProcessorUtility.getFileProperty("thread.pool.size.chunk.processor"));
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

    public void startChunkProcessorPool() {

        //Giving 1 RabbitMQ and SQL Connection to 1 Java Program
        while (true) {
            String chunkPath = ChunksStream.getRecentPostedChunkPath();
            if (chunkPath == null) break;
            executorService.submit(new ChunkProcessorTask(chunkPath));
        }

        executorService.shutdown();
    }

}
