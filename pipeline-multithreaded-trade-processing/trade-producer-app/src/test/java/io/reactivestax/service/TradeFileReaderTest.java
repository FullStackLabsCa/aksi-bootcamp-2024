package io.reactivestax.service;

import io.reactivestax.TestDataProvider;
import io.reactivestax.utility.exceptions.FilepathProcessingException;
import io.reactivestax.utility.messaging.ChunksStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class TradeFileReaderTest {

    @Spy
    private TradesFileReader tradesFileReaderSpy;


    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void cleanUp(){
        ChunksStream.clearChunksQueue();
    }

    @ParameterizedTest
    @MethodSource("invalidFilePathTests")
    void readFileAndCreateChunksTest_InvalidFilePaths(String filePath){
        assertThrows(FilepathProcessingException.class, () -> tradesFileReaderSpy.readFileAndCreateChunks(filePath, ""));
    }

    static Stream<Arguments> invalidFilePathTests(){
        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of(TestDataProvider.invalidFilePathSupplier.get()) // Invalid
        );
    }

    @Test
    void readFileAndCreateChunksTest_NullFilePath(){
        assertThrows(FilepathProcessingException.class, () -> tradesFileReaderSpy.readFileAndCreateChunks(null, ""));
    }

    @ParameterizedTest
    @MethodSource("validFilesWithTradesMockedTests")
    void readFileAndCreateChunks_MockedTest_ValidFile(String filePath, int expectedHitsForChunkProduction){
        try(MockedStatic<ChunksStream> chunksStreamMockedStatic = Mockito.mockStatic(ChunksStream.class)) {
            tradesFileReaderSpy.readFileAndCreateChunks(filePath, "");
            chunksStreamMockedStatic.verify(() -> ChunksStream.produceChunkPath(any()), times(expectedHitsForChunkProduction));
        }
    }

    static Stream<Arguments> validFilesWithTradesMockedTests(){
        return Stream.of(
                Arguments.of(TestDataProvider.trades_0_FilePathSupplier.get(), 0),
                Arguments.of(TestDataProvider.trades_1_FilePathSupplier.get(), 1),
                Arguments.of(TestDataProvider.trades_800_FilePathSupplier.get(), 1),
                Arguments.of(TestDataProvider.trades_999_FilePathSupplier.get(), 1),
                Arguments.of(TestDataProvider.trades_1000_FilePathSupplier.get(), 1),
                Arguments.of(TestDataProvider.trades_1111_FilePathSupplier.get(), 2),
                Arguments.of(TestDataProvider.trades_9999_FilePathSupplier.get(), 10),
                Arguments.of(TestDataProvider.trades_10001_FilePathSupplier.get(), 11),
                Arguments.of(TestDataProvider.trades_10000_FilePathSupplier.get(), 10)
        );
    }

    @ParameterizedTest
    @MethodSource("validFilesWithTradesActualFileCreationTests")
    void readFileAndCreateChunks_ActualChunkCreationTest_ValidFile(String filePath, int expectedNumberOfChunks){
        TradesFileReader tradesFileReader = new TradesFileReader();
        tradesFileReader.readFileAndCreateChunks(filePath, "");
        assertEquals(readChunksInQueue(), expectedNumberOfChunks);
    }

    static Stream<Arguments> validFilesWithTradesActualFileCreationTests(){
        return Stream.of(
                Arguments.of(TestDataProvider.trades_0_FilePathSupplier.get(), 0),
                Arguments.of(TestDataProvider.trades_1_FilePathSupplier.get(), 1),
                Arguments.of(TestDataProvider.trades_800_FilePathSupplier.get(), 1),
                Arguments.of(TestDataProvider.trades_999_FilePathSupplier.get(), 1),
                Arguments.of(TestDataProvider.trades_1000_FilePathSupplier.get(), 1),
                Arguments.of(TestDataProvider.trades_1111_FilePathSupplier.get(), 2),
                Arguments.of(TestDataProvider.trades_9999_FilePathSupplier.get(), 10),
                Arguments.of(TestDataProvider.trades_10001_FilePathSupplier.get(), 11),
                Arguments.of(TestDataProvider.trades_10000_FilePathSupplier.get(), 10)
        );
    }

    int readChunksInQueue(){
        return ChunksStream.getNumberOfChunksAvailableForProcessing();
    }
}
