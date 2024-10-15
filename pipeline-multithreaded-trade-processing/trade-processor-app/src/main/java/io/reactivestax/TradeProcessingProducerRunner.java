package io.reactivestax;

import io.reactivestax.service.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.*;

public class TradeProcessingProducerRunner {

    public static void main(String[] args) {

        readPropertiesFile();
        configureHikariCP(getFileProperty("dbPortNum"), getFileProperty("dbName"));
        configureRabbitMQ(getFileProperty("rabbitMQ.hostName"), getFileProperty("rabbitMQ.guest"), getFileProperty("rabbitMQ.pass"));

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(new FileReaderRunner());
        executorService.submit(new ChunkProcessorRunner());

        executorService.shutdown();
    }
}

class FileReaderRunner implements Runnable{
    @Override
    public void run(){
        String folderPath = getFileProperty("resourcesFolderPath");
        TradesFileReader reader = new TradesFileReader();
        reader.readFileAndCreateChunks(folderPath+"/"+getFileProperty("dataFileName"), null);
    }
}
class ChunkProcessorRunner implements Runnable{
    @Override
    public void run(){
        ChunkProcessor chunkProcessor = new ChunkProcessor();
        chunkProcessor.startChunkProcessorPool();
    }
}
