package com.trende2001.manhunt.listeners;


import com.trende2001.manhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDamageEvent implements Listener {
    private Main plugin;

    public PlayerDamageEvent(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.isHunter(player)) {
            this.plugin.hunters.remove(player.getName());
        } else if (this.plugin.isRunner(player)) {
            this.plugin.speedrunners.remove(player.getName());
        }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player && this.plugin.waitingrunner) {
            Player runner = (Player)event.getDamager();
            Player hunter = (Player)event.getEntity();
            if (this.plugin.isRunner(runner) && this.plugin.isHunter(hunter)) {
                Bukkit.broadcastMessage(ChatColor.GOLD + "The hunt has begun!");
                this.plugin.waitingrunner = false;
                this.plugin.ingame = true;
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            if (this.plugin.isHunter(player) && this.plugin.counting) {
                event.setCancelled(true);
            }
        }
    }
}
