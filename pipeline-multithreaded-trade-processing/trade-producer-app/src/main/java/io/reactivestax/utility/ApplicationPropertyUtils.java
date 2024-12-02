package io.reactivestax.utility;

import io.reactivestax.utility.exceptions.SystemInitializationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationPropertyUtils {

    private ApplicationPropertyUtils() {
    }

    private static Properties fileProperties;

    private static void readPropertiesFile(){
        Properties properties = new Properties();

        try (InputStream fis = ApplicationPropertyUtils.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new SystemInitializationException("Failed to read the properties file. Check the path");
        }

        fileProperties = properties;
    }

    public static void readPropertiesFile(String filePath){
        Properties properties = new Properties();

        try (InputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new SystemInitializationException("Failed to read the properties file. Check the path");
        }

        fileProperties = properties;
    }

    public static String getFileProperty(String propertyName){
        if(fileProperties == null) readPropertiesFile();
        String propertyReadFromApplicationProperties;
        try {
            propertyReadFromApplicationProperties = fileProperties.getProperty(propertyName);
        } catch (NullPointerException e) {
            throw new SystemInitializationException("Null Property");
        }
        if(propertyReadFromApplicationProperties == null || propertyReadFromApplicationProperties.trim().isEmpty())
            throw new SystemInitializationException("Failed to Read Property from the application Properties");
        else return fileProperties.getProperty(propertyName);
    }

}
