package io.reactivestax;

import io.reactivestax.service.ChunkProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChunkProcessorRunnerTest {

    @Spy
    private ChunkProcessor chunkProcessorSpy;

    @Spy
    @InjectMocks
    private ChunkProcessorRunner chunkProcessorRunner;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void runMockedTest(){
        doNothing().when(chunkProcessorSpy).startChunkProcessorPool();
        chunkProcessorRunner.run();
        verify(chunkProcessorSpy, times(1)).startChunkProcessorPool();
    }

    @Test
    void runIntegrationTest(){
        // TODO
    }

}