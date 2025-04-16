package io.reactivestax;

import io.reactivestax.service.ChunkProcessorService;

import static io.reactivestax.utility.ApplicationPropertyUtils.*;

public class TradeProducerAppRunner {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Shutdown hook triggered. Cleaning up...")));
        ChunkProcessorService.getInstance().processChunk(getFileProperty("dataFileName"));
    }
}

