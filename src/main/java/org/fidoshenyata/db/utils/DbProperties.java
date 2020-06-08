package org.fidoshenyata.db.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class DbProperties {

    private static final Properties dbProps = new Properties();
    private static final String dbConfigPath = Objects.requireNonNull(Thread
            .currentThread().getContextClassLoader().getResource("db.properties")).getPath();

    public static String getProperty(String key) {
        if (dbProps.isEmpty()) {
            synchronized (dbProps) {
                if (dbProps.isEmpty()) {
                    try {
                        dbProps.load(new FileInputStream(dbConfigPath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return dbProps.getProperty(key);
    }
}
