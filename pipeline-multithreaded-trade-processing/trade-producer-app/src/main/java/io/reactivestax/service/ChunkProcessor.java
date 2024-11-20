package io.reactivestax.service;

import io.reactivestax.utility.ApplicationPropertyUtils;
import io.reactivestax.utility.messaging.ChunksStream;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ChunkProcessor {

    int numberOfThreads = Integer.parseInt(ApplicationPropertyUtils.getFileProperty("thread.pool.size.chunk.processor"));
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

    public void startChunkProcessorPool() {

        Supplier<Optional<String>> getChunkPath = () -> {
            try {
                return ChunksStream.getRecentPostedChunkPath();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupted status
                return Optional.empty();
            }
        };

        Stream.generate(getChunkPath)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(String.class::cast)
                .forEach(path -> executorService.submit(() -> ChunkProcessorService.getInstance().processChunk(path)));

        executorService.shutdown();
    }

}

//@NoArgsConstructor
//class ChunkProcessorRunnable implements Runnable {
//    String chunkPath;
//
//    public ChunkProcessorRunnable(String chunkPath) {
//        this.chunkPath = chunkPath;
//    }
//    ChunkProcessorService chunkProcessorService = ChunkProcessorService.getInstance();
//
//    @Override
//    public void run() {
//        chunkProcessorService.processChunk(this.chunkPath);
//    }
//}