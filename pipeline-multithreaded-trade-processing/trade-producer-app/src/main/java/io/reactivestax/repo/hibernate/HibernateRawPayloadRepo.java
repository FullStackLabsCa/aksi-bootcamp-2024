package io.reactivestax.repo.hibernate;

import io.reactivestax.entity.RawPayload;
import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.utility.database.HibernateUtils;
import org.hibernate.Session;

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
    public void writeToRawPayloadTable(RawPayload rawPayload) {
        Session session = HibernateUtils.getInstance().getConnection();
        try {
            HibernateUtils.getInstance().startTransaction();
            session.persist(rawPayload);
            HibernateUtils.getInstance().commitTransaction();
        } catch (Exception e) {
            HibernateUtils.getInstance().rollbackTransaction();
        }
    }
}
