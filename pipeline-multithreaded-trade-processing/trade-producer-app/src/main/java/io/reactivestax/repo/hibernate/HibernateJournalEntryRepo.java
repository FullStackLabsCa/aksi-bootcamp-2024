package io.reactivestax.repo.hibernate;

import io.reactivestax.entity.JournalEntry;
import io.reactivestax.model.Trade;
import io.reactivestax.repo.JournalEntryRepo;
import io.reactivestax.repo.jdbc.JDBCSecuritiesReferenceRepo;
import io.reactivestax.utility.database.HibernateUtils;
import io.reactivestax.utility.exceptions.PositionUpdateForJournalEntryFailed;
import io.reactivestax.utility.exceptions.WriteToJournalEntryFailed;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;

public class HibernateJournalEntryRepo implements JournalEntryRepo {
    private static HibernateJournalEntryRepo instance;

    private HibernateJournalEntryRepo(){
        // Private Constructor to avoid anyone creating instance of this class
    }

    public static synchronized HibernateJournalEntryRepo getInstance(){
        if(instance == null) instance = new HibernateJournalEntryRepo();
        return instance;
    }

    @Override
    public void writeTradeToJournalEntryTable(Trade trade) throws WriteToJournalEntryFailed {
        Session session = HibernateUtils.getInstance().getConnection();
        JDBCSecuritiesReferenceRepo securitiesReference = JDBCSecuritiesReferenceRepo.getInstance();
        try {
            JournalEntry journalEntry = new JournalEntry();
            journalEntry.setAccountNumber(trade.getAccountNumber());
            journalEntry.setActivity(trade.getActivity());
            journalEntry.setPositionPostedStatus("Non Posted");
            journalEntry.setQuantity(trade.getQuantity());
            journalEntry.setSecurityID(securitiesReference.getSecurityIdForCusip(trade.getCusip()));
            journalEntry.setTradeExecutionTime(trade.getTransactionTime());
            journalEntry.setTradeID(trade.getTradeID());

            session.persist(journalEntry);
        } catch (Exception e) {
            throw new WriteToJournalEntryFailed();
        }
    }

    @Override
    public void updateJournalEntryForPositionUpdateStatus(Trade trade) throws PositionUpdateForJournalEntryFailed {
        Session session = HibernateUtils.getInstance().getConnection();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaUpdate<JournalEntry> positionStatusUpdate = builder.createCriteriaUpdate(JournalEntry.class);
            Root<JournalEntry> root = positionStatusUpdate.from(JournalEntry.class);
            positionStatusUpdate.set(root.get("positionPostedStatus"), "Posted");
            positionStatusUpdate.where(builder.equal(root.get("tradeID"), trade.getTradeID()));

            session.createQuery(positionStatusUpdate).executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new PositionUpdateForJournalEntryFailed();
        }
    }
}
