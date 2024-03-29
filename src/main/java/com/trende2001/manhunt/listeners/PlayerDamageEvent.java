package com.trende2001.manhunt.listeners;


import com.trende2001.manhunt.Main;
import com.trende2001.manhunt.api.HuntStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerDamageEvent implements Listener {
    private Main plugin;

    private List<String> wasRunner = new ArrayList<>();

    private List<String> wasHunter = new ArrayList<>();

    public PlayerDamageEvent(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.isHunter(player)) {
            this.plugin.hunters.remove(player.getName());
            this.wasHunter.add(player.getName());
        } else if (this.plugin.isRunner(player)) {
            this.plugin.speedRunners.remove(player.getName());
            this.wasRunner.add(player.getName());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.wasRunner.contains(player.getName())) {
            this.plugin.speedRunners.add(player.getName());
            this.wasRunner.remove(player.getName());
        } else if (this.wasHunter.contains(player.getName())) {
            this.plugin.hunters.add(player.getName());
            this.wasHunter.remove(player.getName());
        }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player &&
                this.plugin.waitingRunner) {
            Player runner = (Player)event.getDamager();
            Player hunter = (Player)event.getEntity();
            if (this.plugin.isRunner(runner) && this.plugin.isHunter(hunter)) {
                Bukkit.broadcastMessage(ChatColor.GOLD + "The hunt has begun!");
                this.plugin.waitingRunner = false;
                this.plugin.inGame = true;
                HuntStartEvent gameStartEvent = new HuntStartEvent();
                Bukkit.getServer().getPluginManager().callEvent((Event)gameStartEvent);
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            if (this.plugin.isHunter(player) && this.plugin.countingDown)
                event.setCancelled(true);
        }
    }
}
