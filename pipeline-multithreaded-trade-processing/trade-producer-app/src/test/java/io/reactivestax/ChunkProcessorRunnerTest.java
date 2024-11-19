package io.reactivestax;

import io.reactivestax.service.ChunkProcessor;
import io.reactivestax.service.TradesFileReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//class TradeProducerAppRunnerTest {
//
//}

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
    void runTest(){
        doNothing().when(chunkProcessorSpy).startChunkProcessorPool();
        chunkProcessorRunner.run();
        verify(chunkProcessorSpy, times(1)).startChunkProcessorPool();
    }
}