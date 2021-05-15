package com.trende2001.manhunt.commands;

import com.trende2001.manhunt.Main;
import com.trende2001.manhunt.utils.Methods;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HuntGame implements CommandExecutor {
    private Main plugin;
    private Player spedrun;

    public HuntGame(Main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
        if (label.equalsIgnoreCase("huntgame")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("[Manhunt] You must be a player to run this command!");
                return true;
            }
            final Player player = (Player)sender;
            if (player.hasPermission("manhunt.huntgame")) {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.GREEN + "Please use /huntgame help to see list of commands");
                    return true;
                }
                if (args.length == 1) {
                    switch (args[0].toLowerCase()) {
                        case "help":
                            player.sendMessage(ChatColor.GREEN + "                Manhunt Huntgame Help                ");
                            player.sendMessage(" ");
                            player.sendMessage(ChatColor.GRAY + "-----------------------------------------------------");
                            player.sendMessage(" ");
                            player.sendMessage(ChatColor.YELLOW + "/huntgame start <seconds>:");
                            player.sendMessage(ChatColor.GREEN + " Starts a hunt with <seconds> headstart");
                            player.sendMessage(ChatColor.YELLOW + "/huntgame stop: " + ChatColor.GREEN + "Stops the hunt");
                            player.sendMessage(ChatColor.YELLOW + "/huntgame info: " + ChatColor.GREEN + "Gives info on the plugin");
                            player.sendMessage(ChatColor.YELLOW + "/huntgame reload: " + ChatColor.GREEN + "Reloads the config");
                            player.sendMessage(ChatColor.YELLOW + "/huntgame help: " + ChatColor.GREEN + "Displays this page");
                            player.sendMessage(" ");
                            player.sendMessage(ChatColor.GRAY + "-----------------------------------------------------");
                            return true;
                        case "stop":
                            if (this.plugin.ingame || this.plugin.counting) {
                                Bukkit.broadcastMessage(ChatColor.GREEN + "The hunt has ended. Use /huntgame start <headstarttime> to start again.");
                                this.plugin.ingame = false;
                                this.plugin.counting = false;
                                return true;
                            }
                            player.sendMessage(ChatColor.RED + "No hunt is active!");
                            useConfig(player);
                            return true;
                        case "info":
                            player.sendMessage(ChatColor.GREEN + "Manhunt by trende2001");
                            player.sendMessage(ChatColor.GREEN + "Manhunt is running version " + this.plugin.getDescription().getVersion());
                            player.sendMessage(ChatColor.GREEN + "Plugin Link: https://dev.bukkit.org/projects/trends-manhunt");
                            return true;
                        case "reload":
                            this.plugin.reloadConfig();
                            player.sendMessage(ChatColor.GREEN + "Manhunt has been reloaded!");
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 2.0F);
                            return true;
                    }
                    player.sendMessage(ChatColor.RED + "Use /huntgame help for commands");
                    return true;
                }
                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("start")) {
                        if (checkPlayer(player)) {
                            if (args[1].equalsIgnoreCase("0")) {
                                Bukkit.broadcastMessage(ChatColor.GOLD + "Awaiting runner to hit hunter and start the hunt");
                                useConfig(player);
                                this.plugin.waitingrunner = true;
                                return true;
                            }
                            try {
                                Integer.parseInt(args[1]);
                            } catch (NumberFormatException ex) {
                                player.sendMessage(ChatColor.RED + "Please put an integer to set the time of the headstart or put 0 for no headstart");
                                return true;
                            }
                            Bukkit.broadcastMessage(ChatColor.GOLD + "The hunters will be released in " + args[1] + " seconds!");
                            useConfig(player);
                            this.plugin.counting = true;
                            for (String name : this.plugin.hunters) {
                                Player hunter = Bukkit.getPlayer(name);
                                hunter.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2147483647, 3, true, false));
                            }
                            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, new Runnable() {
                                int countdown = Integer.parseInt(args[1]);

                                public void run() {
                                    if (this.countdown == 0) {
                                        Bukkit.broadcastMessage(Methods.color("&l&cHunters have been released!"));
                                        String name = null;
                                        for (String lame : HuntGame.this.plugin.speedrunners)
                                            name = lame;
                                        Player runner = Bukkit.getPlayer(name);
                                        runner.sendTitle(ChatColor.RED + "Hunters released!", ChatColor.GOLD + "Run away, they can track your distance!", 5, 25, 5);
                                        try {
                                            runner.getLocation().getWorld().playSound(player.getLocation(), Sound.valueOf(HuntGame.this.plugin.getConfig().getString("startSound")), 1.0F, 1.0F);
                                        } catch (Exception e) {
                                            Bukkit.broadcastMessage(ChatColor.RED + "An error occured with the start sound. Playing default sound. Check console for more info");
                                            HuntGame.this.plugin.getLogger().severe("Start sound is invalid. Change configuration to a valid start sound");
                                            runner.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0F, 1.0F);
                                        }
                                        HuntGame.this.plugin.counting = false;
                                        for (String namez : HuntGame.this.plugin.hunters) {
                                            Player hunter = Bukkit.getPlayer(namez);
                                            for (PotionEffect e : hunter.getActivePotionEffects())
                                                hunter.removePotionEffect(e.getType());
                                        }
                                        HuntGame.this.plugin.ingame = true;
                                        Bukkit.getScheduler().cancelTasks((Plugin)HuntGame.this.plugin);
                                    } else if (this.countdown == 1) {
                                        Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "Hunters will be released in 1 second");
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 2.0F);
                                    } else if (this.countdown == 2) {
                                        Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "Hunters will be released in 2 seconds");
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 2.0F);
                                    } else if (this.countdown == 3) {
                                        Bukkit.broadcastMessage(ChatColor.GOLD + "Hunters will be released in 3 seconds");
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 2.0F);
                                    }
                                    this.countdown--;
                                }
                            },0L, 20L);
                            return true;
                        }
                        return false;
                    }
                    player.sendMessage(ChatColor.RED + "Use /huntgame help for commands");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkPlayer(Player player) {
        if (this.plugin.speedrunners.size() == 0 && this.plugin.hunters.size() == 0) {
            player.sendMessage(ChatColor.GREEN + "Please select a speedrunner and a hunter before starting");
            return false;
        }
        if (this.plugin.speedrunners.size() == 0) {
            player.sendMessage(ChatColor.GREEN + "Please select a speedrunner before starting");
            return false;
        }
        if (this.plugin.hunters.size() == 0) {
            player.sendMessage(ChatColor.GREEN + "Please select a hunter before starting");
            return false;
        }
        if (this.plugin.speedrunners.size() >= 2) {
            player.sendMessage(ChatColor.YELLOW + "Only 1 speedrunner is supported. Multiple will come in later versions");
            return false;
        }
        for (String name : this.plugin.speedrunners) {
            Player spedoo = Bukkit.getPlayer(name);
            if (this.plugin.isHunter(spedoo)) {
                player.sendMessage(ChatColor.RED + "Failed to start. The runner is assigned both speedrunner and hunter. The runner must remove one role");
                return false;
            }
        }
        return true;
    }

    private void useConfig(Player player) {
        if (this.plugin.getConfig().getBoolean("healStart")) {
            for (String names : this.plugin.hunters) {
                Player hunt = Bukkit.getPlayer(names);
                hunt.setHealth(20.0D);
            }
            for (String lames : this.plugin.speedrunners) {
                Player rn = Bukkit.getPlayer(lames);
                rn.setHealth(20.0D);
            }
        }
        if (this.plugin.getConfig().getBoolean("feedStart")) {
            for (String names : this.plugin.hunters) {
                Player hunt = Bukkit.getPlayer(names);
                hunt.setFoodLevel(20);
            }
            for (String lames : this.plugin.speedrunners) {
                Player rn = Bukkit.getPlayer(lames);
                rn.setFoodLevel(20);
            }
        }
        if (this.plugin.getConfig().getBoolean("dayStart")) {
            World world = player.getWorld();
            world.setTime(1000L);
        }
        if (this.plugin.getConfig().getBoolean("clearStart")) {
            for (String names : this.plugin.hunters) {
                Player hunt = Bukkit.getPlayer(names);
                hunt.getInventory().clear();
                hunt.getInventory().addItem(new ItemStack[] { new ItemStack(Material.COMPASS) });
            }
            for (String lames : this.plugin.speedrunners) {
                Player rn = Bukkit.getPlayer(lames);
                rn.getInventory().clear();
            }
        }
        if (this.plugin.getConfig().getBoolean("achievementReset")) {
            for (String names : this.plugin.hunters) {
                Player huns = Bukkit.getPlayer(names);
                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "advancement revoke " + huns.getDisplayName() + " everything");
            }
            for (String names : this.plugin.speedrunners) {
                Player runn = Bukkit.getPlayer(names);
                World world = runn.getWorld();
                if (((Boolean)world.getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)).booleanValue())
                    world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, Boolean.valueOf(false));
                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "advancement revoke " + runn.getDisplayName() + " everything");
            }
        }
    }
}
