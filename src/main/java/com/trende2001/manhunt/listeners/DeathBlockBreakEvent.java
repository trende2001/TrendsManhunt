package com.trende2001.manhunt.listeners;

import com.trende2001.manhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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
            event.setDeathMessage(ChatColor.GREEN + "Hunters win! Do /huntplus start to play again!");
            if (this.plugin.deadrunners.size() > 0) {
                this.plugin.speedrunners.addAll(this.plugin.deadrunners);
                this.plugin.deadrunners.clear();
            }
            this.plugin.ingame = false;
        }
        if (this.plugin.speedrunners.size() > 1 &&
                this.plugin.isRunner(player) &&
                this.plugin.ingame) {
            event.setDeathMessage(ChatColor.GREEN + player.getDisplayName() + " has died!");
            this.plugin.speedrunners.remove(player.getDisplayName());
            this.plugin.deadrunners.add(player.getDisplayName());
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