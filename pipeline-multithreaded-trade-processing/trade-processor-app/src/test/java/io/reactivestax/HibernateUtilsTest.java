package io.reactivestax;

import io.reactivestax.utility.database.HibernateUtils;
import org.hibernate.Session;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
    public void getConnectionTest() throws ExecutionException, InterruptedException {
        // Spawn multiple threads and make each of them get 2 connections
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<List<Session>> getConnection = () -> {
            Session session1 = HibernateUtils.getInstance().getConnection();
            Session session2 = HibernateUtils.getInstance().getConnection();
            List<Session> listOfSession = new ArrayList<>();
            listOfSession.add(session1);
            listOfSession.add(session2);
            return listOfSession;
        };

        List<Session> thread1sessions = executorService.submit(getConnection).get();
        List<Session> thread2sessions = executorService.submit(getConnection).get();

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
}
