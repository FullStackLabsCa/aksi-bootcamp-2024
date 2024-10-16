package io.reactivestax.service;

import io.reactivestax.utility.MultiThreadTradeProcessorUtility;

import java.sql.Connection;
import java.util.concurrent.*;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.*;

public class ChunkProcessor {

    int numberOfThreads = Integer.parseInt(MultiThreadTradeProcessorUtility.getFileProperty("threadPoolSizeOfChunkProcessor"));
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
