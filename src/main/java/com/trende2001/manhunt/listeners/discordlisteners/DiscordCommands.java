package com.trende2001.manhunt.listeners.discordlisteners;


import com.trende2001.manhunt.Main;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DiscordCommands extends ListenerAdapter {
    private Main plugin;

    public DiscordCommands(Main plugin) {
        this.plugin = plugin;
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            TextChannel channel = event.getChannel();
            if (event.getMessage().getContentRaw().equals("!huntstatus"))
                if (!this.plugin.ingame) {
                    channel.sendMessage("No hunt is active").queue();
                } else {
                    channel.sendMessage("A hunt is active. List of players:").queue();
                    for (String name : this.plugin.hunters) {
                        Player hunter = Bukkit.getPlayer(name);
                        channel.sendMessage("**" + hunter.getDisplayName() + "** is a **hunter!**").queue();
                    }
                    for (String names : this.plugin.speedrunners) {
                        Player runner = Bukkit.getPlayer(names);
                        channel.sendMessage("**" + runner.getDisplayName() + "** is the **runner!**").queue();
                    }
                }
            if (event.getMessage().getContentRaw().equals("!online")) {
                if (this.plugin.getServer().getOnlinePlayers().size() == 0) {
                    channel.sendMessage("Currently no players online").queue();
                    return;
                }
                channel.sendMessage("List of all players online:").queue();
                for (Player online : Bukkit.getOnlinePlayers())
                    channel.sendMessage("**" + online.getDisplayName() + "** is online").queue();
            }
        }
    }
}

