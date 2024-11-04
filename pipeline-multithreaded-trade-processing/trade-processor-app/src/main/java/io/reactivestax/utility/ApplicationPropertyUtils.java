package io.reactivestax.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ApplicationPropertyUtils {

    private ApplicationPropertyUtils() {
    }

    private static Properties fileProperties;

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
