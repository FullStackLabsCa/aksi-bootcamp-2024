package io.reactivestax.repo;

import io.reactivestax.model.Trade;
import io.reactivestax.utility.exceptions.OptimisticLockingException;

public interface PositionsRepo {

    void updatePositionsTable(Trade trade) throws OptimisticLockingException;

}
