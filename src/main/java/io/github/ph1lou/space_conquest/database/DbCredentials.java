

package io.github.ph1lou.space_conquest.database;

public class DbCredentials {

    private final String host;
    private final String user;
    private final String pass;
    private final String dbName;
    private final int port;
    
    public DbCredentials(String host, String user, String pass, String dbName, int port) {
        this.host = host;
        this.user = user;
        this.pass = pass;
        this.dbName = dbName;
        this.port = port;
    }
    
    public String toURI() {
        return "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.dbName;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public String getPass() {
        return this.pass;
    }
}
