package io.reactivestax.utility;


import io.reactivestax.utility.exceptions.SystemInitializationException;
import org.junit.jupiter.api.Test;

import static io.reactivestax.utility.ApplicationPropertyUtils.getFileProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationPropertiesUtilsTest {

    @Test
    void testGetFileProperty(){
        assertEquals("credit_card_transactions",getFileProperty("rabbitMQ.main.exchange.name"));
    }

    @Test
    void getPropertyTest_NullProperty(){
        assertThrows(SystemInitializationException.class, () -> ApplicationPropertyUtils.getFileProperty(null));
    }

    @Test
    void getPropertyTest_EmptyProperty(){
        assertThrows(SystemInitializationException.class, () -> ApplicationPropertyUtils.getFileProperty(""));
    }

    @Test
    void getPropertyTest_EmptyV2Property(){
        assertThrows(SystemInitializationException.class, () -> ApplicationPropertyUtils.getFileProperty("    "));
    }

    @Test
    void getPropertyTest_NonExistingProperty(){
        assertThrows(SystemInitializationException.class, () -> ApplicationPropertyUtils.getFileProperty(""));
    }
}
