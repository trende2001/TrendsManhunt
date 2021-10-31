package com.trende2001.manhunt.listeners;

import com.trende2001.manhunt.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
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
        if (this.plugin.speedrunners.size() == 1 &&
                this.plugin.isRunner(player) &&
                this.plugin.ingame) {
            Bukkit.broadcastMessage(ChatColor.GREEN + player.getDisplayName() + " has been killed!");
            event.setDeathMessage(ChatColor.GREEN + "Hunters win! Do /huntgame start to play again!");
            if (this.plugin.deadrunners.size() > 0) {
                for (String name : this.plugin.deadrunners) {
                    Player deadRunner = Bukkit.getPlayer(name);
                    if (deadRunner.isOnline())
                        this.plugin.speedrunners.add(name);
                }
                this.plugin.deadrunners.clear();
            }
            this.plugin.ingame = false;
        }
    }

    @EventHandler
    public void onLethalHit(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            if (this.plugin.speedrunners.size() > 1 && this.plugin.ingame &&
                    this.plugin.isRunner(player) &&
                    event.getFinalDamage() > player.getHealth()) {
                event.setCancelled(true);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_DEATH, 1.0F, 1.0F);
                if (this.plugin.getConfig().getString("particleEffect") != null && !this.plugin.getConfig().getString("particleEffect").equals("NONE"))
                    try {
                        player.getWorld().spawnParticle(Particle.valueOf(this.plugin.getConfig().getString("particleEffect")), player.getLocation(), 20);
                    } catch (Exception e) {
                        this.plugin.getLogger().severe("Particle was invalid. Playing default particle instead.");
                        player.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, player.getLocation(), 20);
                    }
                player.setGameMode(GameMode.SPECTATOR);
                Bukkit.broadcastMessage(ChatColor.GREEN + player.getDisplayName() + " has been killed!");
                for (ItemStack i : player.getInventory().getContents()) {
                    if (i != null)
                        player.getWorld().dropItemNaturally(player.getLocation(), i);
                }
                this.plugin.speedrunners.remove(player.getName());
                this.plugin.deadrunners.add(player.getName());
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
        if (event.getEntity() instanceof org.bukkit.entity.EnderDragon && this.plugin.ingame) {
            for (String name : this.plugin.hunters) {
                Player hunter = Bukkit.getPlayer(name);
                hunter.sendTitle(ChatColor.AQUA + "The Ender Dragon died!", ChatColor.LIGHT_PURPLE + "The speedrunner wins!", 5, 25, 5);
            }
            for (String name : this.plugin.speedrunners) {
                Player runner = Bukkit.getPlayer(name);
                runner.sendTitle(ChatColor.AQUA + "The Ender Dragon died!", ChatColor.LIGHT_PURPLE + "The speedrunner wins!", 5, 25, 5);
            }
            Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "The speedrunner wins! Do /huntgame start to play again!");
            this.plugin.ingame = false;
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (this.plugin.counting && this.plugin
                .isHunter(event.getPlayer()))
            event.setCancelled(true);
    }
}