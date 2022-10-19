package com.trende2001.manhunt.commands;

import com.trende2001.manhunt.Main;
import com.trende2001.manhunt.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hunter implements CommandExecutor {
    private Main plugin;

    public Hunter(Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (label.equalsIgnoreCase("hunter") &&
                    player.hasPermission("manhunt.hunter")) {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.GREEN + "Please use /hunter help to see list of commands");
                    return true;
                }
                if (args.length == 1) {
                    switch (args[0].toLowerCase()) {
                        case "help":
                            player.sendMessage(ChatColor.YELLOW + "/hunter add <player>: " + ChatColor.GREEN + "Add a hunter");
                            player.sendMessage(ChatColor.YELLOW + "/hunter remove <player>: " + ChatColor.GREEN + "Remove a hunter");
                            player.sendMessage(ChatColor.YELLOW + "/hunter list: " + ChatColor.GREEN + "Lists all the hunters");
                            player.sendMessage(ChatColor.YELLOW + "/hunter clear: " + ChatColor.GREEN + "Removes all hunters");
                            player.sendMessage(ChatColor.YELLOW + "/hunter help: " + ChatColor.GREEN + "Displays this page");
                            return true;
                        case "list":
                            if (this.plugin.hunters.size() == 0) {
                                player.sendMessage(ChatColor.GREEN + "No hunters have been set");
                                return true;
                            }
                            player.sendMessage(ChatColor.GREEN + "Here are the hunters:");
                            for (String name : this.plugin.hunters) {
                                Player hunterName = Bukkit.getPlayer(name);
                                player.sendMessage(Methods.color("&7- &a" + hunterName.getDisplayName()));
                            }
                            return true;
                        case "clear":
                            if (this.plugin.hunters.size() == 0) {
                                player.sendMessage(ChatColor.RED + "There are no hunters to clear!");
                                return true;
                            }
                            player.sendMessage(ChatColor.GREEN + "All hunters have been cleared");
                            for (String name : this.plugin.hunters) {
                                Player hunter = Bukkit.getPlayer(name);
                                hunter.getInventory().removeItem(new ItemStack(Material.COMPASS));
                            }
                            this.plugin.hunters.clear();
                            return true;
                    }
                    player.sendMessage(ChatColor.RED + "Please use /hunter help for commands");
                    return true;
                }
                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("add")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) {
                            player.sendMessage(ChatColor.RED + "Could not find player!");
                            return true;
                        }
                        if (!this.plugin.isHunter(target)) {
                            if (target == player) {
                                player.sendMessage(ChatColor.GREEN + "You have been added as a hunter");
                                player.getInventory().addItem(new ItemStack(Material.COMPASS));
                                this.plugin.hunters.add(player.getName());
                                this.plugin.huntersNumber.put(player.getName(), 0);
                                return true;
                            }
                            target.sendMessage(ChatColor.GREEN + "You have been added as a hunter");
                            target.getInventory().addItem(new ItemStack(Material.COMPASS));
                            player.sendMessage(ChatColor.GREEN + target.getDisplayName() + " has been added as a hunter");
                            this.plugin.hunters.add(target.getName());
                            this.plugin.huntersNumber.put(target.getName(), 0);
                            return true;
                        }
                        if (target == player) {
                            player.sendMessage(ChatColor.RED + "You are already a hunter!");
                            return true;
                        }
                        player.sendMessage(ChatColor.RED + target.getDisplayName() + " is already a hunter!");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("remove")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) {
                            player.sendMessage(ChatColor.RED + "Could not find player!");
                            return true;
                        }
                        if (this.plugin.isHunter(target)) {
                            if (target == player) {
                                player.sendMessage(ChatColor.GREEN + "You are no longer a hunter");
                                player.getInventory().removeItem(new ItemStack(Material.COMPASS));
                                this.plugin.hunters.remove(player.getName());
                                return true;
                            }
                            target.sendMessage(ChatColor.GREEN + "You are no longer a hunter");
                            target.getInventory().removeItem(new ItemStack(Material.COMPASS));
                            player.sendMessage(ChatColor.GREEN + target.getDisplayName() + " is no longer a hunter");
                            this.plugin.hunters.remove(target.getName());
                            return true;
                        }
                        if (target == player) {
                            player.sendMessage(ChatColor.RED + "You are not a hunter!");
                            return true;
                        }
                        player.sendMessage(ChatColor.RED + target.getDisplayName() + " is not a hunter!");
                        return true;
                    }
                }
                player.sendMessage(ChatColor.RED + "Please use /hunter help to see commands");
                return true;
            }
        }
        sender.sendMessage("[Manhunt] You must be a player to run this command!");
        return false;
    }
}