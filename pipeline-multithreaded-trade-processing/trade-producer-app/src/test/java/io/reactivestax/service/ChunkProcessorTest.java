package io.reactivestax.service;

import io.reactivestax.utility.messaging.ChunksStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChunkProcessorTest {

    @Spy
    private ExecutorService executorServiceSpy;

    @Spy
    @InjectMocks
    private ChunkProcessor chunkProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void cleanUp() {
        ChunksStream.clearChunksQueue();
    }

    //startChunkProcessorPoolTest
    @Test
    void startChunkProcessorPoolMockedTest_NoChunkToProcess() {
        // executorService.submit never called
        // chunkProcessorRunnable.run() never hit
        Thread testThread = new Thread(chunkProcessor::startChunkProcessorPool);
        testThread.start();
        verify(executorServiceSpy, timeout(5000).times(0)).submit(any(Runnable.class));
        testThread.interrupt();
    }

    @Test
    void startChunkProcessorPoolMockedTest_ChunkAvailableToProcess() {
        // executorService.submit called
        // chunkProcessorRunnable.run() hit
        ChunksStream.produceChunkPath("filePath");
        Thread testThread = new Thread(chunkProcessor::startChunkProcessorPool);
        testThread.start();
        verify(executorServiceSpy, timeout(5000).times(1)).submit(any(Runnable.class));
        testThread.interrupt();
    }

    @Test
    void startChunkProcessorPoolTest_getChunkPathThrowsException() {
        try (MockedStatic<ChunksStream> chunksStreamMockedStatic = Mockito.mockStatic(ChunksStream.class)) {
            chunksStreamMockedStatic.when(ChunksStream::getRecentPostedChunkPath).thenThrow(InterruptedException.class);
            Thread testThread = new Thread(chunkProcessor::startChunkProcessorPool);
            testThread.start();
            verify(executorServiceSpy, timeout(5000).times(0)).submit(any(Runnable.class));
            testThread.interrupt();
        }
    }

    @Test
    void startChunkProcessorPoolIntegrationTest_ChunkAvailableToProcess() {
        // TODO
    }
}
