package io.reactivestax.repo;

import io.reactivestax.model.Trade;
import io.reactivestax.utility.exceptions.OptimisticLockingExceptionThrowable;

public interface PositionsRepo {

    void updatePositionsTable(Trade trade) throws OptimisticLockingExceptionThrowable;

}
