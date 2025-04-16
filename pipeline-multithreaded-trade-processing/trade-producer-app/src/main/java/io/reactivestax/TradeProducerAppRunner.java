package io.reactivestax;

import io.reactivestax.service.FileProcessorService;

import static io.reactivestax.utility.ApplicationPropertyUtils.*;

public class TradeProducerAppRunner {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Shutdown hook triggered. Cleaning up...")));
        FileProcessorService.getInstance().processChunk(getFileProperty("dataFileName"));
    }
}

