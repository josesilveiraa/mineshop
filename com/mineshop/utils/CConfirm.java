package com.mineshop.utils;

import org.bukkit.entity.*;
import org.bukkit.configuration.file.*;
import com.mineshop.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

public class CConfirm
{
    private Player p;
    private ItemStack produto;
    private FileConfiguration c;
    
    public CConfirm(final Player p, final ItemStack produto, final FileConfiguration c) {
        this.p = null;
        this.produto = null;
        this.p = p;
        this.produto = produto;
        this.c = c;
    }
    
    public Player getPlayer() {
        return this.p;
    }
    
    public ItemStack getProduto() {
        return this.produto;
    }
    
    public FileConfiguration getConfig() {
        return this.c;
    }
    
    public void init() {
        final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 9, Main.getInstance().getConfig().getString("confirmar.menu_name").replaceAll("&", "§"));
        final ItemStack s = new ItemStack(Material.WOOL, 1, (short)5);
        final ItemMeta smeta = s.getItemMeta();
        smeta.setDisplayName("§aConfirmar");
        s.setItemMeta(smeta);
        final ItemStack n = new ItemStack(Material.WOOL, 1, (short)14);
        final ItemMeta nmeta = n.getItemMeta();
        nmeta.setDisplayName("§cCancelar");
        n.setItemMeta(nmeta);
        inv.setItem(2, n);
        inv.setItem(6, s);
        inv.setItem(4, this.produto);
        this.p.openInventory(inv);
    }
}
