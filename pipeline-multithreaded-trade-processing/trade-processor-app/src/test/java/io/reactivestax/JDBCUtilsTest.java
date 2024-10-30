package io.reactivestax;

import io.reactivestax.utility.database.JDBCUtils;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.*;

public class JDBCUtilsTest {

    @Before
    public void cleanUp(){
        String sql = "delete from Position";
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
        Callable<Boolean> startTransactionAndGetAutoCommit = () -> {
            JDBCUtils.getInstance().startTransaction();
            return JDBCUtils.getInstance().getConnection().getAutoCommit();
        };

        Callable<Boolean> commitTransactionAndGetAutoCommit = () -> {
            JDBCUtils.getInstance().commitTransaction();
            return JDBCUtils.getInstance().getConnection().getAutoCommit();
        };

        boolean autocommitThread1 = startTransactionAndGetAutoCommit.call();
        boolean autocommitThread2 = startTransactionAndGetAutoCommit.call();

        assertFalse(autocommitThread1);
        assertFalse(autocommitThread2);

        autocommitThread1 = commitTransactionAndGetAutoCommit.call();
        assertTrue(autocommitThread1);
        assertFalse(autocommitThread2);

        autocommitThread2 = commitTransactionAndGetAutoCommit.call();
        assertTrue(autocommitThread1);
        assertTrue(autocommitThread2);

    }

//    @Test
//    public void commitTransactionTableSizeTest(){
//        // Check that the size of the table will increase by the number of insertions
//        Connection connection = JDBCUtils.getInstance().getConnection();
//        String countSql = "Select count(p) from Position p";
//        String insertSql = "Insert into positions (account_number, security_id, position, version) values (AkshatSingla,333,303,0)";
//
//        int sizeBeforeCommitting = 0, sizeAfterCommitting = 0;
//        try(PreparedStatement ps = connection.prepareStatement(countSql);
//        PreparedStatement psInsert = connection.prepareStatement(insertSql)) {
//            ResultSet sizeBeforeCommittingRs = ps.executeQuery();
//            sizeBeforeCommitting = sizeBeforeCommittingRs.getInt("count(p)");
//
//            JDBCUtils.getInstance().startTransaction();
//            psInsert.executeUpdate();
//            JDBCUtils.getInstance().commitTransaction();
//
//        } catch (Exception e){
//            System.out.println("Error with getting sizeBeforeCommit / Inserting into the Positions table...");
//        }
//
//        Connection connection1 = JDBCUtils.getInstance().getConnection();
//        try(PreparedStatement psCount = connection1.prepareStatement(countSql)) {
//            ResultSet sizeAfterCommittingRs = psCount.executeQuery();
//            sizeAfterCommitting = sizeAfterCommittingRs.getInt("count(p)");
//        } catch (Exception e) {
//            System.out.println("Error with getting sizeAfterCommit");
//        }
//        assertEquals(sizeBeforeCommitting + 1,sizeAfterCommitting);
//
//    }
//
//    @Test
//    public void commitTransactionTableDataTest(){
//        // Check if the inserted data exists in the DB
//        Session session = JDBCUtils.getInstance().getConnection();
//        JDBCUtils.getInstance().startTransaction();
//
//        String hql = "from Position";
//        Query query = session.createQuery(hql, Position.class);
//        List<Position> positionsBeforeCommitting = query.getResultList();
//
//        assertTrue(positionsBeforeCommitting.isEmpty());
//
//        Position position = new Position();
//        position.setPositionAmount(100);
//        position.setVersion(0);
//        position.setPositionID(new PositionCompositeKey("AkshatSingla", 33));
//        session.persist(position);
//        JDBCUtils.getInstance().commitTransaction();
//
//        Query queryWithNewSession = JDBCUtils.getInstance().getConnection().createQuery(hql, Long.class);
//        List<Position> positionsAfterCommitting = queryWithNewSession.getResultList();
//
//        assertEquals(1, positionsAfterCommitting.size());
//        assertEquals(positionsAfterCommitting.get(0), position);
//    }
//
//    @Test
//    public void rollbackTransactionTableSizeTest(){
//        // Should be the same as before
//        Session session = JDBCUtils.getInstance().getConnection();
//        JDBCUtils.getInstance().startTransaction();
//
//        String hql = "Select count(p) from Position p";
//        Query query = session.createQuery(hql, Long.class);
//        Long sizeBeforeCommitting = (Long) query.getSingleResult();
//
//        Position position = new Position();
//        position.setPositionAmount(100);
//        position.setVersion(0);
//        position.setPositionID(new PositionCompositeKey("AkshatSingla", 33));
//        session.persist(position);
//        JDBCUtils.getInstance().rollbackTransaction();
//
//        Query queryWithNewSession = JDBCUtils.getInstance().getConnection().createQuery(hql, Long.class);
//        Long sizeAfterCommitting = (Long) queryWithNewSession.getSingleResult();
//
//        assertEquals((long) sizeBeforeCommitting, (long) sizeAfterCommitting);
//    }
//
//    @Test
//    public void rollbackTransactionTableDataTest(){
//        // Should be the same as before
//        Session session = JDBCUtils.getInstance().getConnection();
//        JDBCUtils.getInstance().startTransaction();
//
//        String hql = "from Position";
//        Query query = session.createQuery(hql, Position.class);
//        List<Position> positionsBeforeCommitting = query.getResultList();
//
//        assertTrue(positionsBeforeCommitting.isEmpty());
//
//        Position position = new Position();
//        position.setPositionAmount(100);
//        position.setVersion(0);
//        position.setPositionID(new PositionCompositeKey("AkshatSingla", 33));
//        session.persist(position);
//        JDBCUtils.getInstance().rollbackTransaction();
//
//        Query queryWithNewSession = JDBCUtils.getInstance().getConnection().createQuery(hql, Long.class);
//        List<Position> positionsAfterCommitting = queryWithNewSession.getResultList();
//
//        assertEquals(0, positionsAfterCommitting.size());
//    }
}
