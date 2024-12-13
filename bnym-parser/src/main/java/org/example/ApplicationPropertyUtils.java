package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationPropertyUtils {

    private ApplicationPropertyUtils() {
    }

    private static Properties fileProperties;

    private static synchronized void readPropertiesFile(){
        Properties properties = new Properties();

        try (InputStream fis = ApplicationPropertyUtils.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the properties file. Check the path");
        }

        fileProperties = properties;
    }

    public static String getFileProperty(String propertyName){
        if(fileProperties == null) readPropertiesFile();
        String propertyReadFromApplicationProperties;
        try {
            propertyReadFromApplicationProperties = fileProperties.getProperty(propertyName);
        } catch (NullPointerException e) {
            throw new RuntimeException("Null Property");
        }
        if(propertyReadFromApplicationProperties == null)
            throw new RuntimeException("Failed to Read Property from the application Properties");
        else return fileProperties.getProperty(propertyName);
    }

}
