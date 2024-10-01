package io.reactivestax.utility;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.reactivestax.service.TradeProcessorTask;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MultithreadTradeProcessorUtility {

    private MultithreadTradeProcessorUtility() {
    }

    static FileHandler fileHandler;
    public static final Logger logger = Logger.getLogger(MultithreadTradeProcessorUtility.class.getName());
    public static HikariDataSource dataSource;

    public static void configureLogger(){
        try {
            fileHandler = new FileHandler(readPropertiesFile().getProperty("errorLoggerFilePath"), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        config.setUsername(readPropertiesFile().getProperty("dbUsername"));
        config.setPassword(readPropertiesFile().getProperty("dbPassword"));

        // Optional HikariCP settings
        config.setMaximumPoolSize(10); // Max 10 connections in the pool
        config.setMinimumIdle(5); // Minimum idle connections
        config.setConnectionTimeout(30000); // 30 seconds timeout for obtaining a connection
        config.setIdleTimeout(600000); // 10 minutes idle timeout

        dataSource = new HikariDataSource(config);
    }

    public static Properties readPropertiesFile(){
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream("src/main/resources/application.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }


}
