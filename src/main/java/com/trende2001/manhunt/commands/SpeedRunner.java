package com.trende2001.manhunt.commands;

import com.trende2001.manhunt.Main;
import com.trende2001.manhunt.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedRunner implements CommandExecutor {
    private Main plugin;

    public SpeedRunner(Main plugin) {
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
                            if (this.plugin.speedrunners.size() == 0) {
                                player.sendMessage(ChatColor.GREEN + "No speedrunners have been set");
                                return true;
                            }
                            player.sendMessage(ChatColor.GREEN + "Here is the speedrunner:");
                            for (String name : this.plugin.speedrunners)
                                player.sendMessage(Methods.color("&7- &a" + name));
                            return true;
                        case "help":
                            player.sendMessage(ChatColor.GREEN + "                 Manhunt Runner Help                ");
                            player.sendMessage(" ");
                            player.sendMessage(ChatColor.GRAY + "-----------------------------------------------------");
                            player.sendMessage(" ");
                            player.sendMessage(ChatColor.YELLOW + "/runner add <player>: " + ChatColor.GREEN + "Add a speedrunner");
                            player.sendMessage(ChatColor.YELLOW + "/runner remove <player>: " + ChatColor.GREEN + "Remove a speedrunner");
                            player.sendMessage(ChatColor.YELLOW + "/runner list: " + ChatColor.GREEN + "Lists all the speedrunners");
                            player.sendMessage(ChatColor.YELLOW + "/runner clear: " + ChatColor.GREEN + "Removes all speedrunners");
                            player.sendMessage(ChatColor.YELLOW + "/runner help: " + ChatColor.GREEN + "Displays this page");
                            player.sendMessage(" ");
                            player.sendMessage(ChatColor.GRAY + "-----------------------------------------------------");
                            return true;
                        case "clear":
                            if (this.plugin.speedrunners.size() == 0) {
                                player.sendMessage(ChatColor.RED + "There are no speedrunners to clear!");
                                return true;
                            }
                            player.sendMessage(ChatColor.GREEN + "All speedrunners have been cleared");
                            this.plugin.speedrunners.clear();
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
                        if (!this.plugin.speedrunners.contains(target.getDisplayName())) {
                            if (target == player) {
                                player.sendMessage(ChatColor.GREEN + "You have been added as a speedrunner");
                                this.plugin.speedrunners.add(player.getDisplayName());
                                return true;
                            }
                            target.sendMessage(ChatColor.GREEN + "You have been added as a speedrunner");
                            player.sendMessage(ChatColor.GREEN + target.getDisplayName() + " is now a speedrunner");
                            this.plugin.speedrunners.add(target.getDisplayName());
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
                                this.plugin.speedrunners.remove(player.getDisplayName());
                                return true;
                            }
                            target.sendMessage(ChatColor.RED + "You are no longer a speedrunner");
                            player.sendMessage(ChatColor.GREEN + target.getDisplayName() + " is no longer a speedrunner");
                            this.plugin.speedrunners.remove(target.getDisplayName());
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
