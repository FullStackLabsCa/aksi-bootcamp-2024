package io.reactivestax.repo;

import io.reactivestax.model.Trade;
import io.reactivestax.utility.exceptions.OptimisticLockingOccurrence;
import io.reactivestax.utility.exceptions.PositionUpdateFailed;

public interface PositionsRepo {

    void updatePositionsTable(Trade trade) throws OptimisticLockingOccurrence, PositionUpdateFailed;

}
