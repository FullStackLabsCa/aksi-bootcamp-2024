package io.reactivestax.service;

import io.reactivestax.utility.MultithreadTradeProcessorUtility;

import java.util.concurrent.*;

public class ChunkProcessor{

    public static final ConcurrentHashMap<String, Integer> accToQueueMap = new ConcurrentHashMap<>();
    public static final LinkedBlockingDeque<String> chunksPaths = new LinkedBlockingDeque<>();
    boolean POISONPILL = true;
    public void startChunkProcessorPool() {
        int numberOfThreads = Integer.parseInt(MultithreadTradeProcessorUtility.readPropertiesFile().getProperty("threadPoolSizeOfChunkProcessor"));
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        while (POISONPILL){
            try {
                executorService.submit(new ChunkProcessorTask(chunksPaths.take()));
            } catch (InterruptedException e){
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }

        executorService.shutdown();
    }

}
