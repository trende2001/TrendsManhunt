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

public class Runner implements CommandExecutor {
    private Main plugin;

    public Runner(Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("runner")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("[Manhunt] You must be a player to run this command!");
                return true;
            }
            Player player = (Player)sender;
            if (player.hasPermission("manhunt.speedrunner")) {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.GREEN + "Please use /runner help to see list of commands");
                    return true;
                }
                if (args.length == 1) {
                    switch (args[0].toLowerCase()) {
                        case "list":
                            if (this.plugin.speedRunners.size() == 0) {
                                player.sendMessage(ChatColor.GREEN + "No speedrunners have been set");
                                return true;
                            }
                            player.sendMessage(ChatColor.GREEN + "Here are the runners:");
                            for (String name : this.plugin.speedRunners) {
                                Player runnerName = Bukkit.getPlayer(name);
                                player.sendMessage(Methods.color("&7- &a" + runnerName.getDisplayName()));
                            }
                            return true;
                        case "help":
                            player.sendMessage(ChatColor.YELLOW + "/runner add <player>: " + ChatColor.GREEN + "Add a speedrunner");
                            player.sendMessage(ChatColor.YELLOW + "/runner remove <player>: " + ChatColor.GREEN + "Remove a speedrunner");
                            player.sendMessage(ChatColor.YELLOW + "/runner list: " + ChatColor.GREEN + "Lists all the speedrunners");
                            player.sendMessage(ChatColor.YELLOW + "/runner clear: " + ChatColor.GREEN + "Removes all speedrunners");
                            player.sendMessage(ChatColor.YELLOW + "/runner help: " + ChatColor.GREEN + "Displays this page");
                            return true;
                        case "clear":
                            if (this.plugin.speedRunners.size() == 0) {
                                player.sendMessage(ChatColor.RED + "There are no speedrunners to clear!");
                                return true;
                            }
                            player.sendMessage(ChatColor.GREEN + "All speedrunners have been cleared");
                            this.plugin.speedRunners.clear();
                            return true;
                    }
                    player.sendMessage(ChatColor.RED + "Please use /runner help for commands");
                    return true;
                }
                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("add")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) {
                            player.sendMessage(ChatColor.RED + "Could not find player!");
                            return true;
                        }
                        if (!this.plugin.speedRunners.contains(target.getName())) {
                            if (target == player) {
                                player.sendMessage(ChatColor.GREEN + "You have been added as a speedrunner");
                                this.plugin.speedRunners.add(player.getName());
                                this.plugin.hunters.remove(target.getName());
                                player.getInventory().removeItem(new ItemStack(Material.COMPASS));
                                return true;
                            }
                            target.sendMessage(ChatColor.GREEN + "You have been added as a speedrunner");
                            player.sendMessage(ChatColor.GREEN + target.getDisplayName() + " is now a speedrunner");
                            this.plugin.speedRunners.add(target.getName());
                            this.plugin.hunters.remove(target.getName());
                            player.getInventory().removeItem(new ItemStack(Material.COMPASS));
                            return true;
                        }
                        if (target == player) {
                            player.sendMessage(ChatColor.RED + "You are already a speedrunner!");
                            return true;
                        }
                        player.sendMessage(ChatColor.RED + target.getDisplayName() + " is already a speedrunner!");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("remove")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) {
                            player.sendMessage(ChatColor.RED + "Could not find player!");
                            return true;
                        }
                        if (this.plugin.isRunner(target)) {
                            if (target == player) {
                                player.sendMessage(ChatColor.GREEN + "You are no longer a speedrunner");
                                this.plugin.speedRunners.remove(player.getName());
                                return true;
                            }
                            target.sendMessage(ChatColor.GREEN + "You are no longer a speedrunner");
                            player.sendMessage(ChatColor.GREEN + target.getDisplayName() + " is no longer a speedrunner");
                            this.plugin.speedRunners.remove(target.getName());
                            return true;
                        }
                        if (target == player) {
                            player.sendMessage(ChatColor.RED + "You are not a speedrunner!");
                            return true;
                        }
                        player.sendMessage(ChatColor.RED + target.getDisplayName() + " is not a speedrunner!");
                        return true;
                    }
                }
                player.sendMessage(ChatColor.RED + "Please use /runner help to see commands");
                return true;
            }
        }
        return false;
    }
}