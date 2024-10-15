package io.reactivestax;

import io.reactivestax.service.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.*;

public class TradeProcessingProducerRunner {

    public static void main(String[] args) {

        configureHikariCP(readPropertiesFile().getProperty("dbPortNum"), readPropertiesFile().getProperty("dbName"));
        configureRabbitMQ(readPropertiesFile().getProperty("rabbitMQ.hostName"), readPropertiesFile().getProperty("rabbitMQ.guest"), readPropertiesFile().getProperty("rabbitMQ.pass"));

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(new FileReaderRunner());
        executorService.submit(new ChunkProcessorRunner());
//        executorService.submit(new TradesStream());

        executorService.shutdown();
    }
}

class FileReaderRunner implements Runnable{
    @Override
    public void run(){
        String folderPath = readPropertiesFile().getProperty("resourcesFolderPath");
        TradesFileReader reader = new TradesFileReader();
        reader.readFileAndCreateChunks(folderPath+"/"+readPropertiesFile().getProperty("dataFileName"), null);
    }
}
class ChunkProcessorRunner implements Runnable{
    @Override
    public void run(){
        ChunkProcessor chunkProcessor = new ChunkProcessor();
        chunkProcessor.startChunkProcessorPool();
    }
}
