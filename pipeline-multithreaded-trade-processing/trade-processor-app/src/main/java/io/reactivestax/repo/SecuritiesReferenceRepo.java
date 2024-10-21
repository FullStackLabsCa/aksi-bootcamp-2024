package io.reactivestax.repo;

import io.reactivestax.model.Trade;

public interface SecuritiesReferenceRepo {

    String checkIfValidCusip(Trade trade);
    int getSecurityIdForCusip(String cusip);

}
