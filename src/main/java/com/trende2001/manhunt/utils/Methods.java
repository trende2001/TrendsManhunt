package com.trende2001.manhunt.utils;

import com.trende2001.manhunt.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Random;

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

    public static Location generateLoc(Player plr) {
        Random rand = new Random();

        int x = rand.nextInt(15000);
        int y = 0; // this will not be in the config for reasons, i'm sure you don't want someone to suffocate in stone lol
        int z = rand.nextInt(15000);
        //plugin.getConfig().getInt("boundZcoordinate")

        Location randloc = new Location(plr.getWorld(), x, y, z);

        y = randloc.getWorld().getHighestBlockYAt(randloc);
        randloc.setY(y);

        return randloc;
    }
}