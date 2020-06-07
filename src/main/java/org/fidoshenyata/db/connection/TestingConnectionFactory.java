package org.fidoshenyata.db.connection;

import org.fidoshenyata.db.utils.DbProperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestingConnectionFactory extends AbstractConnectionFactory{

    private static final String URL = DbProperties.getProperty("test_url");
    private static final String USER = DbProperties.getProperty("test_user");
    private static final String PASS = DbProperties.getProperty("test_password");

    public Connection getConnection(){
        try{
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }
}
