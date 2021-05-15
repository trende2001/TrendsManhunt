package com.trende2001.manhunt.listeners;

import com.trende2001.manhunt.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class DropRespawnEvent implements Listener {
    private Main plugin;

    public DropRespawnEvent(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.isHunter(player))
            player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.COMPASS) });
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (this.plugin.isHunter(event.getPlayer()) && event.getItemDrop().getItemStack().getType() == Material.COMPASS)
            event.setCancelled(true);
    }
}