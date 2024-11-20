package io.reactivestax.factory;

import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.repo.hibernate.HibernateRawPayloadRepo;
import io.reactivestax.repo.jdbc.JDBCRawPayloadRepo;
import io.reactivestax.service.interfaces.TradeIdAndAccNum;
import io.reactivestax.utility.ApplicationPropertyUtils;
import io.reactivestax.utility.exceptions.InvalidMessagingTechnologyException;
import io.reactivestax.utility.exceptions.InvalidPersistenceTechException;
import io.reactivestax.utility.messaging.MessageSender;
import io.reactivestax.utility.messaging.inmemory.InMemorySender;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static io.reactivestax.utility.ApplicationPropertyUtils.getFileProperty;
import static org.junit.jupiter.api.Assertions.*;

public class BeanFactoryTest {

    private void withMockedProperty(String propertyName, String value, Runnable testToExecute) {
        try (MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {
            mockedStatic.when(() -> getFileProperty(propertyName)).thenReturn(value);
            testToExecute.run();
        }
    }

    @ParameterizedTest
    @MethodSource("rawPayloadRepoTests")
    void testGetRawPayloadRepo(String persistenceTech, Class<?> expectedClass) {
        Runnable test = () -> {
            RawPayloadRepo rawPayloadRepoFromBeanFactory = BeanFactory.getRawPayloadRepo();
            RawPayloadRepo expectedRawPayloadRepo = expectedClass == JDBCRawPayloadRepo.class
                                                                    ? JDBCRawPayloadRepo.getInstance()
                                                                    : HibernateRawPayloadRepo.getInstance();
            // Check not Null
            assertNotNull(rawPayloadRepoFromBeanFactory);
            // Instance Verification
            assertInstanceOf(expectedClass, rawPayloadRepoFromBeanFactory);
            // Singleton Verification
            assertEquals(expectedRawPayloadRepo, rawPayloadRepoFromBeanFactory);
        };

        withMockedProperty("persistence.technology", persistenceTech, test);
    }

    static Stream<Arguments> rawPayloadRepoTests() {
        return Stream.of(
                Arguments.of("jdbc", JDBCRawPayloadRepo.class),
                Arguments.of("hibernate", HibernateRawPayloadRepo.class)
        );
    }

    @Test
    void testGetRawPayloadRepo_InvalidPersistenceTech() {
        try (MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {
            mockedStatic.when(() -> getFileProperty("persistence.technology")).thenReturn("invalidTech");

            assertThrows(InvalidPersistenceTechException.class, BeanFactory::getRawPayloadRepo);
        }
    }

    @Test
    void testGetMessageReceiver_RabbitMQ() {
        Runnable test = () -> {
            MessageSender<TradeIdAndAccNum> messageReceiver = BeanFactory.getMessageSender();
            MessageSender<TradeIdAndAccNum> rabbitMQReceiver = RabbitMQSender.getInstance();

            assertNotNull(messageReceiver);
            assertInstanceOf(RabbitMQSender.class, messageReceiver);
            // Singleton Verification
            assertEquals(messageReceiver, rabbitMQReceiver);
        };

        withMockedProperty("messaging.technology", "rabbitmq", test);
    }

    @Test
    void testGetMessageReceiver_InMemory() {
        Runnable test = () -> {
            MessageSender<TradeIdAndAccNum> messageSender = BeanFactory.getMessageSender();
            MessageSender<TradeIdAndAccNum> inMemorySender = InMemorySender.getInstance();

            assertNotNull(messageSender);
            assertInstanceOf(InMemorySender.class, messageSender);
            // Singleton Verification
            assertEquals(messageSender, inMemorySender);
        };
        withMockedProperty("messaging.technology", "in-memory", test);
    }

    @Test
    void testGetMessageReceiver_InvalidTech() {
        Runnable test = () -> assertThrows(InvalidMessagingTechnologyException.class, BeanFactory::getMessageSender);

        withMockedProperty("messaging.technology", "invalid", test);
    }
}
