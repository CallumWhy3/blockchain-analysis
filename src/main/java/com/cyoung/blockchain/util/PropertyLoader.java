package com.cyoung.blockchain.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader {

    public static String LoadProperty(String propertyName){
        Properties properties = new Properties();
        InputStream input = null;
        String propertyValue = "";

        try {
            input = PropertyLoader.class.getClassLoader().getResourceAsStream("config.properties");
            properties.load(input);
            propertyValue = properties.getProperty(propertyName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {

                }
            }
        }
        return propertyValue;
    }
}
