package io.reactivestax.service;

import io.reactivestax.utility.MultiThreadTradeProcessorUtility;
import io.reactivestax.utility.messaging.ChunksStream;

import java.util.concurrent.*;

public class ChunkProcessor {

    int numberOfThreads = Integer.parseInt(MultiThreadTradeProcessorUtility.getFileProperty("thread.pool.size.chunk.processor"));
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

    public void startChunkProcessorPool() {

        //Giving 1 RabbitMQ and SQL Connection to 1 Java Program
        while (true) {
            String chunkPath = ChunksStream.getRecentPostedChunkPath();
            if (chunkPath == null) break;
            executorService.submit(new ChunkProcessorRunnable(chunkPath));
        }

        executorService.shutdown();
    }

}

class ChunkProcessorRunnable implements Runnable{

    String chunkPath;
    public ChunkProcessorRunnable(String chunkPath) {
        this.chunkPath = chunkPath;
    }
    @Override
    public void run() {
        ChunkProcessorService chunkProcessorService = ChunkProcessorService.getInstance();
        chunkProcessorService.processChunk(this.chunkPath);
    }
}