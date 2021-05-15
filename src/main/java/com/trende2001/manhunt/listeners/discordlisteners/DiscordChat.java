package com.trende2001.manhunt.listeners.discordlisteners;

import com.trende2001.manhunt.Main;
import com.trende2001.manhunt.utils.Methods;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

public class DiscordChat extends ListenerAdapter {
    private Main plugin;

    public DiscordChat(Main plugin) {
        this.plugin = plugin;
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (this.plugin.getConfig().getBoolean("discordMcChat") &&
                !event.getAuthor().isBot() &&
                event.getChannel().getId().equals(this.plugin.getConfig().getString("discordMcChannel"))) {
            if (this.plugin.getServer().getOnlinePlayers().size() == 0) {
                this.plugin.bot.getTextChannelById(this.plugin.getConfig().getString("discordMcChannel")).sendMessage("No one is on the server to talk").queue();
                return;
            }
            Bukkit.broadcastMessage(Methods.color("&b&l" + event.getAuthor().getName() + "&f: &a" + event.getMessage().getContentRaw()));
        }
    }
}
