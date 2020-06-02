package org.fidoshenyata.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class ConnectionFactory {

    public static final String URL = "jdbc:postgresql://localhost:5432/cslab";
    public static final String USER = "postgres";
    public static final String PASS = "root23";

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
