package io.reactivestax.repo.hibernate;

import io.reactivestax.entity.RawPayload;
import io.reactivestax.model.Trade;
import io.reactivestax.repo.interfaces.RawPayloadRepo;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class HibernateRawPayloadRepo implements RawPayloadRepo {
    private HibernateRawPayloadRepo instance;

    private HibernateRawPayloadRepo(){
        // Private Constructor to avoid anyone creating instance of this class
    }

    public HibernateRawPayloadRepo getInstance(){
        if(instance == null) instance = new HibernateRawPayloadRepo();
        return instance;
    }

    @Override
    public void writeToRawPayloadTable(String tradeID, String payload, String validityStatus) {
        try {
            RawPayload rawPayload = new RawPayload();
            rawPayload.setTradeID(tradeID);
            rawPayload.setStatus(validityStatus);
            rawPayload.setPayload(payload);
            rawPayload.setLookupStatus("Non Posted");
            rawPayload.setPostedStatus("Non Posted");
            Transaction transaction;
            try {
                transaction = session.beginTransaction();
            } catch (Exception e) {
                transaction = session.getTransaction();
            }
            session.persist(rawPayload);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String readPayloadFromRawPayloadsTable(String tradeID) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<RawPayload> query = builder.createQuery(RawPayload.class);
        Root<RawPayload> root = query.from(RawPayload.class);
        query.select(root).where(builder.equal(root.get("tradeID"), tradeID));
        List<RawPayload> students = session.createQuery(query).getResultList();

        if (students != null) return students.get(0).getPayload();
        else return null;
    }

    @Override
    public void updateSecurityLookupStatusInRawPayloadsTable(Trade trade, String lookupStatus) {
        Transaction transaction;
        try {
            transaction = session.beginTransaction();
        } catch (Exception e) {
            transaction = session.getTransaction();
        }
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaUpdate<RawPayload> criteriaUpdate = builder.createCriteriaUpdate(RawPayload.class);
            Root<RawPayload> root = criteriaUpdate.from(RawPayload.class);
            if (lookupStatus.equals("Valid")) {
                criteriaUpdate.set(root.get("lookupStatus"), "Succeeded");
            } else {
                criteriaUpdate.set(root.get("lookupStatus"), "Failed");
            }
            criteriaUpdate.where(builder.equal(root.get("tradeID"), trade.getTradeID()));
            session.createQuery(criteriaUpdate).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateJournalEntryStatusInRawPayloadsTable(Trade trade) {
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaUpdate<RawPayload> jeStatusUpdate = builder.createCriteriaUpdate(RawPayload.class);
            Root<RawPayload> root = jeStatusUpdate.from(RawPayload.class);
            jeStatusUpdate.set(root.get("postedStatus"), "Posted");
            jeStatusUpdate.where(builder.equal(root.get("tradeID"), trade.getTradeID()));

            session.createQuery(jeStatusUpdate).executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
