package io.reactivestax.utility.messaging;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class ChunksStream {
    private static final LinkedBlockingDeque<String> chunksPaths = new LinkedBlockingDeque<>();

    private ChunksStream() {
    }

    public static Optional<String> getRecentPostedChunkPath() throws InterruptedException {
        return Optional.ofNullable(chunksPaths.poll(15, TimeUnit.SECONDS));
    }

    public static void produceChunkPath(String chunkPath) {
        try {
            chunksPaths.put(chunkPath);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public static int getNumberOfChunksAvailableForProcessing(){
        return chunksPaths.size();
    }

    public static void clearChunksQueue(){
        chunksPaths.clear();
    }
}
