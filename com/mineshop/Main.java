package com.mineshop;

import org.bukkit.plugin.java.*;
import com.mineshop.utils.*;
import com.mineshop.categoria.*;
import com.mineshop.storage.*;
import com.mineshop.api.*;
import java.io.*;
import java.sql.*;
import com.mineshop.license.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import com.mineshop.cmds.*;
import org.bukkit.command.*;
import org.bukkit.event.*;
import com.mineshop.events.*;
import com.mineshop.system.pontos.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.*;
import java.util.*;
import org.bukkit.configuration.file.*;

public class Main extends JavaPlugin
{
    private static Main plugin;
    private static String plano;
    private String key;
    private Dados data;
    private static ConfigAccesor msgEN;
    private static ConfigAccesor msgBR;
    private static String language;
    public ConfigAccesor perso;
    private static ConfigAccesor cmds;
    private boolean update;
    private static CategoriaCore cc;
    private static PontosCore pc;
    private MySQL mysql;
    private MineSHOP api;
    private HashSet<Connection> conns;
    public static boolean copiando;
    
    public Main() {
        this.key = "";
        this.update = false;
        this.conns = new HashSet<Connection>();
    }
    
    public void onLoad() {
        final File f = new File("plugins" + File.separator + "MineSHOP" + File.separator + "config.yml");
        if (!f.exists()) {
            this.saveDefaultConfig();
            debug("config.yml criada.");
        }
    }
    
