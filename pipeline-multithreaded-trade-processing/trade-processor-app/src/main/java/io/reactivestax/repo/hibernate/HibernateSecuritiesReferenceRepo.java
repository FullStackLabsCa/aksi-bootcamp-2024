package io.reactivestax.repo.hibernate;

import io.reactivestax.model.Trade;
import io.reactivestax.repo.interfaces.SecuritiesReferenceRepo;

public class HibernateSecuritiesReferenceRepo implements SecuritiesReferenceRepo {
    private HibernateSecuritiesReferenceRepo instance;

    private HibernateSecuritiesReferenceRepo(){
        // Private Constructor to avoid anyone creating instance of this class
    }

    public HibernateSecuritiesReferenceRepo getInstance(){
        if(instance == null) instance = new HibernateSecuritiesReferenceRepo();
        return instance;
    }

    @Override
    public String checkIfValidCusip(Trade trade) {
        return "";
    }

    @Override
    public int getSecurityIdForCusip(String cusip) {
        return 0;
    }
}
