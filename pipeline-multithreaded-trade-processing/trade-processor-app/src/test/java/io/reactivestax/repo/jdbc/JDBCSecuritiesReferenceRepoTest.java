package io.reactivestax.repo.jdbc;

import io.reactivestax.TestDataProvider;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JDBCSecuritiesReferenceRepoTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Test
    public void getInstanceSingleThreadTest() {
        // Get two instances
        JDBCSecuritiesReferenceRepo instance1 = JDBCSecuritiesReferenceRepo.getInstance();
        JDBCSecuritiesReferenceRepo instance2 = JDBCSecuritiesReferenceRepo.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<JDBCSecuritiesReferenceRepo> getInstance = JDBCSecuritiesReferenceRepo::getInstance;

        // Get two instances
        JDBCSecuritiesReferenceRepo instance1 = executorService.submit(getInstance).get();
        JDBCSecuritiesReferenceRepo instance2 = executorService.submit(getInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void checkIfValidForValidCusipTest(){
        assertEquals("Valid", JDBCSecuritiesReferenceRepo.getInstance().checkIfValidCusip(TestDataProvider.goodBuyTradeSupplier.get()));
    }

    @Test
    public void checkIfValidForInvalidCusipTest(){
        assertEquals("Invalid", JDBCSecuritiesReferenceRepo.getInstance().checkIfValidCusip(TestDataProvider.invalidCusipTradeSupplier.get()));
    }

    @Test
    public void checkIfValidCusipExceptionTest(){
        assertEquals("Unable to Check CUSIP.", JDBCSecuritiesReferenceRepo.getInstance().checkIfValidCusip(null));
    }

    @Test
    public void getSecurityIdForValidCusipTest(){
        assertEquals(157001093, JDBCSecuritiesReferenceRepo.getInstance().getSecurityIdForCusip("TSLA"));
    }

    @Test
    public void getSecurityIdForInvalidCusipTest(){
        System.setOut(new PrintStream(outputStreamCaptor));

        assertEquals(0, JDBCSecuritiesReferenceRepo.getInstance().getSecurityIdForCusip("Invalid"));
        assertTrue(outputStreamCaptor.toString().contains("Unable to get Security ID For the Given CUSIP."));

        System.setOut(originalOut);
    }

}
