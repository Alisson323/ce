package com.taiter.ce.commands;

import com.taiter.ce.Main;
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

public class RemoveSubCommand implements SubCommand {
    public RemoveSubCommand(com.taiter.ce.Main main) {
    }

    @Override
    public String execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return ChatColor.RED + "This command can only be used by players";
        }

        Player p = (Player) sender;
        String node = "ce.cmd.*";
        String requiredPermission = "ce.cmd.remove";
        if (!sender.hasPermission(node) && !sender.hasPermission(requiredPermission) && !sender.isOp()) {
            return ChatColor.RED + "You do not have permission to use this command.";
        }

        ItemStack item = p.getItemInHand();
        if (item == null || item.getType().equals(Material.AIR)) {
            return ChatColor.RED + "You are not holding an item!";
        }

        ItemMeta im = item.getItemMeta();
        if (!im.hasLore()) {
            return ChatColor.RED + "Your item does not have any enchantments!";
        }

        List<String> lore = im.getLore();
        if (args.length >= 2) {
            CEnchantment ce = EnchantManager.getEnchantment(args[1]);
            if (ce == null) {
                return ChatColor.RED + "The enchantment " + args[1] + " does not exist!";
            }
            for (String s : im.getLore()) {
                if (EnchantManager.containsEnchantment(s, ce)) {
                    lore.remove(s);
                    im.setLore(lore);
                    item.setItemMeta(im);
                    return ChatColor.GREEN + "Removed the enchantment " + ce.getDisplayName() + ChatColor.GREEN + "!";
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
        return ChatColor.GREEN + "Removed all custom enchantments.";
    }
}
