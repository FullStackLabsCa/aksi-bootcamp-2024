package io.reactivestax.repo.interfaces;

import io.reactivestax.model.Trade;

public interface PositionsRepo {

    void updatePositionsTable(Trade trade);
    int getVersionIdForPosition(Trade trade, int securityId);

}
