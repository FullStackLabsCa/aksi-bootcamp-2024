package io.reactivestax;

import io.reactivestax.service.ChunkProcessor;
import io.reactivestax.service.TradesFileReader;

import static io.reactivestax.utility.ApplicationPropertyUtils.*;

public class TradeProducerAppRunner {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Shutdown hook triggered. Cleaning up...")));
        (new Thread(new FileReaderRunner())).start();
        (new Thread(new ChunkProcessorRunner())).start();

    }
}

class FileReaderRunner implements Runnable{
    TradesFileReader reader = new TradesFileReader();

    @Override
    public void run(){
        reader.readFileAndCreateChunks(getFileProperty("dataFileName"), null);
    }
}

class ChunkProcessorRunner implements Runnable{
    ChunkProcessor chunkProcessor = new ChunkProcessor();

    @Override
    public void run(){
        chunkProcessor.startChunkProcessorPool();
    }
}
