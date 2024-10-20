package io.reactivestax.factory;

import io.reactivestax.repo.hibernate.HibernateJournalEntryRepo;
import io.reactivestax.repo.hibernate.HibernatePositionsRepo;
import io.reactivestax.repo.hibernate.HibernateRawPayloadRepo;
import io.reactivestax.repo.interfaces.JournalEntryRepo;
import io.reactivestax.repo.interfaces.PositionsRepo;
import io.reactivestax.repo.interfaces.RawPayloadRepo;
import io.reactivestax.repo.interfaces.SecuritiesReferenceRepo;
import io.reactivestax.repo.jdbc.JDBCJournalEntryRepo;
import io.reactivestax.repo.jdbc.JDBCPositionsRepo;
import io.reactivestax.repo.jdbc.JDBCRawPayloadRepo;
import io.reactivestax.repo.jdbc.JDBCSecuritiesReferenceRepo;
import io.reactivestax.utility.database.HibernateUtils;
import io.reactivestax.utility.database.JDBCUtils;
import io.reactivestax.utility.database.TransactionUtil;
import io.reactivestax.utility.exceptions.InvalidPersistenceTechException;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.getFileProperty;

public class BeanFactory {

    private static final String JDBC_PERSISTENCE_TECH = "jdbc";
    private static final String HIBERNATE_PERSISTENCE_TECH = "hibernate";

    public static TransactionUtil getTransactionUtil(){
        TransactionUtil transactionUtil;

        if(getFileProperty("persistence.technology").equals(JDBC_PERSISTENCE_TECH)){
            transactionUtil = JDBCUtils.getInstance();
        } else if (getFileProperty("persistence.technology").equals(HIBERNATE_PERSISTENCE_TECH)){
            transactionUtil = HibernateUtils.getInstance();
        } else {
            throw new InvalidPersistenceTechException();
        }

        return transactionUtil;
    }

    public static RawPayloadRepo getRawPayloadRepo() {
        RawPayloadRepo rawPayloadRepo;

        if(getFileProperty("persistence.technology").equals(JDBC_PERSISTENCE_TECH)){
            rawPayloadRepo = JDBCRawPayloadRepo.getInstance();
        } else if (getFileProperty("persistence.technology").equals(HIBERNATE_PERSISTENCE_TECH)){
            rawPayloadRepo = HibernateRawPayloadRepo.getInstance();
        } else {
            throw new InvalidPersistenceTechException();
        }

        return rawPayloadRepo;
    }

    public static JournalEntryRepo getJournalEntryRepo() {
        JournalEntryRepo journalEntryRepo;

        if(getFileProperty("persistence.technology").equals(JDBC_PERSISTENCE_TECH)){
            journalEntryRepo = JDBCJournalEntryRepo.getInstance();
        } else if (getFileProperty("persistence.technology").equals(HIBERNATE_PERSISTENCE_TECH)){
            journalEntryRepo = HibernateJournalEntryRepo.getInstance();
        } else {
            throw new InvalidPersistenceTechException();
        }

        return journalEntryRepo;
    }

    public static PositionsRepo getPositionsRepo() {
        PositionsRepo positionsRepo;

        if(getFileProperty("persistence.technology").equals(JDBC_PERSISTENCE_TECH)){
            positionsRepo = JDBCPositionsRepo.getInstance();
        } else if (getFileProperty("persistence.technology").equals(HIBERNATE_PERSISTENCE_TECH)){
            positionsRepo = HibernatePositionsRepo.getInstance();
        } else {
            throw new InvalidPersistenceTechException();
        }

        return positionsRepo;
    }

    public static SecuritiesReferenceRepo getSecuritiesReferenceRepo() {
        SecuritiesReferenceRepo securitiesReferenceRepo;

        securitiesReferenceRepo = JDBCSecuritiesReferenceRepo.getInstance();

        return securitiesReferenceRepo;
    }
}
