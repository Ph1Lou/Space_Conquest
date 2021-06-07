
package io.github.ph1lou.space_conquest.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    private final DbCredentials dbCredentials;
    private Connection connection;
    
    public DbConnection(DbCredentials dbCredentials) {

        this.dbCredentials = dbCredentials;

        try {
            this.connect();
        }
        catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void connect() throws SQLException, ClassNotFoundException {
        if (this.connection != null && !this.connection.isClosed()) {
            return;
        }
        synchronized (this) {
            if (this.connection != null && !this.connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(this.dbCredentials.toURI(), this.dbCredentials.getUser(), this.dbCredentials.getPass());
        }
    }
    
    public void close() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            this.connection.close();
        }
    }
    
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        if (this.connection != null && !this.connection.isClosed()) {
            return this.connection;
        }
        this.connect();
        return this.connection;
    }
}
