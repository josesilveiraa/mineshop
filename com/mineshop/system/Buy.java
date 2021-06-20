package com.mineshop.system;

import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.configuration.file.*;
import com.mineshop.*;
import com.mineshop.utils.*;
import org.bukkit.command.*;
import org.bukkit.*;
import com.mineshop.api.listeners.*;
import org.bukkit.event.*;
import org.bukkit.inventory.meta.*;
import java.util.*;

public class Buy
{
    public static void buy(final Player p, final ItemStack produto, final FileConfiguration c, final String path) {
        try {
            p.closeInventory();
            int cash = Main.getInstance().getAPI().getPontos(p.getName());
            if (cash == -1) {
                p.closeInventory();
                p.sendMessage("§cDesculpe, ocorreu um problema.");
                return;
            }
            final int custo = c.getInt("Itens." + path + ".price");
            if (cash >= custo) {
                cash -= custo;
                if (c.getBoolean("Itens." + path + ".give")) {
                    final ItemStack i = new ItemStack(produto.getType(), produto.getAmount(), produto.getDurability());
                    final ItemMeta meta = i.getItemMeta();
                    if (c.get("Itens." + path + ".enchants") != null) {
                        String[] split;
                        for (int length = (split = c.getString("Itens." + path + ".enchants").split(",")).length, j = 0; j < length; ++j) {
                            final String en = split[j];
                            if (!en.equalsIgnoreCase("none")) {
                                final String ew = en.split(":")[0];
                                final int level = Integer.parseInt(en.split(":")[1]);
                                if (EnchantLibrary.getTranslateEnchant(ew) != null) {
                                    meta.addEnchant(EnchantLibrary.getTranslateEnchant(ew), level, true);
                                }
                            }
                        }
                    }
                    i.setItemMeta(meta);
                    if (p.getInventory().firstEmpty() == -1) {
                        p.sendMessage(Main.getMsg("inventoryfull"));
                        return;
                    }
                    p.getInventory().addItem(new ItemStack[] { i });
                }
                if (Main.getInstance().getAPI().setPontos(p.getName(), cash)) {
                    if (c.get("Itens." + path + ".commands") != null) {
                        for (final String cmd : c.getStringList("Itens." + path + ".commands")) {
                            Main.getInstance().getServer().dispatchCommand((CommandSender)Main.getInstance().getServer().getConsoleSender(), cmd.replaceAll("%player%", p.getName()));
                        }
                    }
                    p.sendMessage(Main.getMsg("buy").replaceAll("%item%", produto.getItemMeta().getDisplayName()).replaceAll("%points%", new StringBuilder(String.valueOf(custo)).toString()));
                    final String pd = ChatColor.translateAlternateColorCodes('§', produto.getItemMeta().getDisplayName());
                    Logs.logToFile(String.valueOf(p.getName()) + " comprou " + pd + " no shop por " + custo + " pontos");
                    Bukkit.broadcastMessage(Main.getMsg("buy-broadcast").replaceAll("%item%", produto.getItemMeta().getDisplayName()).replaceAll("%points%", new StringBuilder(String.valueOf(custo)).toString()).replaceAll("%player%", p.getName()));
                    final ShopBuyEvent event = new ShopBuyEvent(p, Main.getInstance().getAPI().getChachedPlayer(p.getName()), produto, path);
                    Bukkit.getPluginManager().callEvent((Event)event);
                }
                else {
                    p.closeInventory();
                    p.sendMessage("§cDesculpe, ocorreu um problema, tente novamente.");
                }
            }
            else {
                p.sendMessage(Main.getMsg("insuficient-points"));
            }
        }
        catch (Exception e1) {
            e1.printStackTrace();
            p.sendMessage(Main.getMsg("buycanceled"));
        }
    }
}
