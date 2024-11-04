package io.reactivestax.utility;

import org.junit.Test;

import static io.reactivestax.utility.ApplicationPropertyUtils.getFileProperty;
import static org.junit.Assert.assertEquals;

public class ApplicationPropertiesUtilsTest {

    @Test
    public void testGetFileProperty(){
        assertEquals("credit_card_transactions",getFileProperty("rabbitMQ.main.exchange.name"));
    }
}
