package com.trende2001.manhunt.utils;

import com.trende2001.manhunt.Main;
import org.bukkit.ChatColor;

public class Methods {
    private Main plugin;

    public Methods(Main plugin) {
        this.plugin = plugin;
    }

    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}