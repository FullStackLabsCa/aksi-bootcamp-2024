package io.reactivestax.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChunkProcessorRunnableTest {

    @Spy
    private ChunkProcessorService chunkProcessorServiceSpy;

    @Spy
    @InjectMocks
    private ChunkProcessorRunnable chunkProcessorRunnable;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void runMockedTest(){
        doNothing().when(chunkProcessorServiceSpy).processChunk(any());
        chunkProcessorRunnable.run();
        verify(chunkProcessorServiceSpy, times(1)).processChunk(any());

    }

    @Test
    void runIntegrationTest(){
        // TODO
    }

}
