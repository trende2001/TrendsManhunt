package com.trende2001.manhunt.listeners;

import com.trende2001.manhunt.Main;
import com.trende2001.manhunt.utils.Methods;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatEvent implements Listener {
    private Main plugin;

    public ChatEvent(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.getConfig().getBoolean("discordMcChat")) {
            TextChannel chatchannel = this.plugin.bot.getTextChannelById(this.plugin.getConfig().getString("discordMcChannel"));
            if (chatchannel != null) {
                chatchannel.sendMessage("**" + player.getDisplayName() + "**: " + event.getMessage()).queue();
            } else {
                this.plugin.getLogger().warning("Discord channel is null. Please provide a valid channel ID.");
            }
        }
        if (this.plugin.isHunter(player) && this.plugin.ingame && event.getMessage().startsWith("#")) {
            event.setCancelled(true);
            String original = this.plugin.getConfig().getString("hunterTeamchatMessage");
            String message = original.replaceAll("%player%", player.getDisplayName()).replaceAll("%message%", event.getMessage().substring(1));
            String normal = "&c&l[Hunter Chat] &c%player%: &6&l%message% ";
            String msg = normal.replaceAll("%player%", player.getDisplayName()).replaceAll("%message%", event.getMessage().substring(1));
            for (String names : this.plugin.hunters) {
                Player hunter = Bukkit.getPlayer(names);
                if (original.equals("%default%")) {
                    hunter.sendMessage(Methods.color(msg));
                    continue;
                }
                hunter.sendMessage(Methods.color(message));
            }
        } else if (this.plugin.isRunner(player) && this.plugin.ingame && event.getMessage().startsWith("#")) {
            event.setCancelled(true);
            String original = this.plugin.getConfig().getString("runnerTeamchat");
            String message = original.replaceAll("%player%", player.getDisplayName()).replaceAll("%message%", event.getMessage().substring(1));
            String normal = "&a&l[Runner Chat] &a%player%: &b&l%message%";
            String msg = normal.replaceAll("%player%", player.getDisplayName()).replaceAll("%message%", event.getMessage().substring(1));
            for (String names : this.plugin.speedrunners) {
                Player runner = Bukkit.getPlayer(names);
                if (original.equals("%default%")) {
                    runner.sendMessage(Methods.color(msg));
                    continue;
                }
                runner.sendMessage(Methods.color(message));
            }
        } else if (this.plugin.deadrunners.contains(player.getName()) && this.plugin.ingame) {
            event.setCancelled(true);
            for (String names : this.plugin.deadrunners) {
                Player dead = Bukkit.getPlayer(names);
                dead.sendMessage(Methods.color("&8[Death Chat] " + player.getDisplayName() + ":" + event.getMessage()));
            }
        }
    }
}
