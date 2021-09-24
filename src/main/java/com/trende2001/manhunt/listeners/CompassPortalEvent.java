package com.trende2001.manhunt.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.bukkit.StructureType;
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
    private Main plugin;

    private HashMap<UUID, Location> portal = new HashMap<>();

    private Player spedrun;

    private Player currentRunner;

    private Location netherfortress;

    private ItemStack normalcompass = new ItemStack(Material.COMPASS);

    private ItemMeta normalmeta = this.normalcompass.getItemMeta();

    public CompassPortalEvent(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.speedrunners.size() > 0) {
            if (this.plugin.isHunter(player) &&
                    this.plugin.speedrunners.size() > 1 && (
                    player.getInventory().getItemInMainHand().getType() == Material.COMPASS || player.getInventory().getItemInOffHand().getType() == Material.COMPASS)) {
                int playernum = this.plugin.huntersNumber.get(player.getDisplayName()).intValue();
                String name = this.plugin.speedrunners.get(playernum);
                this.currentRunner = Bukkit.getPlayer(name);
            }
            if (this.plugin.isHunter(player) && (
                    event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) &&
                    player.getInventory().getItemInMainHand().getType() == Material.COMPASS &&
                    this.plugin.getServer().getOnlinePlayers().size() > 1 && this.plugin.speedrunners.size() > 1) {
                int currentPlayer = this.plugin.huntersNumber.get(player.getDisplayName()).intValue();
                this.plugin.huntersNumber.put(player.getDisplayName(), Integer.valueOf((currentPlayer + 1) % this.plugin.speedrunners.size()));
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "No longer hunting: " + this.currentRunner.getDisplayName()));
            }
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                if (this.plugin.isHunter(player) &&
                        player.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
                    ItemStack compass = player.getInventory().getItemInMainHand();
                    CompassMeta compassmeta = (CompassMeta)compass.getItemMeta();
                    if (this.plugin.speedrunners.size() > 1) {
                        Location hunterloc = player.getLocation();
                        if (this.currentRunner == null) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_RED + "Player not found. Left click to hunt the other runner."));
                            return;
                        }
                        if (player.getWorld().getEnvironment() == this.currentRunner.getWorld().getEnvironment()) {
                            Location runnerloc = this.currentRunner.getLocation();
                            int blocks = (int)Math.round(hunterloc.distance(runnerloc));
                            if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
                                try {
                                    compassmeta.setLodestoneTracked(false);
                                } catch (NullPointerException e) {
                                    player.sendMessage(ChatColor.RED + "Something went wrong with the compass. Please contact the owner of the plugin, and send the error in the console if there is one!");
                                    return;
                                }
                                compassmeta.setLodestone(this.currentRunner.getLocation());
                                compass.setItemMeta((ItemMeta)compassmeta);
                                if (this.plugin.getConfig().getBoolean("distanceTracker")) {
                                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7&lDistance: &b&l" + blocks + "m &f- &7&lHunting: &a&l" + this.currentRunner.getDisplayName())));
                                    return;
                                }
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Hunting: " + this.currentRunner.getDisplayName()));
                                return;
                            }
                            if (compassmeta.hasLodestone())
                                compass.setItemMeta(this.normalmeta);
                            player.setCompassTarget(this.currentRunner.getLocation());
                            if (this.plugin.getConfig().getBoolean("distanceTracker")) {
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7&lDistance: &b&l" + blocks + "m &f- &7&lHunting: &a&l" + this.currentRunner.getDisplayName())));
                                return;
                            }
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Hunting: " + this.currentRunner.getDisplayName()));
                            return;
                        }
                        if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
                            Location portaloc = this.portal.get(this.currentRunner.getUniqueId());
                            if (portaloc == null) {
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Could not track portal of runner"));
                                return;
                            }
                            int distance = (int)Math.round(hunterloc.distance(portaloc));
                            if (compassmeta.hasLodestone())
                                compass.setItemMeta(this.normalmeta);
                            player.setCompassTarget(portaloc);
                            if (this.plugin.getConfig().getBoolean("distanceTracker")) {
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7&lDistance: &b&l" + distance + "m &f- &7&lHunting: &a&l" + this.currentRunner.getDisplayName() + "'s portal")));
                                return;
                            }
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Hunting: " + this.currentRunner.getDisplayName() + "'s portal"));
                            return;
                        }
                        if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "No runner in The Nether to track"));
                            return;
                        }
                        if (player.getWorld().getEnvironment() == World.Environment.THE_END)
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "No runner in The End to track"));
                    }
                    if (this.plugin.speedrunners.size() == 1) {
                        for (String runnername : this.plugin.speedrunners)
                            this.spedrun = Bukkit.getPlayer(runnername);
                        Location hunterloc = player.getLocation();
                        if (player.getWorld() == this.spedrun.getWorld()) {
                            Location runnerloc = this.spedrun.getLocation();
                            int blocks = (int)Math.round(hunterloc.distance(runnerloc));
                            if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
                                try {
                                    compassmeta.setLodestoneTracked(false);
                                } catch (NullPointerException e) {
                                    player.sendMessage(ChatColor.RED + "Something went wrong with the compass. Please contact the owner of the plugin, and send the error in the console if there is one!");
                                    return;
                                }
                                compassmeta.setLodestone(this.spedrun.getLocation());
                                compass.setItemMeta((ItemMeta)compassmeta);
                                if (this.plugin.getConfig().getBoolean("distanceTracker")) {
                                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7&lDistance: &b&l" + blocks + "m &f- &7&lHunting: &a&l" + this.spedrun.getDisplayName())));
                                    return;
                                }
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(ChatColor.GREEN + "Hunting: " + this.spedrun.getDisplayName()));
                                return;
                            }
                            if (compassmeta.hasLodestone())
                                compass.setItemMeta(this.normalmeta);
                            player.setCompassTarget(this.spedrun.getLocation());
                            if (this.plugin.getConfig().getBoolean("distanceTracker")) {
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7&lDistance: &b&l" + blocks + "m &f- &7&lHunting: &a&l" + this.spedrun.getDisplayName())));
                                return;
                            }
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(ChatColor.GREEN + "Hunting: " + this.spedrun.getDisplayName()));
                            return;
                        }
                        if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
                            Location portaloc = this.portal.get(this.spedrun.getUniqueId());
                            int distance = (int)Math.round(hunterloc.distance(portaloc));
                            if (compassmeta.hasLodestone())
                                compass.setItemMeta(this.normalmeta);
                            player.setCompassTarget(portaloc);
                            if (this.plugin.getConfig().getBoolean("distanceTracker")) {
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7&lDistance: &b&l" + distance + "m &f- &7&lHunting: &a&l" + this.spedrun.getDisplayName() + "'s portal")));
                                return;
                            }
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(ChatColor.GREEN + "Hunting: " + this.spedrun.getDisplayName() + "'s portal"));
                            return;
                        }
                        if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(ChatColor.RED + "No runner in The Nether to track"));
                            return;
                        }
                        if (player.getWorld().getEnvironment() == World.Environment.THE_END)
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(ChatColor.RED + "No runner in The End to track"));
                    }
                }
                if (!this.plugin.ingame)
                    return;
                if (!this.plugin.isRunner(player))
                    return;
                if (player.getInventory().getItemInMainHand().getType() != Material.COMPASS)
                    return;
                if (!player.getInventory().getItemInMainHand().hasItemMeta())
                    return;
                if (player.getInventory().getItemInMainHand().getItemMeta().getLore() != null &&
                        player.getWorld().getEnvironment() == World.Environment.NETHER) {
                    ItemStack compass = player.getInventory().getItemInMainHand();
                    CompassMeta compassMeta = (CompassMeta)compass.getItemMeta();
                    if (!compassMeta.hasLodestone()) {
                        Location fortress = player.getWorld().locateNearestStructure(player.getLocation(), StructureType.NETHER_FORTRESS, 2147483647, true);
                        this.netherfortress = fortress;
                    }
                    try {
                        compassMeta.setLodestoneTracked(false);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        player.sendMessage(ChatColor.RED + "Something went wrong with the compass. Please contact the author, and send the error in the console");
                        return;
                    }
                    compassMeta.setLodestone(this.netherfortress);
                    compass.setItemMeta((ItemMeta)compassMeta);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(ChatColor.GREEN + "Pointing to nearest Nether Fortress"));
                }
            }
        }
        if (this.plugin.isHunter(player) && player.getInventory().getItemInMainHand().getType() == Material.COMPASS && (
                event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, (BaseComponent)new TextComponent(ChatColor.RED + "No players to track"));
    }

    @EventHandler
    public void onPortal(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.isRunner(player)) {
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL || event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL)
                this.portal.put(player.getUniqueId(), player.getLocation());
            if (this.plugin.ingame &&
                    this.plugin.getConfig().getBoolean("fortressTracker") &&
                    event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL &&
                    player.getWorld().getEnvironment() == World.Environment.NORMAL) {
                if (player.getInventory().firstEmpty() == -1) {
                    player.sendMessage(ChatColor.GREEN + "Your inventory is full. The fortress tracker has been dropped on the ground");
                    player.getWorld().dropItemNaturally(player.getLocation(), getFortressTracker());
                    return;
                }
                player.getInventory().addItem(new ItemStack[] { getFortressTracker() });
            }
        }
    }

    private ItemStack getFortressTracker() {
        ItemStack fortresstracker = new ItemStack(Material.COMPASS);
        ItemMeta meta = fortresstracker.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Fortress Tracker");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Right click to track the nearest fortress");
        meta.setLore(lore);
        fortresstracker.setItemMeta(meta);
        return fortresstracker;
    }
}