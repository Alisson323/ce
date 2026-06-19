package com.taiter.ce.commands;

import org.bukkit.command.CommandSender;

public interface SubCommand {
    String execute(CommandSender sender, String[] args);
}
