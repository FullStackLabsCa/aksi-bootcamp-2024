package io.reactivestax;

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

@ExtendWith(MockitoExtension.class)
public class FileReaderRunnerTest{

    @Spy
    private TradesFileReader tradesFileReaderSpy;

    @Spy
    @InjectMocks
    private FileReaderRunner fileReaderRunner;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void runTest(){
        doNothing().when(tradesFileReaderSpy).readFileAndCreateChunks(any(), any());
        fileReaderRunner.run();
        verify(tradesFileReaderSpy, times(1)).readFileAndCreateChunks(any(), any());
    }
}