

package io.github.ph1lou.space_conquest.database;

import io.github.ph1lou.space_conquest.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.SQLException;

public class DataBaseManager {

    private final DbConnection dbConnection;
    
    public DataBaseManager(Main main) {
        FileConfiguration configuration = main.getConfig();
        String host = configuration.getString("host");
        String user = configuration.getString("user");
        String pass = configuration.getString("pass");
        String dbName = configuration.getString("db_name");
        int port = configuration.getInt("port");

        this.dbConnection = new DbConnection(
                new DbCredentials(
                        host,
                        user,
                        pass,
                        dbName,
                        port)
        );
    }
    
    public DbConnection getDataBaseConnection() {
        return this.dbConnection;
    }
    
    public void close() {
        try {
            this.dbConnection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
