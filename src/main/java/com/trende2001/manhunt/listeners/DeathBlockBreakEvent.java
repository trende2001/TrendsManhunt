package com.trende2001.manhunt.listeners;

import com.trende2001.manhunt.Main;
import com.trende2001.manhunt.api.HuntEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class DeathBlockBreakEvent implements Listener {
    private Main plugin;

    public DeathBlockBreakEvent(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (this.plugin.speedRunners.size() == 1 &&
                this.plugin.isRunner(player) &&
                this.plugin.inGame) {
            String deathMessage = ChatColor.GREEN + event.getDeathMessage();
            event.setDeathMessage(deathMessage);
            if (this.plugin.hunters.size() > 1) {
                Bukkit.broadcastMessage(ChatColor.GREEN + "The hunters win! Do /huntgame start to play again!");
            } else {
                Bukkit.broadcastMessage(ChatColor.GREEN + "The hunter wins! Do /huntgame start to play again!");
            }
            if (this.plugin.deadRunners.size() > 0) {
                for (String name : this.plugin.deadRunners) {
                    Player deadRunner = Bukkit.getPlayer(name);
                    if (deadRunner.isOnline())
                        this.plugin.speedRunners.add(name);
                }
                this.plugin.deadRunners.clear();
            }
            this.plugin.inGame = false;
            HuntEndEvent gameEndEvent = new HuntEndEvent();
            Bukkit.getServer().getPluginManager().callEvent((Event)gameEndEvent);
        }
    }

    @EventHandler
    public void onLethalHit(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            if (this.plugin.speedRunners.size() > 1 && this.plugin.inGame &&
                    this.plugin.isRunner(player) &&
                    event.getFinalDamage() > player.getHealth()) {
                event.setCancelled(true);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_DEATH, 1.0F, 1.0F);
                if (this.plugin.getConfig().getString("particleEffect") != null && !this.plugin.getConfig().getString("particleEffect").equals("NONE"))
                    try {
                        player.getWorld().spawnParticle(Particle.valueOf(this.plugin.getConfig().getString("particleEffect")), player.getLocation(), 20);
                    } catch (Exception e) {
                        this.plugin.getLogger().severe("Particle type is invalid. Playing default particle. Please change it in the config file");
                        player.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, player.getLocation(), 20);
                    }
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0F, 1.0F);
                player.setGameMode(GameMode.SPECTATOR);
                player.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "You were killed!", ChatColor.GRAY + "It is now time to spectate!", 0, 40, 10);
                Bukkit.broadcastMessage(ChatColor.GREEN + player.getDisplayName() + " has been killed!");
                for (ItemStack i : player.getInventory().getContents()) {
                    if (i != null)
                        player.getWorld().dropItemNaturally(player.getLocation(), i);
                }
                this.plugin.speedRunners.remove(player.getName());
                this.plugin.deadRunners.add(player.getName());
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (this.plugin.isHunter(event.getEntity()))
            for (ItemStack i : event.getDrops()) {
                if (i.getType() == Material.COMPASS)
                    i.setType(Material.AIR);
            }
    }

    @EventHandler
    public void onDragonDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.EnderDragon &&
                this.plugin.inGame) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (this.plugin.speedRunners.contains(player.getName()) || this.plugin.hunters.contains(player.getName())) {
                    if (this.plugin.speedRunners.size() == 1) {
                        player.sendTitle(ChatColor.AQUA + "The Dragon died!", ChatColor.LIGHT_PURPLE + "The runner wins!", 5, 100, 5);
                        continue;
                    }
                    player.sendTitle(ChatColor.AQUA + "The Dragon died!", ChatColor.LIGHT_PURPLE + "The runners win!", 5, 100, 5);
                }
            }
            if (this.plugin.speedRunners.size() == 1) {
                Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "The runner wins! Do /huntgame start to play again!");
            } else {
                Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "The runners win! Do /huntgame start to play again!");
            }
            this.plugin.inGame = false;
            HuntEndEvent gameEndEvent = new HuntEndEvent();
            Bukkit.getServer().getPluginManager().callEvent((Event)gameEndEvent);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (this.plugin.countingDown &&
                this.plugin.isHunter(event.getPlayer()))
            event.setCancelled(true);
    }
}
