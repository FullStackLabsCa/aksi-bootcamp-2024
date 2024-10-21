package io.reactivestax.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import io.reactivestax.utility.exceptions.InvalidFilePathException;

public class MultiThreadTradeProcessorUtility {

    private MultiThreadTradeProcessorUtility() {
    }

    private static Properties fileProperties;
    static FileHandler fileHandler;
    private static final Logger logger = Logger.getLogger(MultiThreadTradeProcessorUtility.class.getName());

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
        if(fileProperties == null) readPropertiesFile();
        return fileProperties.getProperty(propertyName);
    }

}
