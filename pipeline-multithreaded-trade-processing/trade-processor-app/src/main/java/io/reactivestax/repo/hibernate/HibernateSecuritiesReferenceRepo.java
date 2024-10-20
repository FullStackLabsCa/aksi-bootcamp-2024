package io.reactivestax.repo.hibernate;

import io.reactivestax.model.Trade;
import io.reactivestax.repo.interfaces.SecuritiesReferenceRepo;

public class HibernateSecuritiesReferenceRepo implements SecuritiesReferenceRepo {
    private HibernateJournalEntryRepo instance;

    private HibernateJournalEntryRepo(){
        // Private Constructor to avoid anyone creating instance of this class
    }

    public HibernateJournalEntryRepo getInstance(){
        if(instance == null) instance = new HibernateJournalEntryRepo();
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
