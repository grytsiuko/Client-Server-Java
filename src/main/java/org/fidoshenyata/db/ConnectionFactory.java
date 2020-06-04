package org.fidoshenyata.db;

import org.fidoshenyata.db.utils.DbProperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class ConnectionFactory {

    public static final String URL = DbProperties.getProperty("url");
    public static final String USER = DbProperties.getProperty("user");
    public static final String PASS = DbProperties.getProperty("password");

    public static Connection getConnection(){
        try{
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }

    }

    public static void main(String[] args) {
        Connection con = ConnectionFactory.getConnection();
    }
}
