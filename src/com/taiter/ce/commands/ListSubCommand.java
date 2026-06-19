package com.taiter.ce.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.taiter.ce.Main;
import com.taiter.ce.utils.Tools;
import com.taiter.ce.CItems.CItem;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.EnchantManager;

public class ListSubCommand implements SubCommand {
    public ListSubCommand(com.taiter.ce.Main main) {
    }

    @Override
    public String execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return ChatColor.RED + "This command can only be used by players";
        }

        Player p = (Player) sender;
        String node = "ce.cmd.*";
        String requiredPermission = "ce.cmd.list";
        if (!sender.hasPermission(node) && !sender.hasPermission(requiredPermission) && !sender.isOp()) {
            return ChatColor.RED + "You do not have permission to use this command.";
        }

        String usageError = ChatColor.RED + "Correct Usage: /ce list <Items/Enchantments>";
        if (args.length >= 2) {
            String toList = args[1].toLowerCase();
            if (toList.startsWith("i")) {
                p.sendMessage(ChatColor.GOLD + "-------------Item List-------------");
                for (CItem ci : Main.items) {
                    if (p.isOp() || Tools.checkPermission(ci, p)) {
                        p.sendMessage("   " + ci.getDisplayName());
                    }
                }
                p.sendMessage(ChatColor.GOLD + "-----------------------------------");
                return "";
            } else if (toList.startsWith("e")) {
                p.sendMessage(ChatColor.GOLD + "----------Enchantment List-----------");
                for (CEnchantment ce : EnchantManager.getEnchantments()) {
                    if (p.isOp() || Tools.checkPermission(ce, p)) {
                        p.sendMessage("   " + ce.getDisplayName());
                    }
                }
                p.sendMessage(ChatColor.GOLD + "------------------------------------");
                return "";
            } else {
                return usageError;
            }
        } else {
            return usageError;
        }
    }
}
