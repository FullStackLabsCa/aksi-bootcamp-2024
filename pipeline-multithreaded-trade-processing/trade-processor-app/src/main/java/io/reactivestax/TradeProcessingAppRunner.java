package io.reactivestax;

import io.reactivestax.service.ChunkProcessor;
import io.reactivestax.service.TradeProcessor;
import io.reactivestax.service.TradesFileReader;
import io.reactivestax.utility.exceptions.InvalidAppModeException;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.*;

public class TradeProcessingAppRunner {

    public static void main(String[] args) {

        if(getFileProperty("trading.app.mode").equals("producer")) {
            (new Thread(new FileReaderRunner())).start();
            (new Thread(new ChunkProcessorRunner())).start();
        } else if (getFileProperty("trading.app.mode").equals("consumer")) {
            (new Thread(new TradeProcessorRunner())).start();
        } else {
            throw new InvalidAppModeException("App Mode Invalid.. Please Check the Properties File");
        }

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

class TradeProcessorRunner implements Runnable{
    @Override
    public void run(){
        TradeProcessor processor = new TradeProcessor();
        processor.startTradeProcessingFromQueues();
    }
}
