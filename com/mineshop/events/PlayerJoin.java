package com.mineshop.events;

import org.bukkit.event.player.*;
import com.mineshop.system.pontos.*;
import com.mineshop.*;
import org.bukkit.scheduler.*;
import org.bukkit.entity.*;
import com.mineshop.utils.*;
import org.bukkit.plugin.*;
import org.bukkit.event.*;

public class PlayerJoin implements Listener
{
    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        final Thread th = new TUpdater(p.getName());
        th.start();
        if (p.isOp() && Main.getInstance().hasUpdate()) {
            new BukkitRunnable() {
                public void run() {
                    p.sendMessage("");
                    p.sendMessage("§6[MineSHOP] §eHey, uma nova vers\u00e3o est\u00e1 dispon\u00edvel! §fv" + CheckUpdates.versaorecente + "§e.");
                    p.sendMessage("§6[MineSHOP] §eAcesse: §f" + Main.getInstance().getConfig().getString("Dominio") + "/admin§e para baixa-la.");
                    p.sendMessage("§6[MineSHOP] §e\u00c9 recomendado que voc\u00ea utilize a vers\u00e3o mais recente para evitar problemas.");
                    p.sendMessage("");
                }
            }.runTaskLater((Plugin)Main.getInstance(), 20L);
        }
    }
}
