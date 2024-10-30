package io.reactivestax;

import io.reactivestax.utility.database.JDBCUtils;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.*;

public class JDBCUtilsTest {

    @Before
    public void cleanUp(){
        String sql = "delete from positions";
        Connection connection = JDBCUtils.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            JDBCUtils.getInstance().startTransaction();
            ps.executeUpdate();
            JDBCUtils.getInstance().commitTransaction();
        } catch (Exception e) {
            JDBCUtils.getInstance().rollbackTransaction();
        }

    }

    @Test
    public void getInstanceSingleThreadTest(){
        // Get two instances
        JDBCUtils instance1 = JDBCUtils.getInstance();
        JDBCUtils instance2 = JDBCUtils.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<JDBCUtils> getHibernateUtilInstance = JDBCUtils::getInstance;

        // Get two instances
        JDBCUtils instance1 = executorService.submit(getHibernateUtilInstance).get();
        JDBCUtils instance2 = executorService.submit(getHibernateUtilInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getConnectionMultiThreadTest() throws ExecutionException, InterruptedException {
        // Spawn multiple threads and make each of them get 2 connections
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<List<Connection>> getConnection = () -> {
            Connection connection1 = JDBCUtils.getInstance().getConnection();
            Connection connection2 = JDBCUtils.getInstance().getConnection();
            List<Connection> listOfConnection = new ArrayList<>();
            listOfConnection.add(connection1);
            listOfConnection.add(connection2);
            return listOfConnection;
        };

        List<Connection> thread1Connections = executorService.submit(getConnection).get();
        List<Connection> thread2Connections = executorService.submit(getConnection).get();

        // Both the connections for the same thread will be same
        assertEquals(thread1Connections.get(0).hashCode(), thread1Connections.get(1).hashCode());
        assertEquals(System.identityHashCode(thread1Connections.get(0)), System.identityHashCode(thread1Connections.get(1)));
        assertEquals(thread2Connections.get(0).hashCode(), thread2Connections.get(1).hashCode());
        assertEquals(System.identityHashCode(thread2Connections.get(0)), System.identityHashCode(thread2Connections.get(1)));

        // Two connections from any two different threads will be different
        assertNotEquals(thread1Connections.get(0).hashCode(), thread2Connections.get(0).hashCode());
        assertNotEquals(System.identityHashCode(thread1Connections.get(0)), System.identityHashCode(thread2Connections.get(0)));
        assertNotEquals(thread1Connections.get(1).hashCode(), thread2Connections.get(1).hashCode());
        assertNotEquals(System.identityHashCode(thread1Connections.get(1)), System.identityHashCode(thread2Connections.get(1)));
    }

    @Test
    public void startTransactionSingleTest() throws SQLException {
        boolean autocommit;

        Connection connection = JDBCUtils.getInstance().getConnection();
        JDBCUtils.getInstance().startTransaction();
        autocommit = connection.getAutoCommit();
        assertFalse(autocommit);
    }

    @Test
    public void startTransactionMultiThreadTest() throws ExecutionException, InterruptedException {
        boolean autocommitThread1;
        boolean autocommitThread2;

        Callable<Boolean> startTransaction = () -> {
            JDBCUtils.getInstance().startTransaction();
            return JDBCUtils.getInstance().getConnection().getAutoCommit();
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<Boolean> futureThread1 = executorService.submit(startTransaction);
        Future<Boolean> futureThread2 = executorService.submit(startTransaction);

        autocommitThread1 = futureThread1.get();
        autocommitThread2 = futureThread2.get();

        assertFalse(autocommitThread1);
        assertFalse(autocommitThread2);
    }

    @Test
    public void commitTransactionSingleThreadAutocommitTest() throws SQLException {
        JDBCUtils.getInstance().startTransaction();
        assertFalse(JDBCUtils.getInstance().getConnection().getAutoCommit());
        JDBCUtils.getInstance().commitTransaction();
        assertTrue(JDBCUtils.getInstance().getConnection().getAutoCommit());
    }

    @Test
    public void commitTransactionMultiThreadAutocommitTest() throws Exception {
        Callable<Boolean> commitTransactionAndGetAutoCommit = () -> {
            JDBCUtils.getInstance().startTransaction();
            JDBCUtils.getInstance().commitTransaction();
            return JDBCUtils.getInstance().getConnection().getAutoCommit();
        };

        FutureTask<Boolean> futureTaskThread1 = new FutureTask<>(commitTransactionAndGetAutoCommit);
        FutureTask<Boolean> futureTaskThread2 = new FutureTask<>(commitTransactionAndGetAutoCommit);

        new Thread(futureTaskThread1).start();
        new Thread(futureTaskThread2).start();

        boolean autocommitThread1 = futureTaskThread1.get();
        boolean autocommitThread2 = futureTaskThread2.get();

        assertTrue(autocommitThread1);
        assertTrue(autocommitThread2);

    }

    @Test
    public void commitTransactionTableSizeTest(){
        // Check that the size of the table will increase by the number of insertions
        Connection connection = JDBCUtils.getInstance().getConnection();
        String countSql = "Select count(*) as count from positions";
        String insertSql = "Insert into positions (account_number, security_id, position, version) values ('AkshatSingla',333,303,0)";

        int sizeBeforeCommitting = 0, sizeAfterCommitting = 0;
        try(PreparedStatement ps = connection.prepareStatement(countSql);
            PreparedStatement psInsert = connection.prepareStatement(insertSql)) {
            ResultSet sizeBeforeCommittingRs = ps.executeQuery();
            if(sizeBeforeCommittingRs.next()) sizeBeforeCommitting = sizeBeforeCommittingRs.getInt("count");

            JDBCUtils.getInstance().startTransaction();
            psInsert.executeUpdate();
            JDBCUtils.getInstance().commitTransaction();

        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Error with getting sizeBeforeCommit / Inserting into the Positions table...");
        }

        Connection connection1 = JDBCUtils.getInstance().getConnection();
        try(PreparedStatement psCount = connection1.prepareStatement(countSql)) {
            ResultSet sizeAfterCommittingRs = psCount.executeQuery();
            if (sizeAfterCommittingRs.next()) sizeAfterCommitting = sizeAfterCommittingRs.getInt("count");
        } catch (Exception e) {
            System.out.println("Error with getting sizeAfterCommit");
        }
        assertEquals(sizeBeforeCommitting + 1,sizeAfterCommitting);

    }

    @Test
    public void commitTransactionTableDataTest(){
        String selectSql = "select * from positions";
        String insertSql = "Insert into positions (account_number, security_id, position, version) values ('AkshatSingla',333,303,0)";

        // Check if the inserted data exists in the DB
        Connection connection = JDBCUtils.getInstance().getConnection();

        try(PreparedStatement psSelect = connection.prepareStatement(selectSql);
            PreparedStatement psInsert = connection.prepareStatement(insertSql)) {

            boolean isEmpty;
            ResultSet selectRs = psSelect.executeQuery();
            isEmpty = selectRs.next();

            assertFalse(isEmpty);

            JDBCUtils.getInstance().startTransaction();
            psInsert.executeUpdate();
            JDBCUtils.getInstance().commitTransaction();

        } catch (Exception e) {
            System.out.println("Unable to Insert into DB....");
        }


        String accountNumber = "";
        int securityId = 0, position = 0, version = 0;

        Connection connection1 = JDBCUtils.getInstance().getConnection();
        try(PreparedStatement psCount = connection1.prepareStatement(selectSql)) {
            ResultSet afterCommittingRs = psCount.executeQuery();
            while (afterCommittingRs.next()) {
                accountNumber = afterCommittingRs.getString("account_number");
                securityId = afterCommittingRs.getInt("security_id");
                position = afterCommittingRs.getInt("position");
                version = afterCommittingRs.getInt("version");
            }


        } catch (Exception e) {
            System.out.println("Error with getting resultAfterCommit");
        }

        assertEquals("AkshatSingla", accountNumber);
        assertEquals(333, securityId);
        assertEquals(303, position);
        assertEquals(0, version);
    }

    @Test
    public void rollbackTransactionSingleThreadAutocommitTest() throws SQLException {
        JDBCUtils.getInstance().startTransaction();
        assertFalse(JDBCUtils.getInstance().getConnection().getAutoCommit());
        JDBCUtils.getInstance().rollbackTransaction();
        assertTrue(JDBCUtils.getInstance().getConnection().getAutoCommit());
    }

    @Test
    public void rollbackTransactionMultiThreadAutocommitTest() throws Exception {
        Callable<Boolean> rollbackTransactionAndGetAutoCommit = () -> {
            JDBCUtils.getInstance().startTransaction();
            JDBCUtils.getInstance().rollbackTransaction();
            return JDBCUtils.getInstance().getConnection().getAutoCommit();
        };

        FutureTask<Boolean> futureTaskThread1 = new FutureTask<>(rollbackTransactionAndGetAutoCommit);
        FutureTask<Boolean> futureTaskThread2 = new FutureTask<>(rollbackTransactionAndGetAutoCommit);

        new Thread(futureTaskThread1).start();
        new Thread(futureTaskThread2).start();

        boolean autocommitThread1 = futureTaskThread1.get();
        boolean autocommitThread2 = futureTaskThread2.get();

        assertTrue(autocommitThread1);
        assertTrue(autocommitThread2);

    }

    @Test
    public void rollbackTransactionTableSizeTest(){
        // Check that the size of the table will increase by the number of insertions
        Connection connection = JDBCUtils.getInstance().getConnection();
        String countSql = "Select count(*) as count from positions";
        String insertSql = "Insert into positions (account_number, security_id, position, version) values ('AkshatSingla',333,303,0)";

        int sizeBeforeCommitting = 0, sizeAfterCommitting = 0;
        try(PreparedStatement ps = connection.prepareStatement(countSql);
            PreparedStatement psInsert = connection.prepareStatement(insertSql)) {
            ResultSet sizeBeforeCommittingRs = ps.executeQuery();
            if(sizeBeforeCommittingRs.next()) sizeBeforeCommitting = sizeBeforeCommittingRs.getInt("count");

            JDBCUtils.getInstance().startTransaction();
            psInsert.executeUpdate();
            JDBCUtils.getInstance().rollbackTransaction();

        } catch (Exception e){
            System.out.println("Error with getting sizeBeforeCommit / Inserting into the Positions table...");
        }

        Connection connection1 = JDBCUtils.getInstance().getConnection();
        try(PreparedStatement psCount = connection1.prepareStatement(countSql)) {
            ResultSet sizeAfterCommittingRs = psCount.executeQuery();
            if (sizeAfterCommittingRs.next()) sizeAfterCommitting = sizeAfterCommittingRs.getInt("count");
        } catch (Exception e) {
            System.out.println("Error with getting sizeAfterCommit");
        }
        assertEquals(sizeBeforeCommitting,sizeAfterCommitting);

    }

}
