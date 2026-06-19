package com.taiter.ce.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import com.taiter.ce.Main;
import com.taiter.ce.utils.Translator;

public class UpdateSubCommand implements SubCommand {
    private final Main main;
    private boolean confirmUpdate = false;

    public UpdateSubCommand(Main main) {
        this.main = main;
    }

    @SuppressWarnings("deprecation")
    @Override
    public String execute(final CommandSender sender, String[] args) {
        String usageError = Translator.get("Commands.UpdateUsage", ChatColor.RED + "Correct Usage: /ce update <check/applyupdate>");
        if (!sender.equals(Bukkit.getConsoleSender())) {
            return Translator.get("Commands.OnlyConsole", ChatColor.RED + "This command can only be run via Console");
        }

        if (args.length >= 2) {
            String toDo = args[1].toLowerCase();

            if (toDo.startsWith("c")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        main.updateCheck();
                    }
                }.runTaskLater(Main.plugin, 1L);
                return "";
            } else if (toDo.equals("applyupdate")) {
                if (!main.hasChecked) {
                    return Translator.get("Commands.UpdateCheckFirst", ChatColor.RED + "You need to check for an update first using '/ce update check'.");
                }
                if (main.hasUpdate) {
                    if (!confirmUpdate) {
                        confirmUpdate = true;
                        sender.sendMessage(Translator.get("Commands.UpdateConfirm", ChatColor.AQUA + "Rerun the command to confirm the update (This expires in 5 Minutes)."));
                        Main.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(Main.plugin, new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (confirmUpdate) {
                                    confirmUpdate = false;
                                }
                            }
                        }, 6000L);
                        return "";
                    } else {
                        Main.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(Main.plugin, new BukkitRunnable() {
                            @Override
                            public void run() {
                                main.update();
                            }
                        }, 1L);
                        return "";
                    }
                } else {
                    return Translator.get("Commands.UpdateAlreadyLatest", ChatColor.RED + "You are already using the latest version of CE.");
                }
            } else {
                return usageError;
            }
        } else {
            return usageError;
        }
    }
}
