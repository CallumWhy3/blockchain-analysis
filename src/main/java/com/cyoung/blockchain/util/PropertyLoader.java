package com.cyoung.blockchain.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader {

    /**
     * Load value of property stored in config.properties
     * @param propertyName  Name of property you want to load
     * @return  Value of property
     */
    public static String LoadProperty(String propertyName) {
        try {
            // Read contents of config file
            InputStream input = PropertyLoader.class.getClassLoader().getResourceAsStream("config.properties");
            // Return property with provided name
            Properties properties = new Properties();
            properties.load(input);
            return properties.getProperty(propertyName);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
