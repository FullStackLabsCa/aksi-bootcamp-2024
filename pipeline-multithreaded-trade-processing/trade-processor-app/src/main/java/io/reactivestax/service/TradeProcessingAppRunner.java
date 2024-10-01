package io.reactivestax.service;

import io.reactivestax.interfaces.chunksPathAndNumberOfChunks;

import static io.reactivestax.utility.MultithreadTradeProcessorUtility.*;

public class TradeProcessingAppRunner {

    public static void main(String[] args) {
        String folderPath = readPropertiesFile().getProperty("resourcesFolderPath");

        configureLogger();
        configureHikariCP(readPropertiesFile().getProperty("dbPortNum"), readPropertiesFile().getProperty("dbName"));

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
