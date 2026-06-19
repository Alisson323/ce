package com.taiter.ce.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.taiter.ce.Main;
import com.taiter.ce.utils.Tools;
import com.taiter.ce.utils.Translator;
import com.taiter.ce.CItems.CItem;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.EnchantManager;

public class ListSubCommand implements SubCommand {
    public ListSubCommand(com.taiter.ce.Main main) {
    }

    @Override
    public String execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return Translator.get("Commands.OnlyPlayers", ChatColor.RED + "This command can only be executed by a Player.");
        }

        Player p = (Player) sender;
        String node = "ce.cmd.*";
        String requiredPermission = "ce.cmd.list";
        if (!sender.hasPermission(node) && !sender.hasPermission(requiredPermission) && !sender.isOp()) {
            return Translator.get("Commands.NoPermission", ChatColor.RED + "You do not have permission to execute this command.");
        }

        String usageError = Translator.get("Commands.ListUsage", ChatColor.RED + "Correct Usage: /ce list <Items/Enchantments>");
        if (args.length >= 2) {
            String toList = args[1].toLowerCase();
            if (toList.startsWith("i")) {
                p.sendMessage(Translator.get("Commands.ItemListHeader", ChatColor.GOLD + "-------------Item List-------------"));
                for (CItem ci : Main.items) {
                    if (p.isOp() || Tools.checkPermission(ci, p)) {
                        p.sendMessage("   " + ci.getDisplayName());
                    }
                }
                p.sendMessage(Translator.get("Commands.ItemListFooter", ChatColor.GOLD + "-----------------------------------"));
                return "";
            } else if (toList.startsWith("e")) {
                p.sendMessage(Translator.get("Commands.EnchantListHeader", ChatColor.GOLD + "----------Enchantment List-----------"));
                for (CEnchantment ce : EnchantManager.getEnchantments()) {
                    if (p.isOp() || Tools.checkPermission(ce, p)) {
                        p.sendMessage("   " + ce.getDisplayName());
                    }
                }
                p.sendMessage(Translator.get("Commands.EnchantListFooter", ChatColor.GOLD + "------------------------------------"));
                return "";
            } else {
                return usageError;
            }
        } else {
            return usageError;
        }
    }
}
