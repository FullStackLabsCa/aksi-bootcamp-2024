package io.reactivestax.utility;

import io.reactivestax.utility.exceptions.SystemInitializationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ApplicationPropertyUtils {

    private ApplicationPropertyUtils() {
    }

    private static Properties fileProperties;

    private static void readPropertiesFile(){
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream("src/main/resources/application.properties")) { // TODO
            properties.load(fis);
        } catch (IOException e) {
            System.out.println(e.getMessage());
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
