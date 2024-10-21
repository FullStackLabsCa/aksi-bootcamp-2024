package io.reactivestax.repo.interfaces;

import io.reactivestax.model.Trade;
import io.reactivestax.utility.exceptions.OptimisticLockingException;

public interface PositionsRepo {

    void updatePositionsTable(Trade trade) throws OptimisticLockingException;
    int getVersionIdForPosition(Trade trade, int securityId);

}
