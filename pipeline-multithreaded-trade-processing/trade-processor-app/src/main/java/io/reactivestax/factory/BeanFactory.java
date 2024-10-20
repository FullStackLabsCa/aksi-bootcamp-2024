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


    public static TransactionUtil getTransactionUtil(){
        TransactionUtil transactionUtil;

        if(getFileProperty("persistence.technology").equals("jdbc")){
            transactionUtil = JDBCUtils.getInstance();
        } else if (getFileProperty("persistence.technology").equals("hibernate")){
            transactionUtil = HibernateUtils.getInstance();
        } else {
            throw new InvalidPersistenceTechException("Invalid persistence technology");
        }

        return transactionUtil;
    }

    public static RawPayloadRepo getRawPayloadRepo() {
        RawPayloadRepo rawPayloadRepo;

        if(getFileProperty("persistence.technology").equals("jdbc")){
            rawPayloadRepo = JDBCRawPayloadRepo.getInstance();
        } else if (getFileProperty("persistence.technology").equals("hibernate")){
            rawPayloadRepo = HibernateRawPayloadRepo.getInstance();
        } else {
            throw new InvalidPersistenceTechException("Invalid persistence technology");
        }

        return rawPayloadRepo;
    }

    public static JournalEntryRepo getJournalEntryRepo() {
        JournalEntryRepo journalEntryRepo;

        if(getFileProperty("persistence.technology").equals("jdbc")){
            journalEntryRepo = JDBCJournalEntryRepo.getInstance();
        } else if (getFileProperty("persistence.technology").equals("hibernate")){
            journalEntryRepo = HibernateJournalEntryRepo.getInstance();
        } else {
            throw new InvalidPersistenceTechException("Invalid persistence technology");
        }

        return journalEntryRepo;
    }

    public static PositionsRepo getPositionsRepo() {
        PositionsRepo positionsRepo;

        if(getFileProperty("persistence.technology").equals("jdbc")){
            positionsRepo = JDBCPositionsRepo.getInstance();
        } else if (getFileProperty("persistence.technology").equals("hibernate")){
            positionsRepo = HibernatePositionsRepo.getInstance();
        } else {
            throw new InvalidPersistenceTechException("Invalid persistence technology");
        }

        return positionsRepo;
    }

    public static SecuritiesReferenceRepo getSecuritiesReferenceRepo() {
        SecuritiesReferenceRepo securitiesReferenceRepo;

//        if(getFileProperty("persistence.technology").equals("jdbc")){
            securitiesReferenceRepo = JDBCSecuritiesReferenceRepo.getInstance();
//        } else if (getFileProperty("persistence.technology").equals("hibernate")){
//            securitiesReferenceRepo = HibernateSecuritiesReferenceRepo.getInstance();
//        } else {
//            throw new InvalidPersistenceTechException("Invalid persistence technology");
//        }

        return securitiesReferenceRepo;
    }
}
