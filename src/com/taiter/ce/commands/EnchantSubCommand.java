package com.taiter.ce.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.taiter.ce.Main;
import com.taiter.ce.utils.Tools;
import com.taiter.ce.CBasic;
import com.taiter.ce.CItems.CItem;
import com.taiter.ce.CItems.utilities.Swimsuit;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.EnchantManager;

public class EnchantSubCommand implements SubCommand {
    public EnchantSubCommand(com.taiter.ce.Main main) {
    }

    @SuppressWarnings("deprecation")
    @Override
    public String execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return ChatColor.RED + "This command can only be used by players";
        }

        Player p = (Player) sender;
        String node = "ce.cmd.*";
        String requiredPermission = "ce.cmd.enchant";
        if (!sender.hasPermission(node) && !sender.hasPermission(requiredPermission) && !sender.isOp()) {
            return ChatColor.RED + "You do not have permission to use this command.";
        }

        String isEnchantCmd = args[0].toLowerCase();
        String usageError = ChatColor.RED + "Correct Usage: /ce " + (isEnchantCmd.startsWith("e") ? "enchant [Required Material] <Enchantment> <Level>" : "item <Item>");
        if (args.length < 2) {
            return usageError;
        }

        ItemStack item = p.getItemInHand();
        String customName = args[1];
        Material test = null;
        int start = 2;

        if (Material.getMaterial(customName) != null) {
            test = Material.getMaterial(customName);
        } else {
            try {
                int material = Integer.parseInt(customName);
                if (Material.getMaterial(material) != null) {
                    test = Material.getMaterial(material);
                }
            } catch (NumberFormatException ex) {
            }
        }

        if (test != null) {
            if (p.getItemInHand().getType() != test) {
                return ChatColor.RED + "You do not have the right material to enchant this!";
            }
            start++;
            if (args.length > 2) {
                customName = args[2];
            }
        }

        int level = 1;
        if (isEnchantCmd.startsWith("e")) {
            if (item.getType().equals(Material.AIR)) {
                return ChatColor.RED + "You are not holding an item in your hand";
            }
            try {
                level = Integer.parseInt(args[args.length - 1]);
            } catch (Exception e) {
            }
        } else {
            level = 0;
        }

        if (level < 0) {
            level *= -1;
        }
        if (level > 10) {
            level = 0;
        }

        if (args.length > start) {
            for (int i = start; i < (level == 0 ? args.length : args.length - 1); i++) {
                customName += " " + args[i];
            }
        }

        CBasic custom = null;
        if (isEnchantCmd.startsWith("e")) {
            for (CEnchantment ce : EnchantManager.getEnchantments()) {
                if (ce.getOriginalName().equalsIgnoreCase(customName) || ChatColor.stripColor(ce.getDisplayName()).equalsIgnoreCase(customName)
                        || ce.getOriginalName().replace(" ", "").equalsIgnoreCase(customName)
                        || ChatColor.stripColor(ce.getDisplayName()).replace(" ", "").equalsIgnoreCase(customName)) {
                    custom = ce;
                    if (ce.getEnchantmentMaxLevel() < level) {
                        level = ce.getEnchantmentMaxLevel();
                    }
                }
            }
        } else {
            for (CItem ci : Main.items) {
                if (ci.getOriginalName().equalsIgnoreCase(customName) || ChatColor.stripColor(ci.getDisplayName()).equalsIgnoreCase(customName)
                        || ci.getOriginalName().replace(" ", "").equalsIgnoreCase(customName)
                        || ChatColor.stripColor(ci.getDisplayName()).replace(" ", "").equalsIgnoreCase(customName)) {
                    custom = ci;
                }
            }

            if (custom == null) {
                return ChatColor.RED + "The item '" + customName + "' does not exist.";
            }
        }

        if (custom == null) {
            Enchantment ench = null;
            try {
                ench = Enchantment.getById(Integer.parseInt(customName));
            } catch (Exception e) {
                try {
                    ench = Enchantment.getByName(customName);
                } catch (Exception ex) {
                }
            }

            if (ench != null) {
                if (item.containsEnchantment(ench)) {
                    int newLevel = item.getEnchantmentLevel(ench) + level;
                    item.removeEnchantment(ench);
                    item.addUnsafeEnchantment(ench, newLevel);
                    return ChatColor.GREEN + "You have succesfully increased the item's level of " + ench.getName() + " by " + level + ".";
                } else {
                    item.addUnsafeEnchantment(ench, level);
                    return ChatColor.GREEN + "You have succesfully enchanted your item with " + ench.getName() + " level " + level + ".";
                }
            }

            return ChatColor.RED + "The enchantment '" + customName + "' does not exist.";
        }

        if (item.getType().equals(Material.BOOK) && custom instanceof CEnchantment) {
            p.setItemInHand(EnchantManager.getEnchantBook((CEnchantment) custom, level));
            return ChatColor.GREEN + "You have created an enchanted book with '" + custom.getDisplayName() + ChatColor.GREEN + "' level " + level + "!";
        }

        if (!Tools.checkPermission(custom, p)) {
            return ChatColor.RED + "You do not have permission to use '" + customName + "'.";
        }

        List<String> lore = new ArrayList<>();
        ItemMeta im = item.getItemMeta();

        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            lore = item.getItemMeta().getLore();
            if (custom instanceof CEnchantment) {
                if (EnchantManager.containsEnchantment(lore, (CEnchantment) custom)) {
                    for (int i = 0; i < lore.size(); i++) {
                        if (EnchantManager.containsEnchantment(lore.get(i), (CEnchantment) custom)) {
                            int newLevel = EnchantManager.getLevel(lore.get(i)) + level;
                            int maxLevel = ((CEnchantment) custom).getEnchantmentMaxLevel();
                            if (EnchantManager.getLevel(lore.get(i)) == maxLevel) {
                                return ChatColor.RED + "You already have the maximum level of this enchantment!";
                            }
                            if (newLevel > maxLevel) {
                                newLevel = maxLevel;
                            }
                            lore.set(i, custom.getDisplayName() + " " + EnchantManager.intToLevel(newLevel));
                            im.setLore(lore);
                            item.setItemMeta(im);
                            p.setItemInHand(item);
                            return ChatColor.GREEN + "You have increased your item's level of " + custom.getDisplayName() + ChatColor.GREEN
                                    + (newLevel == maxLevel ? " to " + maxLevel : " by " + level) + "!";
                        }
                    }
                }
                int number = EnchantManager.getMaxEnchants();
                if (number > 0) {
                    for (String s : lore) {
                        if (EnchantManager.containsEnchantment(s)) {
                            number--;
                            if (number <= 0) {
                                return ChatColor.RED + "You already have the maximum number of Enchantments on your item!";
                            }
                        }
                    }
                }
            }
        }

        if (custom instanceof CEnchantment) {
            p.setItemInHand(EnchantManager.addEnchant(item, (CEnchantment) custom, level));
            return ChatColor.GREEN + "You have enchanted your item with '" + custom.getDisplayName() + ChatColor.GREEN + "' level " + level + "!";
        } else if (custom instanceof CItem) {
            ItemStack newItem = new ItemStack(((CItem) custom).getMaterial());
            ItemMeta newIm = newItem.getItemMeta();
            newIm.setDisplayName(custom.getDisplayName());
            newIm.setLore(((CItem) custom).getDescription());
            newItem.setItemMeta(newIm);
            if (custom instanceof Swimsuit) {
                int count = 0;
                for (ItemStack itemStack : p.getInventory()) {
                    if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
                        count++;
                    }
                }

                if (count < 4) {
                    return ChatColor.RED + "Your inventory is full.";
                }

                ItemStack cp = newItem.clone();
                ItemStack le = newItem.clone();
                ItemStack bo = newItem.clone();
                String[] swimParts = ((Swimsuit) custom).parts;

                cp.setType(Material.IRON_CHESTPLATE);
                le.setType(Material.IRON_LEGGINGS);
                bo.setType(Material.IRON_BOOTS);

                im.setDisplayName(swimParts[1]);
                cp.setItemMeta(im);
                im.setDisplayName(swimParts[2]);
                le.setItemMeta(im);
                im.setDisplayName(swimParts[3]);
                bo.setItemMeta(im);

                p.getInventory().addItem(newItem);
                p.getInventory().addItem(cp);
                p.getInventory().addItem(le);
                p.getInventory().addItem(bo);
            } else {
                if (p.getInventory().firstEmpty() == -1) {
                    return ChatColor.RED + "Your inventory is full!";
                } else {
                    p.getInventory().addItem(newItem);
                }
            }

            return ChatColor.GREEN + "You have created the item '" + custom.getDisplayName() + ChatColor.GREEN + "'!";
        }
        return ChatColor.RED + "No actions were taken.";
    }
}
