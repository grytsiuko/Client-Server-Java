package org.fidoshenyata.db.connection;

import java.sql.Connection;

public abstract class AbstractConnectionFactory {

    public abstract Connection getConnection();

}
