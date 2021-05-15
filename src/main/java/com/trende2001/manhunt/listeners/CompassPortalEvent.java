package com.trende2001.manhunt.listeners;

import java.util.HashMap;
import java.util.UUID;
import com.trende2001.manhunt.Main;
import com.trende2001.manhunt.utils.Methods;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class CompassPortalEvent implements Listener {
    private HashMap<UUID, Location> portal = new HashMap<>();

    private Main plugin;

    private Player spedrun;

    private ItemStack normalcompass = new ItemStack(Material.COMPASS);

    private ItemMeta normalmeta = this.normalcompass.getItemMeta();

    public CompassPortalEvent(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) &&
                this.plugin.isHunter(player) && player.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
            if (this.plugin.getServer().getOnlinePlayers().size() == 1 || this.plugin.speedrunners.size() == 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(ChatColor.RED + "No players to track"));
                return;
            }
            if (this.plugin.speedrunners.size() == 1) {
                for (String runnername : this.plugin.speedrunners)
                    this.spedrun = Bukkit.getPlayer(runnername);
                Location hunterloc = player.getLocation();
                if (player.getWorld() == this.spedrun.getWorld()) {
                    Location runnerloc = this.spedrun.getLocation();
                    int blocks = (int)Math.round(hunterloc.distance(runnerloc));
                    ItemStack compass = player.getInventory().getItemInMainHand();
                    CompassMeta meta = (CompassMeta)compass.getItemMeta();
                    if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
                        try {
                            meta.setLodestoneTracked(false);
                        } catch (NullPointerException e) {
                            player.sendMessage(ChatColor.RED + "Something went wrong with the compass. Please contact the author, and send the error in the console if there is one");
                            return;
                        }
                        meta.setLodestone(this.spedrun.getLocation());
                        compass.setItemMeta((ItemMeta)meta);
                        if (this.plugin.getConfig().getBoolean("distanceTracker")) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(Methods.color("&7&lDistance: &b&l" + blocks + "m &f- &7&lTracking: &a&l" + this.spedrun.getDisplayName())));
                            return;
                        }
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(ChatColor.GREEN + "Hunting: " + this.spedrun.getDisplayName()));
                        return;
                    }
                    compass.setItemMeta(this.normalmeta);
                    player.setCompassTarget(this.spedrun.getLocation());
                    if (this.plugin.getConfig().getBoolean("distanceTracker")) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(Methods.color("&7&lDistance: &b&l" + blocks + "m &f- &7&lTracking: &a&l" + this.spedrun.getDisplayName())));
                        return;
                    }
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(ChatColor.GREEN + "Hunting: " + this.spedrun.getDisplayName()));
                    return;
                }
                if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
                    Location portaloc = this.portal.get(this.spedrun.getUniqueId());
                    int distance = (int)Math.round(hunterloc.distance(portaloc));
                    player.setCompassTarget(portaloc);
                    if (this.plugin.getConfig().getBoolean("distanceTracker")) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(Methods.color("&7&lDistance: &b&l" + distance + "m &f- &7&lTracking: &a&l" + this.spedrun.getDisplayName() + "'s portal")));
                        return;
                    }
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(ChatColor.GREEN + "Hunting: " + this.spedrun.getDisplayName()));
                    return;
                }
                if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(ChatColor.RED + "No runner in the nether to track"));
                    return;
                }
                if (player.getWorld().getEnvironment() == World.Environment.THE_END)
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(ChatColor.RED + "No runner in the end to track"));
            }
            player.sendMessage(ChatColor.YELLOW + "Please do not have more than one runner at a time");
        }
    }


    @EventHandler
    public void onPortal(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.isRunner(player) && (
                event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL || event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL))
            this.portal.put(player.getUniqueId(), player.getLocation());
    }

}
