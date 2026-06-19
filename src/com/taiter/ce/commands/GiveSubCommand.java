package com.taiter.ce.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.taiter.ce.Main;
import com.taiter.ce.utils.Tools;
import com.taiter.ce.utils.Translator;
import com.taiter.ce.CItems.CItem;
import com.taiter.ce.CItems.utilities.Swimsuit;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.EnchantManager;

public class GiveSubCommand implements SubCommand {

    public GiveSubCommand(Main main) {
    }

    @SuppressWarnings("deprecation")
    @Override
    public String execute(CommandSender sender, String[] args) {
        String node = "ce.cmd.*";
        String requiredPermission = "ce.cmd.give";
        if (!sender.hasPermission(node) && !sender.hasPermission(requiredPermission) && !sender.isOp()) {
            return Translator.get("Commands.NoPermission", ChatColor.RED + "You do not have permission to execute this command.");
        }

        String usageError = Translator.get("Commands.GiveUsage", ChatColor.RED + "Correct Usage: /ce give <Player> <Material> <Enchantment:Level/Item> [Enchantment:Level] ...");
        if (args.length < 4) {
            return usageError;
        }

        Player target = null;
        for (Player ps : Bukkit.getOnlinePlayers()) {
            if (ps.getName().equalsIgnoreCase(args[1])) {
                target = ps;
            }
        }

        if (target == null) {
            return Translator.get("Commands.PlayerNotFound", ChatColor.RED + "The specified Player could not be found.");
        }

        if (target.getInventory().firstEmpty() < 0) {
            return Translator.get("Commands.PlayerInventoryFull", ChatColor.RED + "The Inventory of Player '%player%' is full.").replace("%player%", args[1]);
        }

        Material mat = null;
        try {
            mat = Material.getMaterial(Integer.parseInt(args[2]));
        } catch (Exception e) {
        }

        if (mat == null) {
            try {
                mat = Material.valueOf(args[2].toUpperCase());
            } catch (Exception e) {
                return ChatColor.RED + "The Material '" + args[2] + "' was not found.";
            }
        }

        String fullString = args[3];
        if (args.length > 4) {
            for (int i = 4; i < args.length; i++) {
                fullString += " " + args[i];
            }
        }

        fullString = fullString.toLowerCase();

        CItem custom = null;
        List<String> enchants = new ArrayList<>();
        List<String> cEnchants = new ArrayList<>();

        for (CItem ci : Main.items) {
            String origLower = ci.getOriginalName().toLowerCase();
            String origNoSpace = ci.getOriginalName().replace(" ", "").toLowerCase();
            String dispLower = ci.getDisplayName().toLowerCase();
            String dispNoSpace = ci.getDisplayName().replace(" ", "").toLowerCase();

            if (fullString.contains(origLower)) {
                custom = ci;
                fullString = fullString.replace(origLower, "");
            } else if (fullString.contains(origNoSpace)) {
                custom = ci;
                fullString = fullString.replace(origNoSpace, "");
            } else if (fullString.contains(dispLower)) {
                custom = ci;
                fullString = fullString.replace(dispLower, "");
            } else if (fullString.contains(dispNoSpace)) {
                custom = ci;
                fullString = fullString.replace(dispNoSpace, "");
            }
        }

        String[] parts = fullString.split(" ");
        for (int i = 0; i < parts.length; i++) {
            for (CEnchantment ce : EnchantManager.getEnchantments()) {
                int level = 0;
                String enchName = "";
                String targetOrig = ce.getOriginalName().toLowerCase();
                String targetOrigNoSpace = ce.getOriginalName().replace(" ", "").toLowerCase();
                String targetDisp = ce.getDisplayName().toLowerCase();
                String targetDispNoSpace = ce.getDisplayName().replace(" ", "").toLowerCase();

                if (fullString.contains(targetOrig)) {
                    enchName = targetOrig;
                } else if (fullString.contains(targetOrigNoSpace)) {
                    enchName = targetOrigNoSpace;
                } else if (fullString.contains(targetDisp)) {
                    enchName = targetDisp;
                } else if (fullString.contains(targetDispNoSpace)) {
                    enchName = targetDispNoSpace;
                }

                if (!enchName.isEmpty()) {
                    int index = fullString.indexOf(enchName);
                    int endIndex = index + enchName.length() + 2;
                    if (endIndex <= fullString.length()) {
                        enchName = fullString.substring(index, endIndex);
                    } else {
                        endIndex = index + enchName.length() + 1;
                        if (endIndex <= fullString.length()) {
                            enchName = fullString.substring(index, endIndex);
                        }
                    }

                    if (enchName.endsWith(" ")) {
                        enchName = enchName.trim();
                    }

                    if (enchName.contains(":")) {
                        String[] finalName = enchName.split(":");
                        try {
                            level = Integer.parseInt(finalName[1]);
                        } catch (Exception e) {
                        }
                        enchName = finalName[0];
                    }
                    fullString = fullString.replace(enchName, "");
                    cEnchants.add(ce.getDisplayName() + " " + level);
                }
            }

            for (Enchantment e : Enchantment.values()) {
                int level = 0;
                String enchName = e.getName().toLowerCase();

                if (fullString.contains(enchName)) {
                    int index = fullString.indexOf(enchName);
                    int endIndex = index + enchName.length() + 3;
                    if (endIndex <= fullString.length()) {
                        enchName = fullString.substring(index, endIndex);
                    } else {
                        endIndex = index + enchName.length() + 2;
                        if (endIndex <= fullString.length()) {
                            enchName = fullString.substring(index, endIndex);
                        }
                    }

                    if (enchName.endsWith(" ")) {
                        enchName = enchName.trim();
                    }

                    if (enchName.contains(":")) {
                        String[] finalName = enchName.split(":");
                        try {
                            level = Integer.parseInt(finalName[1]);
                        } catch (Exception ex) {
                        }
                        enchName = finalName[0];
                    }
                    fullString = fullString.replace(enchName, "");
                    enchants.add(e.getName() + " " + level);
                }
            }
        }

        ItemStack newItem = new ItemStack(mat);
        ItemMeta im = newItem.getItemMeta();
        String targetNotification = ChatColor.GOLD + "";
        String successMsg = "";

        if (custom != null) {
            if (Tools.checkPermission(custom, target)) {
                im.setDisplayName(custom.getDisplayName());
                im.setLore(custom.getDescription());
                newItem.setItemMeta(im);
                if (custom instanceof Swimsuit) {
                    int count = 0;
                    for (ItemStack itemStack : target.getInventory()) {
                        if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
                            count++;
                        }
                    }

                    if (count < 4) {
                        return Translator.get("Commands.PlayerInventoryFull", ChatColor.RED + "The Inventory of Player '%player%' is full.").replace("%player%", args[1]);
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

                    target.getInventory().addItem(newItem);
                    target.getInventory().addItem(cp);
                    target.getInventory().addItem(le);
                    target.getInventory().addItem(bo);
                }
                successMsg += ChatColor.stripColor(Translator.get("Commands.GiveSuccess", "The enchanted Item was given to Player %player%.")).replace("%player%", target.getName()) + " ";
                targetNotification += Translator.get("Commands.ReceiveNotification", ChatColor.GOLD + "You have received an enchanted item from %sender%!").replace("%sender%", sender.getName());
            } else {
                return Translator.get("Commands.NoPermissionTarget", ChatColor.RED + "%player% does not have the permission to use the item %item%.").replace("%player%", target.getName()).replace("%item%", custom.getOriginalName());
            }
        }

        if (!enchants.isEmpty()) {
            for (String e : enchants) {
                String[] enchALvl = e.split(" ");
                Enchantment ench = Enchantment.getByName(enchALvl[0]);
                int level = 1;
                try {
                    level = Integer.parseInt(enchALvl[1]);
                } catch (Exception ex) {
                }
                newItem.addUnsafeEnchantment(ench, level);
            }
            if (successMsg.length() < 10) {
                successMsg += ChatColor.stripColor(Translator.get("Commands.GiveSuccess", "The enchanted Item was given to Player %player%.")).replace("%player%", target.getName()) + " ";
                targetNotification += Translator.get("Commands.ReceiveNotification", ChatColor.GOLD + "You have received an enchanted item from %sender%!").replace("%sender%", sender.getName());
            }
        }

        if (!cEnchants.isEmpty()) {
            HashMap<CEnchantment, Integer> list = new HashMap<>();
            for (String e : cEnchants) {
                String[] split = e.split(" ");
                list.put(EnchantManager.getEnchantment(e), Integer.parseInt(split[split.length - 1]));
            }

            if (newItem.getType().equals(Material.BOOK)) {
                newItem = EnchantManager.getEnchantBook(list);
            } else {
                newItem = EnchantManager.addEnchantments(newItem, list);
            }
            if (successMsg.length() < 10) {
                successMsg += ChatColor.stripColor(Translator.get("Commands.GiveSuccess", "The enchanted Item was given to Player %player%.")).replace("%player%", target.getName()) + " ";
                targetNotification += Translator.get("Commands.ReceiveNotification", ChatColor.GOLD + "You have received an enchanted item from %sender%!").replace("%sender%", sender.getName());
            }
        }

        if (successMsg.length() > 10) {
            target.getInventory().addItem(newItem);
            target.sendMessage(targetNotification);
            return ChatColor.GREEN + successMsg;
        } else {
            return Translator.get("Commands.NoActionsTaken", ChatColor.RED + "No actions were taken.");
        }
    }
}
