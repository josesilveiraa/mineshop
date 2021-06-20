package com.mineshop.categoria;

import com.mineshop.*;
import org.bukkit.inventory.*;
import java.io.*;
import org.bukkit.entity.*;
import org.bukkit.configuration.file.*;
import org.bukkit.*;
import org.bukkit.inventory.meta.*;
import java.util.*;
import com.mineshop.utils.*;
import org.bukkit.plugin.java.*;

public class CategoriaCore
{
    private Inventory template;
    private HashMap<String, FileConfiguration> categoria;
    private HashMap<ItemStack, Inventory> category_acess;
    private String template_name;
    
    public CategoriaCore() {
        this.template = null;
        this.categoria = new HashMap<String, FileConfiguration>();
        this.category_acess = new HashMap<ItemStack, Inventory>();
        this.template_name = "";
        this.template_name = Main.getInstance().getConfig().getString("gui-name").replaceAll("&", "§");
        this.template = Bukkit.createInventory((InventoryHolder)null, 54, Main.getInstance().getConfig().getString("gui-name").replaceAll("&", "§"));
        this.loadTemplate();
        final File diretorio = new File(Main.getInstance().getDataFolder() + File.separator + "categories");
        if (!diretorio.exists()) {
            diretorio.mkdirs();
            Main.debug("Diretorio vazio 'categories' criado com sucesso.");
            Main.getInstance().saveResource("categories/exemplo.yml", true);
            Main.debug("Categoria de exemplo criada.");
            final File f = new File("plugins" + File.separator + "MineSHOP" + File.separator + "categories" + File.separator + "exemplo.yml");
            if (f.exists()) {
                this.loadCategoria(f, "exemplo");
            }
        }
        else {
            this.loadAll(null);
        }
    }
    
    public void reload(final Player p) {
        Main.debug("§eReloading...");
        this.categoria.clear();
        this.category_acess.clear();
        this.template_name = Main.getInstance().getConfig().getString("gui-name").replaceAll("&", "§");
        this.template = Bukkit.createInventory((InventoryHolder)null, 54, Main.getInstance().getConfig().getString("gui-name").replaceAll("&", "§"));
        this.loadTemplate();
        final File diretorio = new File(Main.getInstance().getDataFolder() + File.separator + "categories");
        if (!diretorio.exists()) {
            diretorio.mkdirs();
            Main.debug("Diretorio vazio 'categories' criado com sucesso.");
            Main.getInstance().saveResource("categories/exemplo.yml", true);
            Main.debug("Categoria de exemplo criada.");
            final File f = new File("plugins" + File.separator + "MineSHOP" + File.separator + "categories" + File.separator + "exemplo.yml");
            if (f.exists()) {
                this.loadCategoria(f, "exemplo");
            }
        }
        else {
            this.loadAll(p);
        }
    }
    
    public String getTemplateName() {
        return this.template_name;
    }
    
    public HashMap<String, FileConfiguration> getCategoriaConfig() {
        return this.categoria;
    }
    
    public HashMap<ItemStack, Inventory> getCategoria() {
        return this.category_acess;
    }
    
