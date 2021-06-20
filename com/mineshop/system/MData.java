package com.mineshop.system;

import org.bukkit.scheduler.*;
import com.mineshop.*;
import java.io.*;
import java.text.*;
import java.sql.*;
import java.util.*;
import org.bukkit.plugin.*;

public class MData extends Thread
{
    @Override
    public void run() {
        new BukkitRunnable() {
            public void run() {
                try {
                    Main.debug("Iniciando copia da database...");
                    final File f = new File("plugins" + File.separator + "MineSHOP" + File.separator + "copias");
                    if (!f.exists()) {
                        f.mkdirs();
                    }
                    final Calendar c = Calendar.getInstance();
                    final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy hh-mm");
                    final String name = sdf.format(c.getTime());
                    final Connection co = DriverManager.getConnection("jdbc:sqlite:plugins/MineSHOP/copias/" + name + " .db");
                    Main.debug("Database local criada com sucesso. (plugins/MineSHOP/copias/" + name + ".db)");
                    final Connection mysql = Main.getInstance().getStorage().getConnection();
                    final Statement m_stmt = mysql.createStatement();
                    final ResultSet m_rs = m_stmt.executeQuery("SELECT * FROM MineSHOP_Points");
                    int i = 0;
                    final HashSet<String> data = new HashSet<String>();
                    while (m_rs.next()) {
                        final String player = m_rs.getString("Player");
                        final int pontos = m_rs.getInt("Points");
                        ++i;
                        Main.debug("User: " + player + " pontos: " + pontos + " #" + i);
                        data.add(String.valueOf(player) + ":" + pontos);
                    }
                    mysql.close();
                    m_stmt.close();
                    m_rs.close();
                    final Statement stmt = co.createStatement();
                    stmt.execute("CREATE TABLE IF NOT EXISTS dados (player TEXT, pontos TEXT)");
                    Main.debug("Inserindo dados na database local...");
                    for (final String a : data) {
                        final String player2 = a.split(":")[0];
                        final String pontos2 = a.split(":")[1];
                        stmt.execute("INSERT INTO dados (player, pontos) VALUES ('" + player2 + "','" + pontos2 + "');");
                    }
                    co.close();
                    stmt.close();
                    Main.debug("§aCopia completa.");
                    Main.copiando = false;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Main.debug("§cNao foi possivel iniciar a copia da database.");
                }
            }
        }.runTaskAsynchronously((Plugin)Main.getInstance());
    }
}
