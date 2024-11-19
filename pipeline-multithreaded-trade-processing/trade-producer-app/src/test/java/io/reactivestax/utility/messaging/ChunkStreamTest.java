package io.reactivestax.utility.messaging;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChunkStreamTest {

    @AfterEach
    void cleanUp(){
        ChunksStream.clearChunksQueue();
    }

    @Test
    void getRecentPostedChunkPathTest_Invalid() throws InterruptedException {
        Optional<String> recentPostedChunkPath = ChunksStream.getRecentPostedChunkPath();
        assertEquals(Optional.empty(), recentPostedChunkPath);
    }

    @Test
    void getRecentPostedChunkPathTest_Valid() throws InterruptedException {
        ChunksStream.produceChunkPath("testChunkPath");
        Optional<String> recentPostedChunkPath = ChunksStream.getRecentPostedChunkPath();
        assertEquals("testChunkPath", recentPostedChunkPath.get());
    }

    @Test
    void getAvailableChunksTest(){
        ChunksStream.produceChunkPath("testChunkPath1");
        ChunksStream.produceChunkPath("testChunkPath2");
        ChunksStream.produceChunkPath("testChunkPath3");

        int numberOfChunksAvailableForProcessing = ChunksStream.getNumberOfChunksAvailableForProcessing();
        assertEquals(3, numberOfChunksAvailableForProcessing);
    }

    @Test
    void clearChunksQueueTest(){
        ChunksStream.produceChunkPath("testChunkPath1");
        ChunksStream.produceChunkPath("testChunkPath2");
        ChunksStream.produceChunkPath("testChunkPath3");

        ChunksStream.clearChunksQueue();
        int numberOfChunksAvailableForProcessing = ChunksStream.getNumberOfChunksAvailableForProcessing();
        assertEquals(0, numberOfChunksAvailableForProcessing);
    }

}
