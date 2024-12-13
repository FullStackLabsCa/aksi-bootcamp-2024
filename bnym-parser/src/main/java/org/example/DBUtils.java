package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import static org.example.ApplicationPropertyUtils.getFileProperty;

public class DBUtils {
    private static DBUtils instance;
    private SessionFactory sessionFactory;

    private DBUtils() {
        // Private Constructor to Avoid Instance creation for this Class
    }

    public static synchronized DBUtils getInstance(){
        if(instance == null) instance = new DBUtils();
        return instance;
    }

    public Session getConnection() {
        return getSessionFactory().openSession();
    }

    private synchronized SessionFactory getSessionFactory(){
        if(sessionFactory == null) configureHibernateSessionFactory();
        return sessionFactory;
    }

    private void configureHibernateSessionFactory(){
        String hibernateConfigFile = "hibernate.cfg.xml";
        sessionFactory = getConfiguration()
                .configure(hibernateConfigFile)
                .buildSessionFactory();
    }

    private Configuration getConfiguration(){
        return new Configuration()
                .setProperty("hibernate.connection.url", getFileProperty("db.url"))
                .setProperty("hibernate.connection.username", getFileProperty("db.username"))
                .setProperty("hibernate.connection.password", getFileProperty("db.password"))
                .setProperty("hibernate.hbm2ddl.auto", getFileProperty("db.hibernate.mode"))
                .setProperty("hibernate.connection.driver_class", getFileProperty("db.hibernate.driver.class"))
                .setProperty("hibernate.dialect", getFileProperty("db.hibernate.dialect"));
    }

    public void startTransaction() {
        if(!getConnection().getTransaction().isActive())
            getConnection().beginTransaction();
    }

    public void commitTransaction() {
        getConnection().getTransaction().commit();
        closeConnection();
    }

    public void rollbackTransaction() {
        getConnection().getTransaction().rollback();
        closeConnection();
    }

    private void closeConnection(){
        getConnection().close();
    }
}
