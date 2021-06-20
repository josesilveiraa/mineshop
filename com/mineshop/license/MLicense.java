package com.mineshop.license;

import com.mineshop.*;

public class MLicense
{
    private String key;
    private String plano;
    private String valor;
    private String planoNome;
    private boolean sucess;
    private boolean blocked;
    
    public MLicense(final String key) {
        this.key = "Crackeado";
        this.plano = "3";
        this.valor = "Crackeado";
        this.planoNome = "Completo";
        this.sucess = true;
        this.blocked = false;
        this.key = key;
    }
    
    public void check() {
    }
    
    public void initPlan() {
        this.plano = "3";
        Main.getInstance().setPlano("Completo");
    }
    
    public void auth() {
        Main.debug("URL: §ahttps://google.com");
        Main.debug("Plano: §a" + this.planoNome);
        this.sucess = true;
    }
    
    public boolean isPago() {
        return true;
    }
    
    public boolean existe() {
        return true;
    }
    
    public boolean sucess() {
        return true;
    }
    
    public String getCode() {
        return "0194-2959-3059";
    }
    
    public boolean isBlocked() {
        return false;
    }
}
