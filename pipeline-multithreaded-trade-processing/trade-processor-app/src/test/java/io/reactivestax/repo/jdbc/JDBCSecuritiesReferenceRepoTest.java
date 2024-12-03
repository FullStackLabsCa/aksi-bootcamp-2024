package io.reactivestax.repo.jdbc;

import io.reactivestax.TestDataProvider;
import io.reactivestax.utility.ApplicationPropertyUtils;
import io.reactivestax.utility.database.JDBCUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JDBCSecuritiesReferenceRepoTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private static final String CREATE_TABLE_SECURITIES_REFERENCE = """
            create table if not exists SecuritiesReferenceV2 (
                    cusip varchar(15) not null unique,
                    security_id int not null unique
            );""";

    private static final String POPULATE_TABLE_SECURITIES_REFERENCE = "insert into SecuritiesReferenceV2 (cusip, security_id) values ('TSLA', 157001093);";

    private static final String DELETE_FROM_SECURITIES_REFERENCE_V_2 = "delete from SecuritiesReferenceV2";

    @BeforeEach
    void setUp(){
        ApplicationPropertyUtils.readPropertiesFile("src/test/resources/test.application.properties");

        try (PreparedStatement createSecRefTableStmt = JDBCUtils.getInstance().getConnection().prepareStatement(CREATE_TABLE_SECURITIES_REFERENCE)) {

            JDBCUtils.getInstance().startTransaction();
            createSecRefTableStmt.executeUpdate();
            JDBCUtils.getInstance().commitTransaction();

        } catch (Exception e) {
            JDBCUtils.getInstance().rollbackTransaction();
        }

        try (PreparedStatement populateSecRefTableStmt = JDBCUtils.getInstance().getConnection().prepareStatement(POPULATE_TABLE_SECURITIES_REFERENCE)) {

            JDBCUtils.getInstance().startTransaction();
            populateSecRefTableStmt.executeUpdate();
            JDBCUtils.getInstance().commitTransaction();

        } catch (Exception e) {
            JDBCUtils.getInstance().rollbackTransaction();
        }
    }

    @AfterEach
    void cleanUp(){
        try (PreparedStatement dropSecRefTableStmt = JDBCUtils.getInstance().getConnection().prepareStatement(DELETE_FROM_SECURITIES_REFERENCE_V_2)) {
            JDBCUtils.getInstance().startTransaction();
            dropSecRefTableStmt.executeUpdate();
            JDBCUtils.getInstance().commitTransaction();
        } catch (Exception e) {
            JDBCUtils.getInstance().rollbackTransaction();
        }

        ApplicationPropertyUtils.resetProperties();
    }

    @Test
    void getInstanceSingleThreadTest() {
        // Get two instances
        JDBCSecuritiesReferenceRepo instance1 = JDBCSecuritiesReferenceRepo.getInstance();
        JDBCSecuritiesReferenceRepo instance2 = JDBCSecuritiesReferenceRepo.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
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
    void checkIfValidForValidCusipTest(){
        assertEquals("Valid", JDBCSecuritiesReferenceRepo.getInstance().checkIfValidCusip(TestDataProvider.goodBuyTradeSupplier.get()));
    }

    @Test
    void checkIfValidForInvalidCusipTest(){
        assertEquals("Invalid", JDBCSecuritiesReferenceRepo.getInstance().checkIfValidCusip(TestDataProvider.invalidCusipTradeSupplier.get()));
    }

    @Test
    void checkIfValidCusipExceptionTest(){
        assertEquals("Unable to Check CUSIP.", JDBCSecuritiesReferenceRepo.getInstance().checkIfValidCusip(null));
    }

    @Test
    void getSecurityIdForValidCusipTest(){
        assertEquals(157001093, JDBCSecuritiesReferenceRepo.getInstance().getSecurityIdForCusip("TSLA"));
    }

    @Test
    void getSecurityIdForInvalidCusipTest(){
        System.setOut(new PrintStream(outputStreamCaptor));

        assertEquals(0, JDBCSecuritiesReferenceRepo.getInstance().getSecurityIdForCusip("Invalid"));
        assertTrue(outputStreamCaptor.toString().contains("Unable to get Security ID For the Given CUSIP."));

        System.setOut(originalOut);
    }

}
