package io.reactivestax.service;

import java.util.concurrent.LinkedBlockingDeque;

public class ChunksStream {
    private static final LinkedBlockingDeque<String> chunksPaths = new LinkedBlockingDeque<>();

    public static String getRecentPostedChunkPath() {
        String chunkPath = "";

        try {
            chunkPath = chunksPaths.take();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        return chunkPath;
    }

    public static void produceChunkPath(String chunkPath) {
        try {
            chunksPaths.put(chunkPath);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
