package io.reactivestax.service;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class ChunksStream {
    private static final LinkedBlockingDeque<String> chunksPaths = new LinkedBlockingDeque<>();

    private ChunksStream() {
    }

    public static String getRecentPostedChunkPath() {
        String chunkPath = "";

        try {
            chunkPath = chunksPaths.poll(60, TimeUnit.SECONDS);
            if (chunkPath == null ) return null;
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            Thread.currentThread().interrupt();
        }

        return chunkPath;
    }

    public static void produceChunkPath(String chunkPath) {
        try {
            chunksPaths.put(chunkPath);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
