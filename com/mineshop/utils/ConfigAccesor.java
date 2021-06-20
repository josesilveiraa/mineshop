package com.mineshop.utils;

import org.bukkit.plugin.java.*;
import org.bukkit.configuration.file.*;
import java.util.logging.*;
import java.io.*;

public class ConfigAccesor
{
    private final String fileName;
    private final JavaPlugin plugin;
    private File configFile;
    private FileConfiguration fileConfiguration;
    
    public ConfigAccesor(final JavaPlugin plugin, final String fileName) {
        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }
        if (!plugin.isEnabled()) {
            throw new IllegalArgumentException("plugin must be initialized");
        }
        this.plugin = plugin;
        this.fileName = fileName;
        final File dataFolder = plugin.getDataFolder();
        if (dataFolder == null) {
            throw new IllegalStateException();
        }
        this.configFile = new File(plugin.getDataFolder(), fileName);
    }
    
    public void reloadConfig() {
        this.saveDefaultConfig();
        this.fileConfiguration = (FileConfiguration)YamlConfiguration.loadConfiguration(this.configFile);
    }
    
    public FileConfiguration getConfig() {
        if (this.fileConfiguration == null) {
            this.reloadConfig();
        }
        return this.fileConfiguration;
    }
    
    public void saveConfig() {
        if (this.fileConfiguration == null || this.configFile == null) {
            return;
        }
        try {
            this.getConfig().save(this.configFile);
        }
        catch (IOException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, ex);
        }
    }
    
    public void saveDefaultConfig() {
        if (!this.configFile.exists()) {
            this.plugin.saveResource(this.fileName, false);
        }
    }
}
