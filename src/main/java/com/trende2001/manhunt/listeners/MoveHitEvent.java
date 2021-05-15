package com.trende2001.manhunt.listeners;


import com.trende2001.manhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveHitEvent implements Listener {
    private Main plugin;

    public MoveHitEvent(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.isHunter(player) &&
                this.plugin.counting)
            event.setTo(event.getFrom());
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player runner = (Player)event.getDamager();
            Player hunter = (Player)event.getEntity();
            if (this.plugin.isRunner(runner) && this.plugin.isHunter(hunter) &&
                    this.plugin.waitingrunner) {
                Bukkit.broadcastMessage(ChatColor.GOLD + "The hunt has begun!");
                this.plugin.waitingrunner = false;
                this.plugin.ingame = true;
            }
        }
    }
}
