package io.reactivestax.repo.hibernate;

import io.reactivestax.entity.RawPayload;
import io.reactivestax.model.Trade;
import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.utility.database.HibernateUtils;
import io.reactivestax.utility.exceptions.UpdateJournalEntryStatusInRawPayloadFailed;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;

import java.util.List;

public class HibernateRawPayloadRepo implements RawPayloadRepo {
    private static HibernateRawPayloadRepo instance;

    private HibernateRawPayloadRepo() {
        // Private Constructor to avoid anyone creating instance of this class
    }

    public static synchronized HibernateRawPayloadRepo getInstance() {
        if (instance == null) instance = new HibernateRawPayloadRepo();
        return instance;
    }

    @Override
    public void writeToRawPayloadTable(String tradeID, String payload, String validityStatus) {
        Session session = HibernateUtils.getInstance().getConnection();
        HibernateUtils.getInstance().startTransaction();
        try {
            RawPayload rawPayload = new RawPayload();
            rawPayload.setTradeID(tradeID);
            rawPayload.setStatus(validityStatus);
            rawPayload.setPayload(payload);
            rawPayload.setLookupStatus("Non Posted");
            rawPayload.setPostedStatus("Non Posted");

            session.persist(rawPayload);

            HibernateUtils.getInstance().commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            HibernateUtils.getInstance().rollbackTransaction();
        }
    }
}
