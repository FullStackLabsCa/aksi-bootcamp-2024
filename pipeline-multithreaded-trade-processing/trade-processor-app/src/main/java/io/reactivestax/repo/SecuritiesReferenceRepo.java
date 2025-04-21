package io.reactivestax.repo;

import io.reactivestax.model.Trade;

public interface SecuritiesReferenceRepo {

    int checkIfValidCusip(Trade trade);
    int getSecurityIdForCusip(String cusip);

}
