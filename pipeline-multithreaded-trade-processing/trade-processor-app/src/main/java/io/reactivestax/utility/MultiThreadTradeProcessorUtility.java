package io.reactivestax.utility;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.rabbitmq.client.ConnectionFactory;

import io.reactivestax.utility.exceptions.HikariConnectionGetException;
import io.reactivestax.utility.exceptions.InvalidFilePathException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class MultiThreadTradeProcessorUtility {

    private MultiThreadTradeProcessorUtility() {
    }

    private static Properties fileProperties;
    static FileHandler fileHandler;
    private static final Logger logger = Logger.getLogger(MultiThreadTradeProcessorUtility.class.getName());
    private static HikariDataSource dataSource;
    private static ConnectionFactory rabbitMQFactory;
    private static Connection rabbitMQConnection;
    private static SessionFactory hibernateSessionFactory;
    private static AtomicInteger count = new AtomicInteger(0);

    public static void configureLogger(){
        try {
            fileHandler = new FileHandler(getFileProperty("errorLoggerFilePath"), true);
        } catch (IOException e) {
            throw new InvalidFilePathException("Unable to Access Error Log File Path.");
        }

        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.INFO);
    }

    public static void configureHikariCP(String portNum, String dbName) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:"+portNum+"/"+dbName);
        config.setUsername(getFileProperty("db.username"));
        config.setPassword(getFileProperty("db.password"));

        // Optional HikariCP settings
        config.setMaximumPoolSize(10); // Max 10 connections in the pool
        config.setMinimumIdle(5); // Minimum idle connections
        config.setConnectionTimeout(30000); // 30 seconds timeout for obtaining a connection
        config.setIdleTimeout(600000); // 10 minutes idle timeout

        dataSource = new HikariDataSource(config);
    }

    public static java.sql.Connection getConnectionFromHikariDataSource(){
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            System.out.println("Error Getting Connection from Hikari CP");
            throw new HikariConnectionGetException(e);
        }
    }

    public static void readPropertiesFile(){
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream("src/main/resources/application.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        fileProperties = properties;
    }

    public static String getFileProperty(String propertyName){
        return fileProperties.getProperty(propertyName);
    }

    public static void configureRabbitMQ(String host, String guest, String password){
        rabbitMQFactory = new ConnectionFactory();
        rabbitMQFactory.setHost(host); // Or the RabbitMQ server IP/hostname
        rabbitMQFactory.setUsername(guest); // RabbitMQ username
        rabbitMQFactory.setPassword(password); // RabbitMQ password
    }

    public static Channel getRabbitMQChannel(){
        try {
            if (rabbitMQConnection != null) return rabbitMQConnection.createChannel();
            else {
                rabbitMQConnection = rabbitMQFactory.newConnection();
                return rabbitMQConnection.createChannel();
            }
        } catch (Exception e) {
            System.out.println("Unable to provide Channel from the Rabbit MQ Connection...");
        }
        return null;
    }

    public static void configureHibernateSessionFactory(){
        hibernateSessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();
    }

    public static org.hibernate.Session getHibernateSessionFromFactory(){
        return hibernateSessionFactory.openSession();
    }
}
