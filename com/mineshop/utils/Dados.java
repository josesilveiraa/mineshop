package com.mineshop.utils;

import java.security.*;

public class Dados
{
    private String user;
    private String senha;
    private String db;
    
    public Dados() {
        this.user = "";
        this.senha = "";
        this.db = "";
    }
    
    public String getUser() {
        return this.user;
    }
    
    public String getSenha() {
        return this.senha;
    }
    
    public String getDb() {
        return this.db;
    }
    
    public void init() throws Exception {
        this.user = "root";
        this.senha = "";
        this.db = "mineshop";
    }
    
    private String a(final String s) throws Exception {
        final MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] output = md.digest();
        final String input = s;
        md.update(input.getBytes());
        output = md.digest();
        final String valorCriptografado = this.bytesToHex(output);
        return valorCriptografado;
    }
    
    private String bytesToHex(final byte[] b) {
        final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        final StringBuffer buf = new StringBuffer();
        for (int j = 0; j < b.length; ++j) {
            buf.append(hexDigit[b[j] >> 4 & 0xF]);
            buf.append(hexDigit[b[j] & 0xF]);
        }
        return buf.toString().toLowerCase();
    }
}
