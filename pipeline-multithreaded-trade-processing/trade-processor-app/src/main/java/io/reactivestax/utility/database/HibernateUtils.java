package io.reactivestax.utility.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import static io.reactivestax.utility.ApplicationPropertyUtils.getFileProperty;

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
        String hibernateConfigFile = "hibernate.cfg.xml";
        sessionFactory = getConfiguration()
                .configure(hibernateConfigFile)
                .buildSessionFactory();
    }

    private static Configuration getConfiguration(){
        return new Configuration()
                .setProperty("hibernate.connection.url", getFileProperty("db.url"))
                .setProperty("hibernate.connection.username", getFileProperty("db.username"))
                .setProperty("hibernate.connection.password", getFileProperty("db.password"))
                .setProperty("hibernate.hbm2ddl.auto", getFileProperty("db.hibernate.mode"))
                .setProperty("hibernate.connection.driver_class", getFileProperty("db.hibernate.driver.class"))
                .setProperty("hibernate.dialect", getFileProperty("db.hibernate.dialect"));
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
