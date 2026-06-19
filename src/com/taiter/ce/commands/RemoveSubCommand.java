package com.taiter.ce.commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.EnchantManager;
import com.taiter.ce.utils.Translator;

public class RemoveSubCommand implements SubCommand {
    public RemoveSubCommand(com.taiter.ce.Main main) {
    }

    @Override
    public String execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return Translator.get("Commands.OnlyPlayers", ChatColor.RED + "This command can only be executed by a Player.");
        }

        Player p = (Player) sender;
        String node = "ce.cmd.*";
        String requiredPermission = "ce.cmd.remove";
        if (!sender.hasPermission(node) && !sender.hasPermission(requiredPermission) && !sender.isOp()) {
            return Translator.get("Commands.NoPermission", ChatColor.RED + "You do not have permission to execute this command.");
        }

        ItemStack item = p.getItemInHand();
        if (item == null || item.getType().equals(Material.AIR)) {
            return Translator.get("Commands.NoItemHand", ChatColor.RED + "You do not have an item in your hand.");
        }

        ItemMeta im = item.getItemMeta();
        if (!im.hasLore()) {
            return Translator.get("Commands.NoEnchantsOnItem", ChatColor.RED + "Your item does not have any enchantments!");
        }

        List<String> lore = im.getLore();
        if (args.length >= 2) {
            CEnchantment ce = EnchantManager.getEnchantment(args[1]);
            if (ce == null) {
                return Translator.get("Commands.EnchantNotFound", ChatColor.RED + "The specified Enchantment could not be found.");
            }
            for (String s : im.getLore()) {
                if (EnchantManager.containsEnchantment(s, ce)) {
                    lore.remove(s);
                    im.setLore(lore);
                    item.setItemMeta(im);
                    return Translator.get("Commands.EnchantRemoved", ChatColor.GREEN + "Removed the enchantment %enchant%!").replace("%enchant%", ce.getDisplayName());
                }
            }
        } else {
            List<String> toRemove = new ArrayList<>();
            for (String s : im.getLore()) {
                if (EnchantManager.containsEnchantment(s)) {
                    toRemove.add(s);
                }
            }
            lore.removeAll(toRemove);
        }
        im.setLore(lore);
        item.setItemMeta(im);
        return Translator.get("Commands.AllEnchantsRemoved", ChatColor.GREEN + "Removed all custom enchantments.");
    }
}
