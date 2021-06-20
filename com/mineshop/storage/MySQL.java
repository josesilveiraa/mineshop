package com.mineshop.storage;

import com.mineshop.*;
import com.mineshop.system.*;
import java.sql.*;

public class MySQL
{
    private String user;
    private String senha;
    private String database;
    private String url;
    
    public MySQL() {
        this.user = "";
        this.senha = "";
        this.database = "";
        this.url = "localhost";
        this.init();
    }
    
    private void init() {
        this.user = Main.getInstance().getConfig().getString("mysql.usuario");
        this.senha = Main.getInstance().getConfig().getString("mysql.senha");
        this.database = Main.getInstance().getConfig().getString("mysql.database");
        this.url = "jdbc:mysql://" + Main.getInstance().getConfig().getString("mysql.host") + "/" + this.database;
        try {
            final Connection c = this.getConnection();
            final Statement stmt = c.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS MineSHOP_Points (Player TEXT, Points INTEGER)");
            stmt.close();
            c.close();
            Main.debug("Database inicializada com sucesso.");
        }
        catch (SQLException e) {
            Logs.logToFile("N\u00e3o foi poss\u00edvel inicializar a database.");
            Main.getInstance().shutdown();
        }
    }
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.url, this.user, this.senha);
    }
    
    public void clear() throws SQLException {
        final Connection c = this.getConnection();
        final PreparedStatement pst = c.prepareStatement("DELETE FROM MineSHOP_Points");
        pst.execute();
        pst.close();
        c.close();
    }
}
