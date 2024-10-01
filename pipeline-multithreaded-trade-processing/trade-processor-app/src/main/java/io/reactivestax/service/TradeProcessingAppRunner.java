package io.reactivestax.service;

import multithread_trade_processing.interfaces.chunksPathAndNumberOfChunks;

import static multithread_trade_processing.utility.MultithreadTradeProcessorUtility.configureHikariCP;
import static multithread_trade_processing.utility.MultithreadTradeProcessorUtility.configureLogger;

public class TradeProcessingAppRunner {

    public static void main(String[] args) {
        String folderPath = "boca-bc24-java-core-problems/src/multithread_trade_processing";

        configureLogger();
        configureHikariCP(3306);

        //This is 1 thread
        TradeProcessor processor = new TradeProcessor();
        processor.startTradeProcessingFromQueues();

        //This is another thread
        TradesFileReader reader = new TradesFileReader();
        chunksPathAndNumberOfChunks result =  reader.readFileAndCreateChunks(folderPath+"/trades.csv", null);

        //This is another thread
        ChunkProcessor chunkProcessor = new ChunkProcessor();
        chunkProcessor.startChunkProcessorPool(result.folderPath(), result.numberOfFiles());

    }
}
