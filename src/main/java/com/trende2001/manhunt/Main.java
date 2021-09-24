package com.trende2001.manhunt;
// commands
import com.trende2001.manhunt.commands.HuntGame;
import com.trende2001.manhunt.commands.Hunter;
import com.trende2001.manhunt.commands.SpeedRunner;

// tabs
import com.trende2001.manhunt.tabs.HuntGameTab;
import com.trende2001.manhunt.tabs.HunterTab;
import com.trende2001.manhunt.tabs.RunnerTab;

// listeners
import com.trende2001.manhunt.listeners.ChatDisconnectEvent;
import com.trende2001.manhunt.listeners.CompassPortalEvent;
import com.trende2001.manhunt.listeners.DeathBlockBreakEvent;
import com.trende2001.manhunt.listeners.DropRespawnEvent;
import com.trende2001.manhunt.listeners.PlayerDamageEvent;

import com.trende2001.manhunt.listeners.discordlisteners.DiscordChat;
import com.trende2001.manhunt.listeners.discordlisteners.DiscordCommands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {
    public List<String> hunters = new ArrayList<>();

    public List<String> speedrunners = new ArrayList<>();

    public List<String> deadrunners = new ArrayList<>();

    public HashMap<String, Integer> huntersNumber = new HashMap<>();

    public boolean ingame = false;
    public boolean counting = false;
    public boolean waitingrunner = false;

    // discord integration
    public JDA bot;

    File config = new File(getDataFolder() + File.separator + "config.yml");

    @Override
    public void onEnable() {
        logs();
        checkConfig();
        registerCommands();
        registerEvents();
        discordBot();
    }

    private void registerCommands() {
        getCommand("hunter").setExecutor((CommandExecutor)new Hunter(this));
        getCommand("runner").setExecutor((CommandExecutor)new SpeedRunner(this));
        getCommand("huntgame").setExecutor((CommandExecutor)new HuntGame(this));
        getCommand("hunter").setTabCompleter(new HunterTab());
        getCommand("runner").setTabCompleter(new RunnerTab());
        getCommand("huntgame").setTabCompleter(new HuntGameTab());
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents((Listener)new CompassPortalEvent(this), (Plugin)this);
        pm.registerEvents((Listener)new DropRespawnEvent(this), (Plugin)this);
        pm.registerEvents((Listener)new PlayerDamageEvent(this), (Plugin)this);
        pm.registerEvents((Listener)new DeathBlockBreakEvent(this), (Plugin)this);
        pm.registerEvents((Listener)new ChatDisconnectEvent(this), (Plugin)this);
    }

    private void logs() {
        Logger log = getLogger();
        log.info("Author: trende2001");
        log.info("Version: " + getDescription().getVersion());
        log.info("Plugin Link: https://dev.bukkit.org/projects/trends-manhunt");

//        new UpdateChecker(this, 92298).getVersion(version -> {
//            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
//                log.info("You are running the latest version of Manhunt.");
//            } else {
//                Bukkit.broadcastMessage(ChatColor.GOLD + "There is a new update available for the plugin: Manhunt! Click on the link to download the new version: https://www.spigotmc.org/resources/trends-manhunt.92298/");
//            }
//        });
    }

    private void checkConfig() {
        if (!this.config.exists()) {
            getLogger().info("Config not found. Creating ...");
            saveDefaultConfig();
            getLogger().info("Config created!");
        }
    }

    // discord integration yeee

    private void discordBot() {
        if (!getConfig().getBoolean("discordIntegration"))
            return;
        if (getServer().getPluginManager().getPlugin("JDA") == null) {
            getLogger().info("Discord integration needs the plugin JDA");
            getLogger().info("https://www.spigotmc.org/resources/jda.80824/ to download. Download version 4.2.0_204");
            return;
        }
        try {
            this.bot = JDABuilder.createDefault(getConfig().getString("botToken")).setActivity(Activity.watching(" " + getConfig().getString("botActivityMessage"))).addEventListeners(new Object[] { new DiscordChat(this), new DiscordCommands(this) }).build().awaitReady();
        } catch (LoginException | InterruptedException e) {
            getLogger().severe("Please put in a valid token for the Discord bot to work");
            getLogger().severe("Paste the bot token in the selected part of the config");
            return;
        }
        getLogger().info("Discord Bot is now running successfully!");
    }


    public boolean isHunter(Player player) {
        return this.hunters.contains(player.getDisplayName());
    }

    public boolean isRunner(Player player) {
        return this.speedrunners.contains(player.getDisplayName());
    }
}
