package io.reactivestax.service;

import org.junit.jupiter.api.Test;

public class ChunkProcessorTest {

//startChunkProcessorPoolTest
    @Test
    void startChunkProcessorPoolMockedTest_NoChunkToProcess(){
        // Verify Repeated Calls to getRecentPostedChunkPath
        // executorService.submit never called
        // chunkProcessorRunnable.run() never hit
        // executorService.shutdown called
    }

    @Test
    void startChunkProcessorPoolMockedTest_ChunkAvailableToProcess(){
        // Verify Repeated Calls to getRecentPostedChunkPath
        // executorService.submit called
        // chunkProcessorRunnable.run() hit
        // executorService.shutdown called
    }

    @Test
    void startChunkProcessorPoolTest_getChunkPathThrowsException(){
        // getRecentPostedChunkPath throws Interrupt exception
        // submit never called
        // runnable never called
        // shutdown called
    }

    @Test
    void startChunkProcessorPoolIntegrationTest_ChunkAvailableToProcess(){
        // TODO
    }
}