    public void onDisable() {
        if (!this.conns.isEmpty()) {
            for (final Connection c : this.conns) {
                try {
                    c.close();
                    debug("Connection closed.");
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            this.conns.clear();
        }
    }
    
    public void onEnable() {
        debug("Inicializando MineSHOP Lite v" + this.getDescription().getVersion());
        Main.plugin = this;
        debug("Verificando sua URL...");
        final MLicense ml = new MLicense("https://google.com");
        ml.check();
        this.key = ml.getCode();
        this.data = new Dados();
        ml.initPlan();
        ml.auth();
        try {
            this.data.init();
        }
        catch (Exception e3) {
            debug("Ocorreu um problema ao inicializar os dados de acesso a API.");
            Bukkit.getPluginManager().disablePlugin((Plugin)this);
            return;
        }
        this.api = new MineSHOP((Plugin)this);
        this.loadConfig();
        this.loadLanguage();
        this.loadCustomCmds();
        Main.cc = new CategoriaCore();
        this.mysql = new MySQL();
        Main.pc = new PontosCore();
        this.getCommand("mineshop").setExecutor((CommandExecutor)new Comandos());
        this.getCommand("pontos").setExecutor((CommandExecutor)new Comandos());
        this.getCommand("shop").setExecutor((CommandExecutor)new Comandos());
        this.getCommand("activate").setExecutor((CommandExecutor)new Comandos());
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerJoin(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new ShopEvents(), (Plugin)this);
        try {
            Connection c = null;
            for (final Player p : Bukkit.getOnlinePlayers()) {
                if (c == null) {
                    c = this.getStorage().getConnection();
                }
                final TUpdater tp = new TUpdater(p.getName(), c);
                tp.start();
            }
            if (c != null) {
                this.conns.add(c);
                this.gamb();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (NoSuchMethodError e2) {
            e2.printStackTrace();
        }
    }
    
    public void loadCustomCmds() {
        if (this.getPlano().equalsIgnoreCase("Essencial") || this.getPlano().equalsIgnoreCase("completo")) {
            (Main.cmds = new ConfigAccesor(this, "customcmds.yml")).saveDefaultConfig();
        }
    }
    
    public boolean isEssencial() {
        return false;
    }
    
    public boolean isCompleto() {
        return true;
    }
    
    public static ConfigAccesor getCmds() {
        return Main.cmds;
    }
    
    public void gamb() {
        new BukkitRunnable() {
            public void run() {
                if (!Main.this.conns.isEmpty()) {
                    for (final Connection c : Main.this.conns) {
                        try {
                            c.close();
                            Main.debug("Connection closed.");
                        }
                        catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    Main.this.conns.clear();
                }
            }
        }.runTaskTimerAsynchronously((Plugin)this, 600L, 60L);
    }
    
    public static CategoriaCore getCC() {
        return Main.cc;
    }
    
    public MineSHOP getAPI() {
        return this.api;
    }
    
    public String getLanguage() {
        return Main.language;
    }
    
    public void setUpdate(final boolean b) {
        this.update = true;
    }
    
    public boolean hasUpdate() {
        return false;
    }
    
    private void loadConfig() {
        final FileConfiguration c = this.getConfig();
        debug("Carregando configuracao...");
        c.addDefault("language", (Object)"br");
        c.addDefault("gui-name", (Object)"MineShop");
        c.addDefault("gerar-logs", (Object)true);
        c.addDefault("back-item", (Object)"&cVoltar (&7Clique&c)");
        final ArrayList<String> lores = new ArrayList<String>();
        lores.add("");
        lores.add("&eNome (&6Produto&e): &f%product name%");
        lores.add("&eCusto (&6Pontos&e): &f%points% points");
        lores.add("&eQuantidade: &f%amount%");
        lores.add("");
        c.addDefault("products-lore", (Object)lores);
        c.addDefault("confirmar.utilizar", (Object)true);
        c.addDefault("confirmar.menu_name", (Object)"Deseja comprar este produto?");
        c.options().copyDefaults(true);
        this.saveConfig();
    }
    
    public static String getMsg(final String msg) {
        if (Main.language.equalsIgnoreCase("br")) {
            return Main.msgBR.getConfig().getString(msg).replaceAll("%prefix%", Main.msgBR.getConfig().getString("prefix").replaceAll("&", "§")).replaceAll("&", "§");
        }
        return Main.msgEN.getConfig().getString(msg).replaceAll("%prefix%", Main.msgEN.getConfig().getString("prefix").replaceAll("&", "§")).replaceAll("&", "§");
    }
    
    private void loadLanguage() {
        Main.msgEN = new ConfigAccesor(this, "messages_en.yml");
        Main.msgBR = new ConfigAccesor(this, "messages_br.yml");
        Main.language = this.getConfig().getString("language");
        if (Main.language.equalsIgnoreCase("en") || Main.language.equalsIgnoreCase("br")) {
            if (Main.language.equalsIgnoreCase("br")) {
                debug("Linguagem definida para: BR");
            }
            if (Main.language.equalsIgnoreCase("en")) {
                debug("Loaded language: EN");
            }
        }
        else {
            debug("Linguagem invalida.");
            Main.language = "br";
        }
        Main.msgBR.getConfig().addDefault("player-points", (Object)"%prefix% O Jogador&6 %player%&f possui &6%points% &fpontos!");
        Main.msgBR.getConfig().addDefault("you-points", (Object)"%prefix% Voce possui &6%points% &fpontos");
        Main.msgBR.getConfig().addDefault("no-permission", (Object)"&cSem permissao");
        Main.msgBR.getConfig().addDefault("player-offline", (Object)"&cJogador nao encontrado");
        Main.msgBR.getConfig().addDefault("unknown", (Object)"&cErro...");
        Main.msgBR.getConfig().addDefault("prefix", (Object)"&6[MineSHOP] &f");
        Main.msgBR.getConfig().addDefault("points-added", (Object)"Voce adicionou: &6%points% pontos &fpara &6%player%");
        Main.msgBR.getConfig().addDefault("code-help", (Object)"&cUtilize /Ativar <code>");
        Main.msgBR.getConfig().addDefault("code-used", (Object)"&cEste codigo ja foi utilizado");
        Main.msgBR.getConfig().addDefault("code-payment", (Object)"&cO Pagamento deste codigo ainda nao foi aprovado.");
        Main.msgBR.getConfig().addDefault("unsuported-code", (Object)"&cEste codigo nao pode ser ativado");
        Main.msgBR.getConfig().addDefault("unknown-refcode", (Object)"&cEste codigo nao existe");
        Main.msgBR.getConfig().addDefault("activated-pv", (Object)"&aParabens! %points% pontos foram depositados em sua conta.");
        Main.msgBR.getConfig().addDefault("activated-broadcast", (Object)"%prefix% O Jogador &6%player% &fativou &6%points% pontos!!");
        Main.msgBR.getConfig().addDefault("points-remove", (Object)"&fVoce removeu &6%points% pontos&f de &f%player%");
        Main.msgBR.getConfig().addDefault("buy", (Object)"&fVoce comprou &6%item% &fpor &6%points% pontos");
        Main.msgBR.getConfig().addDefault("buy-broadcast", (Object)"&f%prefix% &6%player% &fComprou &6%item% &fpor &6%points% pontos&f no shop!");
        Main.msgBR.getConfig().addDefault("insuficient-points", (Object)"&cVoce nao possui pontos suficientes!");
        Main.msgBR.getConfig().addDefault("delay", (Object)"&cPor favor, aguarde para utilizar este comando novamente");
        Main.msgBR.getConfig().addDefault("wait", (Object)"&cPor favor aguarde...");
        Main.msgBR.getConfig().addDefault("process", (Object)"&aProcessando...");
        Main.msgBR.getConfig().addDefault("sendpoints", (Object)"&fVoce enviou &a%points% pontos&f para&a %player%");
        Main.msgBR.getConfig().addDefault("received", (Object)"&fVoce recebeu &a%points% pontos &fde &a%player%");
        Main.msgBR.getConfig().addDefault("nosendpoints", (Object)"&cSomente jogadores VIPs podem enviar pontos.");
        Main.msgBR.getConfig().addDefault("insuficientpoints", (Object)"&cVoce nao possui pontos suficientes para enviar...");
        Main.msgBR.getConfig().addDefault("nosend", (Object)"&cVoce nao pode enviar pontos para voce mesmo.");
        Main.msgBR.getConfig().addDefault("buycanceled", (Object)"&cCompra cancelada");
        Main.msgBR.getConfig().addDefault("inventoryfull", (Object)"&cNao foi possivel completar a compra, seu inventario esta cheio.");
        Main.msgEN.getConfig().addDefault("player-points", (Object)"%prefix% The player&6 %player%&f has &6%points% &fpoints!");
        Main.msgEN.getConfig().addDefault("you-points", (Object)"%prefix% You have &6%points% &fpoints");
        Main.msgEN.getConfig().addDefault("no-permission", (Object)"&cYou don't have permission.");
        Main.msgEN.getConfig().addDefault("player-offline", (Object)"&cPlayer not found");
        Main.msgEN.getConfig().addDefault("unknown", (Object)"&cError...");
        Main.msgEN.getConfig().addDefault("prefix", (Object)"&6[MineSHOP] &f");
        Main.msgEN.getConfig().addDefault("points-added", (Object)"You added: &6%points% points&f to&6 %player%");
        Main.msgEN.getConfig().addDefault("unknown-refcode", (Object)"&cThe code does not exist.");
        Main.msgEN.getConfig().addDefault("code-used", (Object)"&cThis code has already been used.");
        Main.msgEN.getConfig().addDefault("code-payment", (Object)"&cThe payment of this code has not yet been approved.");
        Main.msgEN.getConfig().addDefault("unsuported-code", (Object)"&cThis code can not be activated");
        Main.msgEN.getConfig().addDefault("code-help", (Object)"&cUse /Ativar <code>");
        Main.msgEN.getConfig().addDefault("activated-pv", (Object)"&aCongrulatulations! %points% points was been deposited into your account.");
        Main.msgEN.getConfig().addDefault("activated-broadcast", (Object)"%prefix% The player &6%player% &factivated &6%points% points!!");
        Main.msgEN.getConfig().addDefault("points-remove", (Object)"&fYou removed &6%points% points &fof the player &6%player%");
        Main.msgEN.getConfig().addDefault("buy", (Object)"&fYou bought &6%item% &ffor &6%points% points");
        Main.msgEN.getConfig().addDefault("buy-broadcast", (Object)"&f%prefix% &6%player% &fBought &6%item% &ffor &6%points% points&f in shop!");
        Main.msgEN.getConfig().addDefault("insuficient-points", (Object)"&cYou don't enought points");
        Main.msgEN.getConfig().addDefault("delay", (Object)"&cWait again to use this command");
        Main.msgEN.getConfig().addDefault("wait", (Object)"&cPlease wait...");
        Main.msgEN.getConfig().addDefault("process", (Object)"&aProcessing...");
        Main.msgEN.getConfig().addDefault("sendpoints", (Object)"&fYou sent &a%points% points &ffor &a%player%");
        Main.msgEN.getConfig().addDefault("received", (Object)"&fYou receive &a%points% points &ffrom &a%player%");
        Main.msgEN.getConfig().addDefault("nosendpoints", (Object)"&cOnly VIPs can send points");
        Main.msgEN.getConfig().addDefault("insuficientpoints", (Object)"&cYou dont have a suficient points to send");
        Main.msgEN.getConfig().addDefault("nosend", (Object)"&cYou dont send points to you");
        Main.msgEN.getConfig().addDefault("buycanceled", (Object)"&cBuy canceled");
        Main.msgEN.getConfig().addDefault("inventoryfull", (Object)"&cNao foi possivel completar a compra, seu inventario esta cheio.");
        Main.msgEN.getConfig().options().copyDefaults(true);
        Main.msgEN.saveConfig();
        Main.msgBR.getConfig().options().copyDefaults(true);
        Main.msgBR.saveConfig();
    }
    
    public Dados getData() {
        return this.data;
    }
    
    public static Main getInstance() {
        return Main.plugin;
    }
    
    public static int debug(final String a) {
        if (!a.equalsIgnoreCase("Conectado com sucesso!")) {
            Bukkit.getConsoleSender().sendMessage("§e[MineSHOP Lite] " + a);
        }
        return 76;
    }
    
    public void shutdown() {
        Bukkit.getPluginManager().disablePlugin((Plugin)this);
    }
    
    public void setPlano(final String plano) {
        Main.plano = plano;
    }
    
    public String getPlano() {
        return Main.plano;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public MySQL getStorage() {
        return this.mysql;
    }
    
    public static PontosCore getPontosCore() {
        return Main.pc;
    }
    
    public ConfigAccesor getPerson() {
        return this.perso;
    }
    
    static {
        Main.plano = "Completo";
        Main.language = "";
        Main.copiando = false;
    }
}
