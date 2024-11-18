package io.reactivestax.utility.database;

import io.reactivestax.utility.ApplicationPropertyUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtils implements ConnectionUtil<Session>, TransactionUtil {
    private static HibernateUtils instance;
    private static SessionFactory sessionFactory;
    private static final ThreadLocal<Session> sessionHolder = new ThreadLocal<>();

    private HibernateUtils() {
        // Private Constructor to Avoid Instance creation for this Class
    }

    public static synchronized HibernateUtils getInstance(){
        if(instance == null) instance = new HibernateUtils();
        return instance;
    }

    @Override
    public Session getConnection() {
        Session session = sessionHolder.get();
        if(session == null) {
            session = getSessionFactory().openSession();
            sessionHolder.set(session);
        }
        return session;
    }

    private static SessionFactory getSessionFactory(){
        if(sessionFactory == null) configureHibernateSessionFactory();
        return sessionFactory;
    }

    private static void configureHibernateSessionFactory(){
        String hibernateConfigFile;
        if("true".equals(ApplicationPropertyUtils.getFileProperty("test.mode"))){
            hibernateConfigFile = "hibernate_testing.cfg.xml";
        } else {
            hibernateConfigFile = "hibernate.cfg.xml";
        }
        sessionFactory = new Configuration()
                .configure(hibernateConfigFile)
                .buildSessionFactory();
    }

    @Override
    public void startTransaction() {
        if(!getConnection().getTransaction().isActive())
            getConnection().beginTransaction();
    }

    @Override
    public void commitTransaction() {
        getConnection().getTransaction().commit();
        closeConnection();
    }

    @Override
    public void rollbackTransaction() {
        getConnection().getTransaction().rollback();
        closeConnection();
    }

    private void closeConnection(){
        getConnection().close();
        sessionHolder.remove();
    }
}
