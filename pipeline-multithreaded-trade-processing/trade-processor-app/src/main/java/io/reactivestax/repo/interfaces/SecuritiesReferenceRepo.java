package io.reactivestax.repo.interfaces;

import io.reactivestax.model.Trade;

public interface SecuritiesReferenceRepo {

    String checkIfValidCusip(Trade trade);
    int getSecurityIdForCusip(String cusip);

}
