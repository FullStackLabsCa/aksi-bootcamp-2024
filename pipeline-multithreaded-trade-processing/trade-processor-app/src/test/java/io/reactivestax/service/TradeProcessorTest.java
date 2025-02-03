package io.reactivestax.service;

import io.reactivestax.utility.ApplicationPropertyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static io.reactivestax.utility.ApplicationPropertyUtils.getFileProperty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class TradeProcessorTest {

    @Spy
    private ExecutorService executorServiceSpy;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @MethodSource("tradeProcessingPoolTest")
    void startTradeProcessingTest(int numberOfProviders){
        try(MockedStatic<ApplicationPropertyUtils> applicationPropertyUtilsMockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class);
            MockedStatic<Executors> executorsMockedStatic = Mockito.mockStatic(Executors.class)){

            //SetUp
            applicationPropertyUtilsMockedStatic.when(() -> getFileProperty("trade.processor.provider.count")).thenReturn(String.valueOf(numberOfProviders));
            applicationPropertyUtilsMockedStatic.when(() -> getFileProperty("thread.pool.size.trade.processor")).thenReturn(String.valueOf(3));
            executorsMockedStatic.when(() -> Executors.newFixedThreadPool(anyInt())).thenReturn(executorServiceSpy);

            //Action
            (new TradeProcessor()).startTradesProcessing();

            //Assert
            verify(executorServiceSpy, times(numberOfProviders)).submit(any(TradeProcessorRunnable.class));
            verify(executorServiceSpy, times(1)).shutdown();
        }
    }

    static Stream<Arguments> tradeProcessingPoolTest(){
        return Stream.of(
                Arguments.of(0),
                Arguments.of(1),
                Arguments.of(5)
        );
    }
}
