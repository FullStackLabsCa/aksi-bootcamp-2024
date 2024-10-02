package io.reactivestax;

import io.reactivestax.interfaces.chunksPathAndNumberOfChunks;
import io.reactivestax.service.ChunkProcessor;
import io.reactivestax.service.TradeProcessor;
import io.reactivestax.service.TradesFileReader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.reactivestax.service.TradeProcessor.bringUpQueues;
import static io.reactivestax.utility.MultithreadTradeProcessorUtility.*;

public class TradeProcessingAppRunner {

    public static void main(String[] args) {

        configureLogger();
        configureHikariCP(readPropertiesFile().getProperty("dbPortNum"), readPropertiesFile().getProperty("dbName"));

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        executorService.submit(new TradeProcessorRunner());
        executorService.submit(new FileReaderRunner());
        executorService.submit(new ChunkProcessorRunner());

        executorService.shutdown();
    }
}

class TradeProcessorRunner implements Runnable{
    @Override
    public void run(){
        TradeProcessor processor = new TradeProcessor();
        processor.startTradeProcessingFromQueues();
    }
}

class FileReaderRunner implements Runnable{
    @Override
    public void run(){
        String folderPath = readPropertiesFile().getProperty("resourcesFolderPath");
        TradesFileReader reader = new TradesFileReader();
        chunksPathAndNumberOfChunks result =  reader.readFileAndCreateChunks(folderPath+"/"+readPropertiesFile().getProperty("dataFileName"), null);
    }
}

class ChunkProcessorRunner implements Runnable{
    @Override
    public void run(){
        ChunkProcessor chunkProcessor = new ChunkProcessor();
        chunkProcessor.startChunkProcessorPool(result.folderPath());
    }
}