package com.mineshop.api.listeners;

import org.bukkit.event.*;
import org.bukkit.entity.*;
import com.mineshop.system.pontos.*;
import org.bukkit.inventory.*;

public class ShopBuyEvent extends Event
{
    private static final HandlerList handlers;
    private Player player;
    private PPlayer mplayer;
    private ItemStack icone;
    private String path;
    
    static {
        handlers = new HandlerList();
    }
    
    public ShopBuyEvent(final Player p, final PPlayer mp, final ItemStack icone, final String path) {
        this.path = "";
        this.player = p;
        this.mplayer = mp;
        this.icone = icone;
        this.path = path;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public PPlayer getChachePlayer() {
        return this.mplayer;
    }
    
    public ItemStack getEventItem() {
        return this.icone;
    }
    
    public String getProductName() {
        return this.path;
    }
    
    public HandlerList getHandlers() {
        return ShopBuyEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return ShopBuyEvent.handlers;
    }
}
