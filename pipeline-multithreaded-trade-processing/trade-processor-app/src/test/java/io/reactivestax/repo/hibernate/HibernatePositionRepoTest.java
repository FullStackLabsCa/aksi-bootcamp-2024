package io.reactivestax.repo.hibernate;

import io.reactivestax.TestDataProvider;
import io.reactivestax.model.Trade;
import io.reactivestax.utility.database.HibernateUtils;
import io.reactivestax.utility.exceptions.OptimisticLockingExceptionThrowable;
import org.hibernate.query.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class HibernatePositionRepoTest {

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @After
    public void cleanUp(){
        try {
            HibernateUtils.getInstance().startTransaction();
            String sql = "delete from Position";
            jakarta.persistence.Query query = HibernateUtils.getInstance().getConnection().createQuery(sql);
            query.executeUpdate();
            HibernateUtils.getInstance().commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            HibernateUtils.getInstance().rollbackTransaction();
        }
    }


    @Test
    public void getInstanceSingleThreadTest() {
        // Get two instances
        HibernatePositionsRepo instance1 = HibernatePositionsRepo.getInstance();
        HibernatePositionsRepo instance2 = HibernatePositionsRepo.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<HibernatePositionsRepo> getInstance = HibernatePositionsRepo::getInstance;

        // Get two instances
        HibernatePositionsRepo instance1 = executorService.submit(getInstance).get();
        HibernatePositionsRepo instance2 = executorService.submit(getInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getVersionIdTradeNeverInsertedTest(){
        Trade trade = TestDataProvider.goodTradeSupplier.get();
        int version = HibernatePositionsRepo.getInstance().getVersionIdForPosition(trade, 157001093);
        assertEquals(-1, version);
    }

    @Test
    public void getVersionIdTradeAfterUpdateTest() throws OptimisticLockingExceptionThrowable {
        // Setup
        Trade trade = TestDataProvider.goodTradeSupplier.get();
        int version = HibernatePositionsRepo.getInstance().getVersionIdForPosition(trade, 157001093);

        // Action
        HibernateUtils.getInstance().startTransaction();
        HibernatePositionsRepo.getInstance().updatePositionsTable(trade);
        HibernateUtils.getInstance().commitTransaction();
        int versionAfterUpdate = HibernatePositionsRepo.getInstance().getVersionIdForPosition(trade, 157001093);

        // Assert
        assertEquals(version + 1, versionAfterUpdate);
    }

    /*
    Update Position Table Test
    --  If Version Id == -1 -> Position Does not exist
        -- Insert the Position Into the position Table
            -- Check the Position Table Size Before and After
            -- After = before + 1
            -- Check data as well
        -- Test for Buy and Sell # TODO 1, 2
        -- Test for InvalidActivity # TODO 3
    -- If Version ID == 0 -> Position Exist
        -- Update the Position
            -- The Size before and After will be same
            -- Position Amount needs to be verified before and after
        -- Verify position Update for Buy and Sell # TODO 4, 5
        -- Test for Invalid Activity as well # TODO 6
        -- Hit OptimisticExceptionThrowable # TODO 8
    -- Some Exception Occurs during the Update
        -- Exception Occurrence when Update  # TODO 7
     */

    private void checkSizeOfTable(){
        String hql = "SELECT COUNT(e) FROM Position e";
        Query<Long> query = HibernateUtils.getInstance().getConnection().createQuery(hql, Long.class);
        long tableSize = query.uniqueResult();
        System.out.println("tableSize = " + tableSize);
    }
}
