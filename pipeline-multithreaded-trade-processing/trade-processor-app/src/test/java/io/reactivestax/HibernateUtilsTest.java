package io.reactivestax;

import io.reactivestax.entity.Position;
import io.reactivestax.entity.PositionCompositeKey;
import io.reactivestax.utility.database.HibernateUtils;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.*;

public class HibernateUtilsTest {

    @Test
    public void getInstanceSingleThreadTest(){
        // Get two instances
        HibernateUtils instance1 = HibernateUtils.getInstance();
        HibernateUtils instance2 = HibernateUtils.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<HibernateUtils> getHibernateUtilInstance = HibernateUtils::getInstance;

        // Get two instances
        HibernateUtils instance1 = executorService.submit(getHibernateUtilInstance).get();
        HibernateUtils instance2 = executorService.submit(getHibernateUtilInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getConnectionMultiThreadTest() throws ExecutionException, InterruptedException {
        // Spawn multiple threads and make each of them get 2 connections
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<List<Session>> getSession = () -> {
            Session session1 = HibernateUtils.getInstance().getConnection();
            Session session2 = HibernateUtils.getInstance().getConnection();
            List<Session> listOfSession = new ArrayList<>();
            listOfSession.add(session1);
            listOfSession.add(session2);
            return listOfSession;
        };

        List<Session> thread1sessions = executorService.submit(getSession).get();
        List<Session> thread2sessions = executorService.submit(getSession).get();

        // Both the connections for the same thread will be same
        assertEquals(thread1sessions.get(0).hashCode(), thread1sessions.get(1).hashCode());
        assertEquals(System.identityHashCode(thread1sessions.get(0)), System.identityHashCode(thread1sessions.get(1)));
        assertEquals(thread2sessions.get(0).hashCode(), thread2sessions.get(1).hashCode());
        assertEquals(System.identityHashCode(thread2sessions.get(0)), System.identityHashCode(thread2sessions.get(1)));

        // Two connections from any two different threads will be different
        assertNotEquals(thread1sessions.get(0).hashCode(), thread2sessions.get(0).hashCode());
        assertNotEquals(System.identityHashCode(thread1sessions.get(0)), System.identityHashCode(thread2sessions.get(0)));
        assertNotEquals(thread1sessions.get(1).hashCode(), thread2sessions.get(1).hashCode());
        assertNotEquals(System.identityHashCode(thread1sessions.get(1)), System.identityHashCode(thread2sessions.get(1)));
    }

    @Test
    public void startTransactionSingleTest(){
        Transaction transaction;

        Session session = HibernateUtils.getInstance().getConnection();
        HibernateUtils.getInstance().startTransaction();
        transaction = session.getTransaction();
        assertNotNull(transaction);
    }

    @Test
    public void startTransactionMultiThreadTest() throws ExecutionException, InterruptedException {
        Transaction transactionThread1;
        Transaction transactionThread2;

        Callable<Transaction> startTransaction = () -> {
            HibernateUtils.getInstance().startTransaction();
            return HibernateUtils.getInstance().getConnection().getTransaction();
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<Transaction> futureThread1 = executorService.submit(startTransaction);
        Future<Transaction> futureThread2 = executorService.submit(startTransaction);

        transactionThread1 = futureThread1.get();
        transactionThread2 = futureThread2.get();

        assertNotNull(transactionThread1);
        assertNotNull(transactionThread2);
        assertNotEquals(transactionThread1, transactionThread2);
    }

    @Test
    public void commitTransactionTableSizeTest(){
        // Check that the size of the table will increase by the number of insertions
        Session session = HibernateUtils.getInstance().getConnection();
        HibernateUtils.getInstance().startTransaction();

        String hql = "Select count(p) from Position p";
        Query query = session.createQuery(hql, Long.class);
        Long sizeBeforeCommitting = (Long) query.getSingleResult();

        Position position = new Position();
        position.setPositionAmount(100);
        position.setVersion(0);
        position.setPositionID(new PositionCompositeKey("AkshatSingla", 33));
        session.persist(position);
        HibernateUtils.getInstance().commitTransaction();

        Query queryWithNewSession = HibernateUtils.getInstance().getConnection().createQuery(hql, Long.class);
        Long sizeAfterCommitting = (Long) queryWithNewSession.getSingleResult();

        assertEquals((long) sizeBeforeCommitting + 1, (long) sizeAfterCommitting);
    }

    @Test
    public void commitTransactionTableDataTest(){
        // Check if the inserted data exists in the DB
        Session session = HibernateUtils.getInstance().getConnection();
        HibernateUtils.getInstance().startTransaction();

        String hql = "from Position";
        Query query = session.createQuery(hql, Position.class);
        List<Position> positionsBeforeCommitting = query.getResultList();

        assertTrue(positionsBeforeCommitting.isEmpty());

        Position position = new Position();
        position.setPositionAmount(100);
        position.setVersion(0);
        position.setPositionID(new PositionCompositeKey("AkshatSingla", 33));
        session.persist(position);
        HibernateUtils.getInstance().commitTransaction();

        Query queryWithNewSession = HibernateUtils.getInstance().getConnection().createQuery(hql, Long.class);
        List<Position> positionsAfterCommitting = queryWithNewSession.getResultList();

        assertEquals(1, positionsAfterCommitting.size());
        assertEquals(positionsAfterCommitting.get(0), position);
    }

    @Test
    public void rollbackTransactionTableSizeTest(){
        // Should be the same as before
    }

    @Test
    public void rollbackTransactionTableDataTest(){
        // Should be the same as before
    }
}
