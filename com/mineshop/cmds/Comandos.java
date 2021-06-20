package com.mineshop.cmds;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import com.mineshop.*;
import org.bukkit.*;
import java.util.concurrent.*;
import org.bukkit.plugin.*;
import com.mineshop.categoria.*;
import com.mineshop.system.*;
import java.sql.*;
import java.util.*;

public class Comandos implements CommandExecutor
{
    public WeakHashMap<String, Long> delay;
    
    public Comandos() {
        this.delay = new WeakHashMap<String, Long>();
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (label.equalsIgnoreCase("pontos") || label.equalsIgnoreCase("points")) {
            boolean perm = true;
            boolean player = false;
            if (sender instanceof Player) {
                final Player p = (Player)sender;
                perm = p.hasPermission("points.admin");
                player = true;
            }
            if (args.length == 0) {
                if (player) {
                    final Player p = (Player)sender;
                    final int points = Main.getInstance().getAPI().getPontos(p.getName());
                    p.sendMessage(Main.getMsg("you-points").replaceAll("%points%", new StringBuilder(String.valueOf(points)).toString()));
                    return false;
                }
                this.sendHelp(sender);
            }
            if (args.length != 1) {
                if (args.length == 3) {
                    Label_1365: {
                        final String s;
                        switch (s = args[0]) {
                            case "enviar": {
                                break;
                            }
                            case "remove": {
                                if (!perm) {
                                    sender.sendMessage(Main.getMsg("no-permission"));
                                    return true;
                                }
                                if (this.isInt(args[2])) {
                                    String aaa = args[1];
                                    if (Bukkit.getOfflinePlayer(aaa) != null) {
                                        aaa = Bukkit.getOfflinePlayer(aaa).getName();
                                    }
                                    final String pl = aaa;
                                    final int pontosofplayer = Main.getInstance().getAPI().getPontos(pl);
                                    final int points2 = Integer.parseInt(args[2]);
                                    int end = pontosofplayer - points2;
                                    if (end < 0) {
                                        end = 0;
                                    }
                                    Main.getInstance().getAPI().setPontos(pl, end);
                                    sender.sendMessage(Main.getMsg("points-remove").replaceAll("%points%", new StringBuilder(String.valueOf(points2)).toString()).replaceAll("%player%", pl));
                                    return false;
                                }
                                sender.sendMessage(Main.getMsg("unknown"));
                                return false;
                            }
                            case "add": {
                                if (!perm) {
                                    sender.sendMessage(Main.getMsg("no-permission"));
                                    return true;
                                }
                                if (this.isInt(args[2])) {
                                    String aaa = args[1];
                                    if (Bukkit.getOfflinePlayer(aaa) != null) {
                                        aaa = Bukkit.getOfflinePlayer(aaa).getName();
                                    }
                                    final int points3 = Integer.parseInt(args[2]) + Main.getInstance().getAPI().getPontos(aaa);
                                    final String pl2 = aaa;
                                    final int addeds = Integer.parseInt(args[2]);
                                    Main.getInstance().getAPI().setPontos(pl2, points3);
                                    sender.sendMessage(Main.getMsg("points-added").replaceAll("%points%", new StringBuilder(String.valueOf(addeds)).toString()).replaceAll("%player%", pl2));
                                    Logs.logToFile(String.valueOf(sender.getName()) + " adicionou " + addeds + " pontos para " + pl2);
                                    return false;
                                }
                                sender.sendMessage(Main.getMsg("unknown"));
                                return false;
                            }
                            case "pay": {
                                break;
                            }
                            default:
                                break Label_1365;
                        }
                        if (!sender.hasPermission("mineshop.sendpoints")) {
                            sender.sendMessage(Main.getMsg("nosendpoints"));
                            return false;
                        }
                        if (!player) {
                            return false;
                        }
                        final Player p2 = (Player)sender;
                        if (!this.isInt(args[2])) {
                            p2.sendMessage(Main.getMsg("unknown"));
                            return false;
                        }
                        if (this.delay.containsKey(p2.getName())) {
                            final long l = this.delay.get(p2.getName());
                            if (l > System.currentTimeMillis()) {
                                p2.sendMessage(Main.getMsg("delay"));
                                return false;
                            }
                            this.delay.remove(p2.getName());
                        }
                        this.delay.put(p2.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5L));
                        if (args[1].equalsIgnoreCase(p2.getName())) {
                            p2.sendMessage(Main.getMsg("nosend"));
                            return false;
                        }
                        String aaa2 = args[1];
                        if (Bukkit.getOfflinePlayer(aaa2) != null) {
                            aaa2 = Bukkit.getOfflinePlayer(aaa2).getName();
                        }
                        String pl2 = aaa2;
                        final int pontos = Main.getInstance().getAPI().getPontos(p2.getName());
                        if (Bukkit.getPlayer(pl2) == null) {
                            p2.sendMessage(Main.getMsg("player-offline"));
                            return false;
                        }
                        if (pontos >= Integer.parseInt(args[2])) {
                            pl2 = Bukkit.getPlayer(pl2).getName();
                            final int points4 = Integer.parseInt(args[2]) + Main.getInstance().getAPI().getPontos(pl2);
                            final int addeds2 = Integer.parseInt(args[2]);
                            final int plp = Main.getInstance().getAPI().getPontos(p2.getName());
                            Main.getInstance().getAPI().setPontos(pl2, points4);
                            Main.getInstance().getAPI().setPontos(p2.getName(), plp - addeds2);
                            p2.sendMessage(Main.getMsg("sendpoints").replaceAll("%points%", new StringBuilder(String.valueOf(addeds2)).toString()).replaceAll("%player%", pl2));
                            final Player pl3 = Bukkit.getPlayer(pl2);
                            pl3.sendMessage(Main.getMsg("received").replaceAll("%points%", new StringBuilder(String.valueOf(addeds2)).toString()).replaceAll("%player%", p2.getName()));
                            Logs.logToFile(String.valueOf(p2.getName()) + " enviou " + addeds2 + " pontos para " + pl2);
                            return false;
                        }
                        p2.sendMessage(Main.getMsg("insuficientpoints"));
                        return false;
                    }
                    sender.sendMessage("§cArgumento inv\u00e1lido.");
                }
                return false;
            }
            String a = args[0];
            if (a.equalsIgnoreCase("help")) {
                if (perm || !player) {
                    this.sendHelp(sender);
                }
                else if (player && perm) {
                    this.sendHelp(sender);
                }
                else if (player) {
                    final Player p2 = (Player)sender;
                    final int points3 = Main.getInstance().getAPI().getPontos(p2.getName());
                    p2.sendMessage(Main.getMsg("you-points").replaceAll("%points%", new StringBuilder(String.valueOf(points3)).toString()));
                }
                else {
                    this.sendHelp(sender);
                }
                return true;
            }
            if (Bukkit.getPlayer(a) != null) {
                a = Bukkit.getPlayer(a).getName();
                final int pontos2 = Main.getInstance().getAPI().getPontos(a);
                final String msg = Main.getMsg("player-points");
                sender.sendMessage(msg.replace("%points%", new StringBuilder(String.valueOf(pontos2)).toString()).replace("%player%", new StringBuilder(String.valueOf(a)).toString()));
            }
            else {
                sender.sendMessage("§cJogador n\u00e3o encontrado.");
            }
            return true;
        }
        else {
            if (label.equalsIgnoreCase("shop")) {
                if (sender instanceof Player) {
                    final Player p3 = (Player)sender;
                    Main.getCC().openShop(p3);
                }
                return false;
            }
            if (label.equalsIgnoreCase("ativar") || label.equalsIgnoreCase("activate")) {
                final Player p3 = (Player)sender;
                if (args.length == 0) {
                    p3.sendMessage(Main.getMsg("code-help"));
                }
                else {
                    if (this.delay.containsKey(p3.getName())) {
                        final long i = this.delay.get(p3.getName());
                        if (i > System.currentTimeMillis()) {
                            p3.sendMessage(Main.getMsg("delay"));
                            return false;
                        }
                        this.delay.remove(p3.getName());
                    }
                    this.delay.put(p3.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10L));
                    final String refcode = args[0];
                    if (refcode.length() != 32) {
                        p3.sendMessage(Main.getMsg("unknown-refcode"));
                    }
                    else {
                        final MAtivar ma = new MAtivar(p3, refcode);
                        ma.runTaskAsynchronously((Plugin)Main.getInstance());
                    }
                }
                return false;
            }
            if (label.equalsIgnoreCase("mineshop") || label.equalsIgnoreCase("ms")) {
                if (sender instanceof Player) {
                    final Player p3 = (Player)sender;
                    if (p3.isOp()) {
                        if (args.length == 0) {
                            sender.sendMessage("");
                            sender.sendMessage("§6MineSHOP Lite - §7Lista de comandos do plugin");
                            sender.sendMessage("§8- §7/ms reload §e§o: §eRecarrega o plugin.");
                            sender.sendMessage("§8- §7/shop §e§o: §eAbre o menu do shop.");
                            sender.sendMessage("§8- §7/pontos help §e§o: §eMostra os comandos relacionados a pontos.");
                            sender.sendMessage("§8- §7/ativar <c\u00f3digo> §e§o: §eAtiva um c\u00f3digo de pontos.");
                            sender.sendMessage("§8- §7/ms resetpontos §c§o: §cReseta a database de pontos.");
                            sender.sendMessage("");
                        }
                        else {
                            if (args[0].equalsIgnoreCase("reload")) {
                                p3.sendMessage("§7Recarregando...");
                                Main.getInstance().reloadConfig();
                                if (Main.getInstance().getPerson() != null) {
                                    Main.getInstance().getPerson().reloadConfig();
                                }
                                if (Main.getCmds() != null) {
                                    Main.getCmds().reloadConfig();
                                }
                                Produtos.getProdutoPath().clear();
                                Produtos.getProdutos().clear();
                                Main.getCC().reload(p3);
                                p3.sendMessage("§aRecarregado.");
                                return false;
                            }
                            if (args[0].equalsIgnoreCase("resetpontos")) {
                                p3.sendMessage("§cEste comando deve ser executado somente pelo console do servidor.");
                                return false;
                            }
                            if (args[0].equalsIgnoreCase("copydatabase")) {
                                p3.sendMessage("§cEste comando deve ser executado somente pelo console do servidor.");
                                return false;
                            }
                            sender.sendMessage("");
                            sender.sendMessage("§6MineSHOP Lite - §7Lista de comandos do plugin");
                            sender.sendMessage("§8- §7/ms reload §e§o: §eRecarrega o plugin.");
                            sender.sendMessage("§8- §7/shop §e§o: §eAbre o menu do shop.");
                            sender.sendMessage("§8- §7/pontos help §e§o: §eMostra os comandos relacionados a pontos.");
                            sender.sendMessage("§8- §7/ativar <c\u00f3digo> §e§o: §eAtiva um c\u00f3digo de pontos.");
                            sender.sendMessage("§8- §7/ms resetpontos §c§o: §cReseta a database de pontos.");
                            sender.sendMessage("");
                        }
                    }
                    else {
                        p3.sendMessage(Main.getMsg("no-permission"));
                    }
                }
                else {
                    if (args.length == 0) {
                        sender.sendMessage("");
                        sender.sendMessage("§6MineSHOP Lite - §7Lista de comandos do plugin");
                        sender.sendMessage("§8- §7/ms reload §e§o: §eRecarrega o plugin.");
                        sender.sendMessage("§8- §7/shop §e§o: §eAbre o menu do shop.");
                        sender.sendMessage("§8- §7/pontos help §e§o: §eMostra os comandos relacionados a pontos.");
                        sender.sendMessage("§8- §7/ativar <c\u00f3digo> §e§o: §eAtiva um c\u00f3digo de pontos.");
                        sender.sendMessage("§8- §7/ms resetpontos §c§o: §cReseta a database de pontos.");
                        sender.sendMessage("");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("copydatabase")) {
                        if (Main.copiando) {
                            sender.sendMessage("§cO plugin j\u00e1 est\u00e1 efetuando uma c\u00f3pia.");
                        }
                        else {
                            Main.copiando = true;
                            final Thread th = new MData();
                            th.start();
                        }
                    }
                    if (args[0].equalsIgnoreCase("resetpontos")) {
                        sender.sendMessage("§eProcessando...");
                        try {
                            Main.getInstance().getStorage().clear();
                            for (final String a2 : Main.getPontosCore().getCache().keySet()) {
                                Main.getPontosCore().getCached(a2).setSaldo(0);
                            }
                            sender.sendMessage("§aTodos os pontos foram resetados com sucesso.");
                        }
                        catch (SQLException e) {
                            sender.sendMessage("§cOcorreu um problema.");
                        }
                        return true;
                    }
                    sender.sendMessage("§cArgumento invalido.");
                }
            }
            return false;
        }
    }
    
    public boolean isInt(final String args) {
        try {
            Integer.parseInt(args);
            final int in = Integer.parseInt(args);
            if (in <= 0) {
                return false;
            }
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    public void sendHelp(final CommandSender p) {
        if (Main.getInstance().getLanguage().equalsIgnoreCase("br")) {
            p.sendMessage("");
            p.sendMessage("§6MineSHOP Lite - §7Menu de ajuda (Pontos)");
            p.sendMessage("§8- §7/pontos <player> §e- Mostra os pontos de algum jogador.");
            p.sendMessage("§8- §7/pontos add <player> <quantidade> §e- Adiciona pontos.");
            p.sendMessage("§8- §7/pontos remove <player> <quantidade> §e- Remove pontos.");
            p.sendMessage("§8- §7/pontos enviar <player> <quantidade> §e- Envia pontos.");
            p.sendMessage("");
        }
        else {
            p.sendMessage("");
            p.sendMessage("§6MineSHOP Lite - §7Help menu (Points)");
            p.sendMessage("§8- §7/pontos <player> §e- Show points of player.");
            p.sendMessage("§8- §7/pontos add <player> <quantidade> §e- Add points to player.");
            p.sendMessage("§8- §7/pontos remove <player> <quantidade> §e- Remove points of player.");
            p.sendMessage("§8- §7/pontos pay <player> <quantidade> §e- Send points to player.");
            p.sendMessage("");
        }
    }
}
