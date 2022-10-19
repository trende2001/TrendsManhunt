package com.trende2001.manhunt.utils;

import com.trende2001.manhunt.Main;
import org.bukkit.ChatColor;

public class Methods {
    private Main plugin;

    public Methods(Main plugin) {
        this.plugin = plugin;
    }


    /**
     * @param msg use this every time to reduce the pain of typing the actual method
     * @return a translated color code
     */
    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}