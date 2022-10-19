package com.trende2001.manhunt.listeners;

import com.trende2001.manhunt.Main;
import com.trende2001.manhunt.utils.Methods;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MCChatEvent implements Listener {
    private Main plugin;

    public MCChatEvent(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.getConfig().getBoolean("discordMcChat")) {
            TextChannel chatChannel = this.plugin.bot.getTextChannelById(this.plugin.getConfig().getString("discordMcChannel"));
            if (chatChannel != null) {
                chatChannel.sendMessage("**" + player.getDisplayName() + "** " + event.getMessage()).queue();
            } else {
                this.plugin.getLogger().warning("Discord channel is null. Please provide a valid channel ID");
            }
        }
        if (this.plugin.isHunter(player)) {
            if (this.plugin.getConfig().getString("teamchatPrefix") == null) {
                player.sendMessage(ChatColor.RED + "No teamchat prefix is set in the configuration file. Please set in order to use teamchat");
                return;
            }
            if (event.getMessage().startsWith(this.plugin.getConfig().getString("teamchatPrefix"))) {
                event.setCancelled(true);
                String original = this.plugin.getConfig().getString("hunterTeamchatMessage");
                String message = original.replaceAll("%player%", player.getDisplayName()).replaceAll("%message%", event.getMessage().substring(1));
                String normal = "&c&l[Hunter Chat] &c%player%: &6&l%message%";
                String msg = normal.replaceAll("%player%", player.getDisplayName()).replaceAll("%message%", event.getMessage().substring(1));
                for (String names : this.plugin.hunters) {
                    Player hunter = Bukkit.getPlayer(names);
                    if (original.equals("%default%")) {
                        hunter.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                        continue;
                    }
                    hunter.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            }
        } else if (this.plugin.isRunner(player)) {
            if (this.plugin.getConfig().getString("teamchatPrefix") == null) {
                player.sendMessage(ChatColor.RED + "No teamchat prefix is set in the configuration file. Please set in order to use teamchat");
                return;
            }
            if (event.getMessage().startsWith(this.plugin.getConfig().getString("teamchatPrefix"))) {
                event.setCancelled(true);
                String original = this.plugin.getConfig().getString("runnerTeamchatMessage");
                String message = original.replaceAll("%player%", player.getDisplayName()).replaceAll("%message%", event.getMessage().substring(1));
                String normal = "&a&l[Runner Chat] &a%player%: &b&l%message%";
                String msg = normal.replaceAll("%player%", player.getDisplayName()).replaceAll("%message%", event.getMessage().substring(1));
                for (String names : this.plugin.speedRunners) {
                    Player runner = Bukkit.getPlayer(names);
                    if (original.equals("%default%")) {
                        runner.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                        continue;
                    }
                    runner.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            }
        } else if (this.plugin.deadRunners.contains(player.getName()) && this.plugin.inGame) {
            event.setCancelled(true);
            for (String names : this.plugin.deadRunners) {
                Player dead = Bukkit.getPlayer(names);
                dead.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[Spectator Chat] " + player.getDisplayName() + ":" + event.getMessage()));
            }
        }
    }
}
