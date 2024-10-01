package io.reactivestax.service;

import io.reactivestax.interfaces.chunksPathAndNumberOfChunks;

import static io.reactivestax.utility.MultithreadTradeProcessorUtility.configureHikariCP;
import static io.reactivestax.utility.MultithreadTradeProcessorUtility.configureLogger;

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
