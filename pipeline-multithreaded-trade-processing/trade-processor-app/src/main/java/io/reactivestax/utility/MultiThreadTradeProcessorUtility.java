package io.reactivestax.utility;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.rabbitmq.client.ConnectionFactory;

import io.reactivestax.utility.exceptions.InvalidFilePathException;

public class MultiThreadTradeProcessorUtility {

    private MultiThreadTradeProcessorUtility() {
    }

    private static Properties fileProperties;
    static FileHandler fileHandler;
    private static final Logger logger = Logger.getLogger(MultiThreadTradeProcessorUtility.class.getName());
    private static ConnectionFactory rabbitMQFactory;
    private static Connection rabbitMQConnection;

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

}
