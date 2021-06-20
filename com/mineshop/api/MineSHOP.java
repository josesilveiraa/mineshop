package com.mineshop.api;

import org.bukkit.plugin.*;
import com.mineshop.*;
import org.bukkit.scheduler.*;
import com.mineshop.system.pontos.*;
import com.mineshop.system.*;
import java.sql.*;

public class MineSHOP
{
    public MineSHOP(final Plugin pl) {
        if (!pl.getName().equalsIgnoreCase("MineSHOP")) {
            Main.debug("Hooked " + pl.getName() + " v" + pl.getDescription().getVersion());
        }
    }
    
    public int getPontos(final String player) {
        if (Main.getPontosCore().getCached(player) != null) {
            return Main.getPontosCore().getCached(player).getSaldo();
        }
        return 0;
    }
    
    public boolean setPontos(final String player, final int pontos) {
        if (Main.getPontosCore().getCached(player) != null) {
            final PPlayer pp = Main.getPontosCore().getCached(player);
            pp.setSaldo(pontos);
            pp.save();
        }
        else {
            new BukkitRunnable() {
                public void run() {
                    try {
                        final Connection c = Main.getInstance().getStorage().getConnection();
                        final Statement stmt2 = c.createStatement();
                        stmt2.execute("INSERT INTO MineSHOP_Points (Player, Points) VALUES ('" + player + "','" + pontos + "')");
                        stmt2.close();
                        c.close();
                        final PPlayer mp = new PPlayer(player);
                        mp.setSaldo(pontos);
                        Main.getPontosCore().getCache().put(player, mp);
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                        Logs.logToFile("Ocorreu um problema ao salvar o saldo de " + player);
                    }
                }
            }.runTaskAsynchronously((Plugin)Main.getInstance());
        }
        return Main.getPontosCore().getCached(player) != null;
    }
    
    public PPlayer getChachedPlayer(final String nickname) {
        return Main.getPontosCore().getCached(nickname);
    }
}
