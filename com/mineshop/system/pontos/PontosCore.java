package com.mineshop.system.pontos;

import java.util.*;

public class PontosCore
{
    private HashMap<String, PPlayer> cache;
    
    public PontosCore() {
        this.cache = new HashMap<String, PPlayer>();
    }
    
    public HashMap<String, PPlayer> getCache() {
        return this.cache;
    }
    
    public PPlayer getCached(final String nick) {
        if (this.cache.containsKey(nick)) {
            return this.cache.get(nick);
        }
        return null;
    }
}
