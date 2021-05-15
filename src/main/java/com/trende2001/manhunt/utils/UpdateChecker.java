package com.trende2001.manhunt.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {
    private JavaPlugin plugin;
    private int resourceid;

    public UpdateChecker(JavaPlugin plugin, int resourceid) {
        this.plugin = plugin;
        this.resourceid = resourceid;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceid).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                this.plugin.getLogger().info("Couldn't look for updates: " + exception.getMessage());
            }
        });
    }
}
