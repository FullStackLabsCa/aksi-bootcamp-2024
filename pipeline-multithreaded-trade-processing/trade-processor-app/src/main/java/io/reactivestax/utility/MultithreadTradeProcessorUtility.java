package io.reactivestax.utility;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import multithread_trade_processing.service.TradeProcessorTask;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MultithreadTradeProcessorUtility {
    static FileHandler fileHandler;
    public static final Logger logger = Logger.getLogger(TradeProcessorTask.class.getName());
    public static HikariDataSource dataSource;

    public static void configureLogger(){
        {
            try {
                fileHandler = new FileHandler("error.txt", true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.INFO);
    }

    public static void configureHikariCP(int portNum) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:"+portNum+"/bootcamp");
        config.setUsername("root");
        config.setPassword("password123");

        // Optional HikariCP settings
        config.setMaximumPoolSize(10); // Max 10 connections in the pool
        config.setMinimumIdle(5); // Minimum idle connections
        config.setConnectionTimeout(30000); // 30 seconds timeout for obtaining a connection
        config.setIdleTimeout(600000); // 10 minutes idle timeout

        dataSource = new HikariDataSource(config);
    }


}
