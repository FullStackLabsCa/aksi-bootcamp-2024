package io.reactivestax.repo.hibernate;

import io.reactivestax.model.Trade;
import io.reactivestax.utility.ApplicationPropertyUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;


class HibernateSecuritiesReferenceRepoTest {

    @BeforeEach
    void setUp(){
        ApplicationPropertyUtils.readPropertiesFile("src/test/resources/test.application.properties");
    }

    @AfterEach
    void cleanUp(){
        ApplicationPropertyUtils.resetProperties();
    }

    @Test
    void getInstanceSingleThreadTest() {
        // Get two instances
        HibernateSecuritiesReferenceRepo instance1 = HibernateSecuritiesReferenceRepo.getInstance();
        HibernateSecuritiesReferenceRepo instance2 = HibernateSecuritiesReferenceRepo.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<HibernateSecuritiesReferenceRepo> getInstance = HibernateSecuritiesReferenceRepo::getInstance;

        // Get two instances
        HibernateSecuritiesReferenceRepo instance1 = executorService.submit(getInstance).get();
        HibernateSecuritiesReferenceRepo instance2 = executorService.submit(getInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    void getSecurityIdTest(){
        int securityId = HibernateSecuritiesReferenceRepo.getInstance().getSecurityIdForCusip("Akshat");
        assertEquals(0, securityId);
    }

    @Test
    void checkValidCusip(){
        String validityStatus = HibernateSecuritiesReferenceRepo.getInstance().checkIfValidCusip(Trade.builder().build());
        assertEquals("", validityStatus);
    }
}
