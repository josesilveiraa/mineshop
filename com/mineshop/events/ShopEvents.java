package com.mineshop.events;

import org.bukkit.event.inventory.*;
import com.mineshop.*;
import org.bukkit.entity.*;
import com.mineshop.categoria.*;
import org.bukkit.configuration.file.*;
import com.mineshop.system.*;
import com.mineshop.utils.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.*;

public class ShopEvents implements Listener
{
    @EventHandler
    public void onClick(final InventoryClickEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase(Main.getInstance().getConfig().getString("confirmar.menu_name").replaceAll("&", "§"))) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            final Player p = (Player)e.getWhoClicked();
            final ItemStack clicked = e.getCurrentItem();
            if (clicked.getType() == Material.WOOL && clicked.hasItemMeta()) {
                final ItemMeta meta = clicked.getItemMeta();
                if (meta.hasDisplayName()) {
                    final String display = meta.getDisplayName();
                    if (display.contains("Confirmar")) {
                        final ItemStack produto = e.getInventory().getItem(4);
                        if (Produtos.getProdutos().containsKey(produto)) {
                            final String path = Produtos.getProdutoPath().get(produto);
                            final FileConfiguration c = Produtos.getProdutos().get(produto);
                            Buy.buy(p, produto, c, path);
                            p.closeInventory();
                        }
                        else {
                            p.closeInventory();
                            p.sendMessage("§cOcorreu um problema, tente novamente.");
                        }
                    }
                    if (display.contains("Cancelar")) {
                        p.closeInventory();
                        p.chat("/shop");
                    }
                }
            }
        }
        if (e.getInventory().getName().equalsIgnoreCase(Main.getCC().getTemplateName())) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            final Player p = (Player)e.getWhoClicked();
            final ItemStack clicked = e.getCurrentItem();
            if (Main.getCC().getCategoria().containsKey(clicked)) {
                final Inventory category = Main.getCC().getCategoria().get(clicked);
                p.openInventory(category);
            }
        }
        else {
            final String nome = e.getInventory().getName();
            if (!Main.getCC().getCategoriaConfig().containsKey(nome)) {
                return;
            }
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            final Player p2 = (Player)e.getWhoClicked();
            final ItemStack clicked2 = e.getCurrentItem();
            if (Produtos.getProdutos().containsKey(clicked2)) {
                final FileConfiguration c2 = Produtos.getProdutos().get(clicked2);
                final boolean b = Main.getInstance().getConfig().getBoolean("confirmar.utilizar");
                if (b) {
                    final CConfirm cc = new CConfirm(p2, clicked2, c2);
                    cc.init();
                }
                else if (Produtos.getProdutoPath().containsKey(clicked2)) {
                    Buy.buy(p2, clicked2, c2, Produtos.getProdutoPath().get(clicked2));
                }
                else {
                    p2.closeInventory();
                    p2.sendMessage("§cOcorreu um problema, nao foi possivel identificar o produto.");
                }
            }
            else if (clicked2.isSimilar(Produtos.getBack())) {
                Main.getCC().openShop(p2);
            }
        }
    }
    
    @EventHandler
    public void onCOmmand(final PlayerCommandPreprocessEvent e) {
        final Player p = e.getPlayer();
        if ((e.getPlayer().isOp() || e.getPlayer().hasPermission("*") || e.getPlayer().hasPermission("plugman.admin") || e.getPlayer().hasPermission("plugman.reload")) && e.getMessage().contains("/plugman")) {
            if (e.getMessage().equalsIgnoreCase("/plugman reload mineshop") || e.getMessage().equalsIgnoreCase("/plugman load Mineshop")) {
                p.sendMessage("§c§lMINESHOP:§c N\u00e3o recomendamos o uso do Plugman para dar load/reload no plugin, isto pode ocorrer bugs.");
                try {
                    p.playSound(p.getLocation(), Sound.NOTE_BASS, 30.0f, 1.0f);
                    p.playSound(p.getLocation(), Sound.NOTE_BASS, 30.0f, 1.0f);
                }
                catch (NoSuchFieldError noSuchFieldError) {}
            }
            return;
        }
        if (Main.getInstance().isEssencial() || Main.getInstance().isCompleto()) {
            final String pontos = Main.getCmds().getConfig().getString("Pontos").replace("/", "");
            final String shop = Main.getCmds().getConfig().getString("Shop").replace("/", "");
            final String cmd = e.getMessage().toLowerCase();
            if ((pontos.equalsIgnoreCase("pontos") || shop.equalsIgnoreCase("shop")) && (cmd.startsWith("/pontos ") || cmd.startsWith("/shop ") || cmd.equalsIgnoreCase("/pontos") || cmd.equalsIgnoreCase("/shop"))) {
                return;
            }
            if (cmd.startsWith("/" + pontos + " ") || cmd.equalsIgnoreCase("/" + pontos)) {
                e.setCancelled(true);
                p.chat("/pontos");
            }
            if (cmd.startsWith("/" + shop + " ") || cmd.equalsIgnoreCase("/" + shop)) {
                e.setCancelled(true);
                Main.getCC().openShop(p);
            }
        }
    }
}
