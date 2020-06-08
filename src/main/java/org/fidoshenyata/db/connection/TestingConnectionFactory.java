package org.fidoshenyata.db.connection;

import org.fidoshenyata.db.utils.EmbeddedDb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestingConnectionFactory extends AbstractConnectionFactory {

    static String url = EmbeddedDb.getUrl();

    public Connection getConnection() {
        try{
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }
}
