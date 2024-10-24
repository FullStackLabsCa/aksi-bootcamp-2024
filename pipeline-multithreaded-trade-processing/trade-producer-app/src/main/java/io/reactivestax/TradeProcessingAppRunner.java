package io.reactivestax;

import io.reactivestax.service.ChunkProcessor;
import io.reactivestax.service.TradesFileReader;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.*;

public class TradeProcessingAppRunner {

    public static void main(String[] args) {

        (new Thread(new FileReaderRunner())).start();
        (new Thread(new ChunkProcessorRunner())).start();

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
