package com.mineshop.system.pontos;

import org.bukkit.scheduler.*;
import com.mineshop.*;
import com.mineshop.system.*;
import java.sql.*;
import org.bukkit.plugin.*;

public class TUpdater extends Thread
{
    private String nick;
    private Connection con;
    private boolean test;
    
    public TUpdater(final String nick, final Connection c) {
        this.nick = "";
        this.con = null;
        this.test = false;
        this.nick = nick;
        this.con = c;
        this.test = true;
    }
    
    public TUpdater(final String nick) {
        this.nick = "";
        this.con = null;
        this.test = false;
        this.nick = nick;
    }
    
    @Override
    public void run() {
        new BukkitRunnable() {
            public void run() {
                try {
                    if (Main.getPontosCore().getCached(TUpdater.this.nick) != null) {
                        final PPlayer pp = Main.getPontosCore().getCached(TUpdater.this.nick);
                        pp.update();
                        return;
                    }
                    if (TUpdater.this.con == null) {
                        TUpdater.access$2(TUpdater.this, Main.getInstance().getStorage().getConnection());
                    }
                    final Statement stmt = TUpdater.this.con.createStatement();
                    final ResultSet rs = stmt.executeQuery("SELECT Points FROM MineSHOP_Points WHERE Player='" + TUpdater.this.nick + "'");
                    if (rs.next()) {
                        final PPlayer pp2 = new PPlayer(TUpdater.this.nick);
                        pp2.setSaldo(rs.getInt("Points"));
                        Main.getPontosCore().getCache().put(TUpdater.this.nick, pp2);
                        Main.debug("Saldo de " + TUpdater.this.nick + " cacheado.");
                    }
                    if (!TUpdater.this.test) {
                        TUpdater.this.con.close();
                    }
                    stmt.close();
                    rs.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    Logs.logToFile("Falha ao obter saldo de " + TUpdater.this.nick + "'");
                }
            }
        }.runTaskAsynchronously((Plugin)Main.getInstance());
    }
    
    static /* synthetic */ void access$2(final TUpdater tUpdater, final Connection con) {
        tUpdater.con = con;
    }
}
