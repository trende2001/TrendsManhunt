package com.trende2001.manhunt;

import com.trende2001.bstats.Metrics;
import com.trende2001.manhunt.commands.HuntGame;
import com.trende2001.manhunt.commands.Hunter;
import com.trende2001.manhunt.commands.Runner;
import com.trende2001.manhunt.listeners.*;
import com.trende2001.manhunt.listeners.discordlisteners.DiscordChat;
import com.trende2001.manhunt.listeners.discordlisteners.DiscordCommands;
import com.trende2001.manhunt.tabs.HuntGameTab;
import com.trende2001.manhunt.tabs.HunterTab;
import com.trende2001.manhunt.tabs.RunnerTab;
import com.trende2001.manhunt.utils.Methods;
import com.trende2001.manhunt.utils.Updater;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {
    public List<String> hunters = new ArrayList<>();
    public List<String> speedRunners = new ArrayList<>();
    public List<String> deadRunners = new ArrayList<>();

    public HashMap<String, Integer> huntersNumber = new HashMap<>();

    public boolean inGame = false;
    public boolean countingDown = false;
    public boolean waitingRunner = false;

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
        getCommand("hunter").setExecutor(new Hunter(this));
        getCommand("runner").setExecutor(new Runner(this));
        getCommand("huntgame").setExecutor(new HuntGame(this));
        getCommand("hunter").setTabCompleter(new HunterTab());
        getCommand("runner").setTabCompleter(new RunnerTab());
        getCommand("huntgame").setTabCompleter(new HuntGameTab());
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new CompassPortalEvent(this), this);
        pm.registerEvents(new DropRespawnEvent(this), this);
        pm.registerEvents(new PlayerDamageEvent(this), this);
        pm.registerEvents(new DeathBlockBreakEvent(this), this);
        pm.registerEvents(new MCChatEvent(this), this);
        pm.registerEvents(new PiglinTradeEvent(this), this);
    }

    private void registerBstats() {
        int pluginId = 13321; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);
    }

    Updater updater = new Updater(this, 480387, this.getFile(), Updater.UpdateType.NO_VERSION_CHECK, true);
    private void logs() {
        Logger log = getLogger();
        log.info("Author: trende2001");
        log.info("Version: " + getDescription().getVersion());
        log.info("Plugin Link: https://dev.bukkit.org/projects/trends-manhunt");
        if (Bukkit.getVersion().contains("1.16.1"))
            log.warning("The server version is 1.16.1, Nether Fortress tracking is not supported and you may have issues while tracking in the Nether.");
    }

    private void checkConfig() {
        if (!this.config.exists()) {
            getLogger().info("Config not found. Creating ...");
            saveDefaultConfig();
            getLogger().info("Config created!");
        }
    }

    // discord integration

    private void discordBot() {
        if (!getConfig().getBoolean("discordIntegration"))
            return;
        if (getServer().getPluginManager().getPlugin("JDA") == null) {
            getLogger().info("Discord integration needs the plugin JDA");
            getLogger().info("Download newest version:  https://www.spigotmc.org/resources/jda.80824/");
            return;
        }
        try {
            this.bot = JDABuilder.createDefault(getConfig().getString("botToken")).setActivity(Activity.watching(" " + getConfig().getString("botActivityMessage"))).addEventListeners(new Object[] { new DiscordChat(this), new DiscordCommands(this) }).build().awaitReady();
        } catch (Exception e) {
            getLogger().severe("Please put in a valid token for the Discord bot to work");
            getLogger().severe("Paste the bot token in the selected part of the config");
            return;
        }
        getLogger().info("Discord Bot is now running successfully!");
    }

    public ItemStack ruleBook() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.addPage(Methods.color("&a&lOfficial Manhunt Rules\n\n&a1. &bNo portal trapping. When the runner(s) comes through an end or nether portal, they must be able to move and get out from the portal regardless of the trap set by the hunter(s)"));
        bookMeta.addPage(Methods.color("&a2. &bNo nether portal traveling or breaking. Only the runner(s) is allowed to break their own portal, and the runner(s) is not allowed to create a new portal in the nether to travel far away in the overworld."));
        bookMeta.addPage(Methods.color("&a3. &bNo over powered items or glitches allowed. For example, towering with obsidian in the end to kill the dragon is not allowed. Instant harming potions are not allowed. Rules like this can vary depending on the players choices."));
        bookMeta.setTitle("Manhunt Rules");
        bookMeta.setAuthor("trende2001");
        book.setItemMeta(bookMeta);
        return book;
    }


    public boolean isHunter(Player player) {
        return this.hunters.contains(player.getDisplayName());
    }

    public boolean isRunner(Player player) {
        return this.speedRunners.contains(player.getDisplayName());
    }
}
