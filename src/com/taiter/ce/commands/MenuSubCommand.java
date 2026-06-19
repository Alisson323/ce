package com.taiter.ce.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.taiter.ce.Main;
import com.taiter.ce.utils.Translator;

public class MenuSubCommand implements SubCommand {
    public MenuSubCommand(com.taiter.ce.Main main) {
    }

    @Override
    public String execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return Translator.get("Commands.OnlyPlayers", ChatColor.RED + "This command can only be executed by a Player.");
        }

        Player p = (Player) sender;
        String node = "ce.cmd.*";
        String requiredPermission = "ce.cmd.menu";
        if (!sender.hasPermission(node) && !sender.hasPermission(requiredPermission) && !sender.isOp()) {
            return Translator.get("Commands.NoPermission", ChatColor.RED + "You do not have permission to execute this command.");
        }

        p.openInventory(Main.CEMainMenu);
        return "";
    }
}