    private void loadAll(final Player p) {
        final File diretorio = new File(Main.getInstance().getDataFolder() + File.separator + "categories");
        int loaded = 0;
        final String plano = Main.getInstance().getPlano();
        File[] listFiles;
        for (int length = (listFiles = diretorio.listFiles()).length, i = 0; i < length; ++i) {
            final File files = listFiles[i];
            if (files.isFile()) {
                if (plano.equalsIgnoreCase("Basico") || plano.equalsIgnoreCase("B\u00e1sico")) {
                    if (loaded >= 5) {
                        Main.debug("Seu plano permite que voce possa ter somente 5 categorias.");
                        if (p != null) {
                            p.sendMessage("§cSeu plano permite que voce possa ter somente 5 categorias.");
                            break;
                        }
                        break;
                    }
                    else {
                        try {
                            this.loadCategoria(files, files.getName().replace(".yml", ""));
                            ++loaded;
                        }
                        catch (Exception e) {
                            Main.debug("§cFalha ao ler categoria: §f" + files.getName());
                            e.printStackTrace();
                        }
                    }
                }
                else if (loaded >= 10) {
                    Main.debug("Limite de categorias atingido, voce pode somente ter 10 categorias.");
                    if (p != null) {
                        p.sendMessage("§cSeu plano permite que voce possa ter somente 10 categorias.");
                        break;
                    }
                    break;
                }
                else {
                    try {
                        this.loadCategoria(files, files.getName().replace(".yml", ""));
                        ++loaded;
                    }
                    catch (Exception e) {
                        Main.debug("§cFalha ao ler categoria: §f" + files.getName());
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    private void loadCategoria(final File f, final String nome) {
        try {
            Main.debug("Lendo categoria: §f" + nome);
            final FileConfiguration c = (FileConfiguration)YamlConfiguration.loadConfiguration(f);
            if (c.getBoolean("ativate")) {
                final String display = ChatColor.translateAlternateColorCodes('&', c.getString("name"));
                final int id = c.getInt("icon-id");
                final int data = c.getInt("icon-data");
                final ItemStack item = new ItemStack(id, 1, (short)data);
                final ItemMeta meta = item.getItemMeta();
                final List<String> lore = new ArrayList<String>();
                for (final String l : c.getStringList("lore")) {
                    lore.add(l.replaceAll("&", "§"));
                }
                meta.setLore((List)lore);
                meta.setDisplayName(display);
                item.setItemMeta(meta);
                int slot = this.getSlot();
                if (c.get("slot") == null) {
                    c.set("slot", (Object)this.getSlot());
                    c.save(f);
                }
                else {
                    slot = c.getInt("slot");
                }
                this.template.setItem(slot, item);
                this.categoria.put(nome, c);
                final Inventory categoria = Bukkit.createInventory((InventoryHolder)null, 45, nome);
                this.category_acess.put(item, categoria);
                Main.debug("Categoria " + nome + " carregada.");
                Produtos.loadProdutos(c, categoria, nome);
            }
            else if (Main.getInstance().getPlano().equalsIgnoreCase("Basico")) {
                final ItemStack item2 = new ItemStack(Material.IRON_FENCE);
                final ItemMeta meta2 = item2.getItemMeta();
                meta2.setDisplayName("§c-/-");
                item2.setItemMeta(meta2);
                int slot2 = this.getSlot();
                if (c.get("slot") == null) {
                    c.set("slot", (Object)this.getSlot());
                    c.save(f);
                }
                else {
                    slot2 = c.getInt("slot");
                }
                this.template.setItem(slot2, item2);
            }
            else {
                final int id2 = Main.getInstance().getPerson().getConfig().getInt("Menu_Principal.categoria-desativada.id");
                final int data2 = Main.getInstance().getPerson().getConfig().getInt("Menu_Principal.categoria-desativada.data");
                final String nom = Main.getInstance().getPerson().getConfig().getString("Menu_Principal.categoria-desativada.nome").replaceAll("&", "§");
                final ItemStack item = new ItemStack(Material.getMaterial(id2), 1, (short)(byte)data2);
                final ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(nom);
                item.setItemMeta(meta);
                int slot3 = this.getSlot();
                if (c.get("slot") == null) {
                    c.set("slot", (Object)this.getSlot());
                    c.save(f);
                }
                else {
                    slot3 = c.getInt("slot");
                }
                this.template.setItem(slot3, item);
            }
        }
        catch (Exception e) {
            Main.debug("§cFalha ao ler categoria " + nome);
            e.printStackTrace();
        }
    }
    
    public void loadTemplate() {
        if (Main.getInstance().getPlano().equalsIgnoreCase("Essencial") || Main.getInstance().getPlano().equalsIgnoreCase("Completo")) {
            Main.getInstance().perso = new ConfigAccesor(Main.getInstance(), "personaliza\u00e7ao.yml");
            Main.getInstance().getPerson().saveDefaultConfig();
            this.loadLaterais(true);
        }
        else {
            this.loadLaterais(false);
        }
    }
    
    private void loadLaterais(final boolean b) {
        if (b) {
            final FileConfiguration c = Main.getInstance().getPerson().getConfig();
            ItemStack item = new ItemStack(Material.getMaterial(102));
            try {
                final int id = c.getInt("Menu_Principal.outros.laterais.id");
                final int data = c.getInt("Menu_Principal.outros.laterais.data");
                final String nome = c.getString("Menu_Principal.outros.laterais.nome").replaceAll("&", "§");
                item = new ItemStack(Material.getMaterial(id), 1, (short)data);
                final ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(nome);
                item.setItemMeta(meta);
            }
            catch (Exception e) {
                Main.debug("Nao foi possivel criar a personalizacao do menu gui.");
                e.printStackTrace();
            }
            if (!item.hasItemMeta() && !item.getItemMeta().hasDisplayName()) {
                final ItemMeta meta2 = item.getItemMeta();
                meta2.setDisplayName(" ");
                item.setItemMeta(meta2);
            }
            final int[] slots = { 0, 1, 3, 5, 7, 8, 9, 17, 36, 44, 45, 46, 48, 49, 50, 52, 53 };
            int[] array;
            for (int length = (array = slots).length, j = 0; j < length; ++j) {
                final int i = array[j];
                this.template.setItem(i, item);
            }
        }
        else if (Material.getMaterial(160) != null) {
            final ItemStack item2 = new ItemStack(Material.getMaterial(160), 1, (short)9);
            final ItemMeta meta3 = item2.getItemMeta();
            meta3.setDisplayName(" ");
            item2.setItemMeta(meta3);
            final int[] slots = { 0, 1, 3, 5, 7, 8, 9, 17, 36, 44, 45, 46, 48, 49, 50, 52, 53 };
            int[] array2;
            for (int length2 = (array2 = slots).length, k = 0; k < length2; ++k) {
                final int i = array2[k];
                this.template.setItem(i, item2);
            }
        }
        else {
            final ItemStack item2 = new ItemStack(Material.getMaterial(102));
            final ItemMeta meta3 = item2.getItemMeta();
            meta3.setDisplayName(" ");
            item2.setItemMeta(meta3);
            final int[] slots = { 0, 1, 3, 5, 7, 8, 9, 17, 36, 44, 45, 46, 48, 49, 50, 52, 53 };
            int[] array3;
            for (int length3 = (array3 = slots).length, l = 0; l < length3; ++l) {
                final int i = array3[l];
                this.template.setItem(i, item2);
            }
        }
    }
    
    public Inventory getTemplate() {
        return this.template;
    }
    
    private int getSlot() {
        final int[] i = { 20, 21, 22, 23, 24, 29, 30, 31, 32, 33 };
        int[] array;
        for (int length = (array = i).length, j = 0; j < length; ++j) {
            final int a = array[j];
            if (this.template.getItem(a) == null || this.template.getItem(a).getType() == Material.AIR) {
                return a;
            }
        }
        return -1;
    }
    
    public void openShop(final Player p) {
        final Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, Main.getInstance().getConfig().getString("gui-name").replaceAll("&", "§"));
        inv.setContents(this.template.getContents());
        Label_0293: {
            if (!Main.getInstance().getPlano().equalsIgnoreCase("Essencial")) {
                if (!Main.getInstance().getPlano().equalsIgnoreCase("Completo")) {
                    final ItemStack item = new ItemStack(Material.GOLD_INGOT);
                    final ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName("§fVoc\u00ea possui §6" + Main.getInstance().getAPI().getPontos(p.getName()) + " pontos");
                    item.setItemMeta(meta);
                    inv.setItem(4, item);
                    break Label_0293;
                }
            }
            try {
                final FileConfiguration c = Main.getInstance().getPerson().getConfig();
                final int id = c.getInt("Menu_Principal.mostrar-pontos.id");
                final int data = c.getInt("Menu_Principal.mostrar-pontos.data");
                final String nome = c.getString("Menu_Principal.mostrar-pontos.nome").replaceAll("&", "§");
                final ItemStack item2 = new ItemStack(Material.getMaterial(id), 1, (short)data);
                final ItemMeta meta2 = item2.getItemMeta();
                meta2.setDisplayName(nome.replaceAll("%points%", new StringBuilder().append(Main.getInstance().getAPI().getPontos(p.getName())).toString()));
                item2.setItemMeta(meta2);
                inv.setItem(4, item2);
            }
            catch (NullPointerException e) {
                e.printStackTrace();
                Main.debug("Verifique a configuracao 'personaliza\u00e7ao.yml', possui algo de errado com o icone 'mostrar-pontos'");
            }
        }
        p.openInventory(inv);
    }
}
