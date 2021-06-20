package com.mineshop.categoria;

import org.bukkit.configuration.file.*;
import org.bukkit.inventory.*;
import com.mineshop.utils.*;
import com.mineshop.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.enchantments.*;

public class Produtos
{
    private static HashMap<ItemStack, FileConfiguration> produtos;
    private static HashMap<ItemStack, String> produto_path;
    
    static {
        Produtos.produtos = new HashMap<ItemStack, FileConfiguration>();
        Produtos.produto_path = new HashMap<ItemStack, String>();
    }
    
    public static void loadProdutos(final FileConfiguration config, final Inventory inv, final String categoria) {
        inv.clear();
        final List<ItemStack> itens = new ArrayList<ItemStack>();
        final HashMap<ItemStack, Integer> slot = new HashMap<ItemStack, Integer>();
        if (config == null) {
            return;
        }
        int i = 0;
        for (final String c : config.getConfigurationSection("Itens").getKeys(false)) {
            try {
                final String itemName = c;
                final ItemStack stack = new ItemStack(config.getInt("Itens." + itemName + ".id"), 1, (short)config.getInt("Itens." + itemName + ".data"));
                final ItemMeta stackM = stack.getItemMeta();
                if (config.getString("Itens." + itemName + ".name") != null) {
                    stackM.setDisplayName(config.getString("Itens." + itemName + ".name").replaceAll("&", "§"));
                }
                int amount = config.getInt("Itens." + itemName + ".amount");
                if (amount > 64) {
                    amount = 64;
                }
                stack.setAmount(amount);
                setLore(stackM, config, itemName);
                if (config.getString("Itens." + itemName + ".enchants") != null) {
                    final String ench = config.getString("Itens." + itemName + ".enchants");
                    String[] split;
                    for (int length = (split = ench.split(",")).length, j = 0; j < length; ++j) {
                        final String e = split[j];
                        final Enchantment en = EnchantLibrary.getTranslateEnchant(e.split(":")[0]);
                        if (en != null) {
                            stackM.addEnchant(en, (int)Integer.valueOf(e.split(":")[1]), true);
                        }
                    }
                }
                stack.setItemMeta(stackM);
                itens.add(stack);
                slot.put(stack, config.getInt("Itens." + itemName + ".slot"));
                Produtos.produtos.put(stack, config);
                Produtos.produto_path.put(stack, itemName);
                ++i;
            }
            catch (Exception e2) {
                e2.printStackTrace();
                Main.debug("Falha ao carregar o produto: §c" + c + " §e da categoria: §f" + categoria);
            }
        }
        for (final ItemStack item : itens) {
            inv.setItem((int)slot.get(item), item);
        }
        final ItemStack back = new ItemStack(Material.ARROW);
        final ItemMeta m = back.getItemMeta();
        m.setDisplayName(Main.getInstance().getConfig().getString("back-item").replaceAll("&", "§"));
        back.setItemMeta(m);
        inv.setItem(40, back);
        itens.clear();
        slot.clear();
        Main.debug("Foram carregados §f" + i + " §eprodutos da categoria: §f" + categoria);
    }
    
    public static HashMap<ItemStack, FileConfiguration> getProdutos() {
        return Produtos.produtos;
    }
    
    public static HashMap<ItemStack, String> getProdutoPath() {
        return Produtos.produto_path;
    }
    
    private static void setLore(final ItemMeta meta, final FileConfiguration config, final String name) {
        if (config.getStringList("Itens." + name + ".lore") != null) {
            final List<String> lore = new ArrayList<String>();
            for (final String s : config.getStringList("Itens." + name + ".lore")) {
                lore.add(s.replaceAll("&", "§"));
            }
            lore.add("");
            for (final String price : Main.getInstance().getConfig().getStringList("products-lore")) {
                lore.add(price.replaceAll("&", "§").replaceAll("%product name%", config.getString("Itens." + name + ".name").replaceAll("&", "§")).replaceAll("%points%", new StringBuilder(String.valueOf(config.getInt("Itens." + name + ".price"))).toString()).replaceAll("%amount%", new StringBuilder(String.valueOf(config.getInt("Itens." + name + ".amount"))).toString()));
            }
            meta.setLore((List)lore);
        }
        else {
            final List<String> lore = new ArrayList<String>();
            lore.add("");
            for (final String price : Main.getInstance().getConfig().getStringList("products-lore")) {
                lore.add(price.replaceAll("&", "§").replaceAll("%product name%", config.getString("Itens." + name + ".name").replaceAll("&", "§").replaceAll("%points%", new StringBuilder(String.valueOf(config.getInt("Itens." + name + ".price"))).toString()).replaceAll("%amount%", new StringBuilder(String.valueOf(config.getInt("Itens." + name + ".amount"))).toString())));
            }
            meta.setLore((List)lore);
        }
    }
    
    public static ItemStack getBack() {
        final ItemStack back = new ItemStack(Material.ARROW);
        final ItemMeta m = back.getItemMeta();
        m.setDisplayName(Main.getInstance().getConfig().getString("back-item").replaceAll("&", "§"));
        back.setItemMeta(m);
        return back;
    }
}
