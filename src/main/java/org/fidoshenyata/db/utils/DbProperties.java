package org.fidoshenyata.db.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DbProperties {

    private static Properties dbProps;
    private static String dbConfigPath = Thread
            .currentThread().getContextClassLoader().getResource("db.properties").getPath();

    public static String getProperty(String key) {
        if (dbProps == null) {
            dbProps = new Properties();
            try {
                dbProps.load(new FileInputStream(dbConfigPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return dbProps.getProperty(key);
    }
}
