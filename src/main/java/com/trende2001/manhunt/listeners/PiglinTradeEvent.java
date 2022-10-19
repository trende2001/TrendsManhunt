package com.trende2001.manhunt.listeners;

import com.trende2001.manhunt.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Piglin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class PiglinTradeEvent implements Listener {
    private Main plugin;

    public PiglinTradeEvent(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTrade(EntityDropItemEvent event) {
        // If it's not a piglin, return
        if (!(event.getEntity() instanceof Piglin))
            return;
        // If we do get an ender pearl, stop here
        if (event.getItemDrop().getItemStack().getType() == Material.ENDER_PEARL)
            return;

        // Okay, boost the ender pearl rate in piglin trading
        if (this.plugin.inGame && this.plugin.getConfig().getBoolean("piglinPearlBoost")) {
            Piglin piglin = (Piglin)event.getEntity();
            Random random = new Random();

            int chance = random.nextInt(99);
            // If chance is above or equal to 10, return
            if (chance >= 10)
                return;

            int quantityDropped = random.nextInt(2) + 2;
            Location itemLoc = event.getItemDrop().getLocation();

            event.getItemDrop().setItemStack(new ItemStack(Material.ENDER_PEARL));
            event.getItemDrop().getItemStack().setAmount(quantityDropped);
        }
    }
}
