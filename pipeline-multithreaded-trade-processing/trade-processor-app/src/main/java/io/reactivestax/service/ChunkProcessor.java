package io.reactivestax.service;

import io.reactivestax.utility.MultithreadTradeProcessorUtility;

import java.util.concurrent.*;

public class ChunkProcessor{

    int numberOfThreads = Integer.parseInt(MultithreadTradeProcessorUtility.readPropertiesFile().getProperty("threadPoolSizeOfChunkProcessor"));
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

    public void startChunkProcessorPool() {

        while (true){
            String chunkPath = ChunksStream.getRecentPostedChunkPath();
            if(chunkPath == null) break;
            executorService.submit(new ChunkProcessorTask(chunkPath));
        }

        executorService.shutdown();
    }

}
