package com.mineshop.system.pontos;

import org.bukkit.scheduler.*;
import com.mineshop.*;
import com.mineshop.system.*;
import java.sql.*;
import org.bukkit.plugin.*;

public class PPlayer
{
    private int saldo;
    private String player;
    
    public PPlayer(final String player) {
        this.saldo = 0;
        this.player = "";
        this.player = player;
    }
    
    public int getSaldo() {
        return this.saldo;
    }
    
    public void setSaldo(final int i) {
        this.saldo = i;
    }
    
    public String getPlayer() {
        return this.player;
    }
    
    public void save() {
        new BukkitRunnable() {
            public void run() {
                try {
                    final Connection c = Main.getInstance().getStorage().getConnection();
                    final Statement stmt = c.createStatement();
                    final ResultSet rs = stmt.executeQuery("SELECT Points FROM MineSHOP_Points WHERE Player='" + PPlayer.this.player + "'");
                    if (rs.next()) {
                        final Statement stmt2 = c.createStatement();
                        stmt2.executeUpdate("UPDATE MineSHOP_Points SET Points='" + PPlayer.this.saldo + "' WHERE Player='" + PPlayer.this.player + "'");
                        stmt2.close();
                    }
                    else {
                        final Statement stmt2 = c.createStatement();
                        stmt2.execute("INSERT INTO MineSHOP_Points (Player, Points) VALUES ('" + PPlayer.this.player + "','" + PPlayer.this.saldo + "')");
                        stmt2.close();
                        Logs.logToFile(PPlayer.this.player + " salvo na database. (Saldo: " + PPlayer.this.saldo + ")");
                    }
                    c.close();
                    stmt.close();
                    rs.close();
                    Main.debug("Saldo de " + PPlayer.this.player + " salvo.");
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously((Plugin)Main.getInstance());
    }
    
    public void update() {
        new BukkitRunnable() {
            public void run() {
                try {
                    final Connection c = Main.getInstance().getStorage().getConnection();
                    final Statement stmt = c.createStatement();
                    final ResultSet rs = stmt.executeQuery("SELECT Points FROM MineSHOP_Points WHERE Player='" + PPlayer.this.player + "'");
                    if (rs.next()) {
                        PPlayer.access$2(PPlayer.this, rs.getInt("Points"));
                    }
                    c.close();
                    stmt.close();
                    rs.close();
                    Main.debug("Saldo de " + PPlayer.this.player + " atualizado.");
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously((Plugin)Main.getInstance());
    }
    
    static /* synthetic */ void access$2(final PPlayer pPlayer, final int saldo) {
        pPlayer.saldo = saldo;
    }
}
