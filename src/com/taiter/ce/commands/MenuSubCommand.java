package com.taiter.ce.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.taiter.ce.Main;

public class MenuSubCommand implements SubCommand {
    public MenuSubCommand(com.taiter.ce.Main main) {
    }

    @Override
    public String execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return ChatColor.RED + "This command can only be used by players";
        }

        Player p = (Player) sender;
        String node = "ce.cmd.*";
        String requiredPermission = "ce.cmd.menu";
        if (!sender.hasPermission(node) && !sender.hasPermission(requiredPermission) && !sender.isOp()) {
            return ChatColor.RED + "You do not have permission to use this command.";
        }

        p.openInventory(Main.CEMainMenu);
        return "";
    }
}
