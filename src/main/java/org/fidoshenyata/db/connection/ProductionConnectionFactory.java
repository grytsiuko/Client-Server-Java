package org.fidoshenyata.db.connection;

import org.fidoshenyata.db.utils.DbProperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ProductionConnectionFactory extends AbstractConnectionFactory{

    private static final String URL = DbProperties.getProperty("url");
    private static final String USER = DbProperties.getProperty("user");
    private static final String PASS = DbProperties.getProperty("password");

    public Connection getConnection(){
        try{
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }

    }
}
