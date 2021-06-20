package com.mineshop.system;

import org.bukkit.scheduler.*;
import org.bukkit.entity.*;
import com.mineshop.*;
import com.mineshop.system.pontos.*;
import org.bukkit.*;
import java.sql.*;

public class MAtivar extends BukkitRunnable
{
    private Player p;
    private String code;
    
    public MAtivar(final Player p, final String code) {
        this.p = null;
        this.code = "";
        this.p = p;
        this.code = code;
    }
    
    public void run() {
        try {
            final Connection c = Main.getInstance().getStorage().getConnection();
            PreparedStatement stmt = c.prepareStatement("SELECT ref_code FROM compras WHERE ref_code='" + this.code + "'");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                stmt.close();
                rs.close();
                stmt = c.prepareStatement("SELECT pontos FROM compras WHERE ref_code='" + this.code + "'");
                rs = stmt.executeQuery();
                rs.next();
                if (rs.getInt("pontos") == 1) {
                    rs.close();
                    stmt.close();
                    stmt = c.prepareStatement("SELECT status FROM compras WHERE ref_code='" + this.code + "'");
                    rs = stmt.executeQuery();
                    rs.next();
                    if (rs.getInt("status") == 0) {
                        rs.close();
                        stmt.close();
                        stmt = c.prepareStatement("SELECT payment_status FROM compras WHERE ref_code='" + this.code + "'");
                        rs = stmt.executeQuery();
                        rs.next();
                        if (rs.getString("payment_status").equalsIgnoreCase("pagamento aprovado")) {
                            stmt.close();
                            stmt = c.prepareStatement("SELECT qntidade FROM compras WHERE ref_code='" + this.code + "'");
                            rs = stmt.executeQuery();
                            rs.next();
                            final int amount = rs.getInt("qntidade");
                            stmt.close();
                            stmt = c.prepareStatement("UPDATE compras SET status='1' WHERE ref_code='" + this.code + "'");
                            stmt.executeUpdate();
                            stmt.close();
                            int saldo = -1;
                            final PPlayer mpp = Main.getPontosCore().getCached(this.p.getName());
                            if (saldo == -1 && mpp == null) {
                                saldo = amount;
                                final Statement stmt2 = c.createStatement();
                                stmt2.execute("INSERT INTO MineSHOP_Points (Player, Points) VALUES ('" + this.p.getName() + "','" + saldo + "')");
                                stmt2.close();
                                final PPlayer mp = new PPlayer(this.p.getName());
                                mp.setSaldo(saldo);
                                Main.getPontosCore().getCache().put(this.p.getName(), mp);
                            }
                            else {
                                saldo = Main.getInstance().getAPI().getPontos(this.p.getName());
                                saldo += amount;
                                mpp.setSaldo(saldo);
                                mpp.save();
                            }
                            this.p.sendMessage(Main.getMsg("activated-pv").replaceAll("%points%", new StringBuilder(String.valueOf(amount)).toString()));
                            Bukkit.broadcastMessage(Main.getMsg("activated-broadcast").replaceAll("%points%", new StringBuilder(String.valueOf(amount)).toString()).replaceAll("%player%", this.p.getName()));
                            Logs.logToFile(String.valueOf(this.p.getName()) + " ativou o codigo: " + this.code + " de " + amount + " pontos");
                            rs.close();
                        }
                        else {
                            this.p.sendMessage(Main.getMsg("code-payment"));
                        }
                    }
                    else {
                        this.p.sendMessage(Main.getMsg("code-used"));
                    }
                }
                else {
                    this.p.sendMessage(Main.getMsg("unsuported-code"));
                }
            }
            else {
                this.p.sendMessage(Main.getMsg("unknown-refcode"));
            }
            c.close();
            stmt.close();
            rs.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
