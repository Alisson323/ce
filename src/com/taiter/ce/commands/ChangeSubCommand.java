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

public class ChangeSubCommand implements SubCommand {
    public ChangeSubCommand(com.taiter.ce.Main main) {
    }

    @Override
    public String execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return ChatColor.RED + "This command can only be used by players";
        }

        Player p = (Player) sender;
        String node = "ce.cmd.*";
        String requiredPermission = "ce.cmd.change";
        if (!sender.hasPermission(node) && !sender.hasPermission(requiredPermission) && !sender.isOp()) {
            return ChatColor.RED + "You do not have permission to use this command.";
        }

        String usageError = ChatColor.RED + "Correct Usage: /ce change <name/lore> <color/set/add/reset> [New Value]";
        ItemStack item = p.getItemInHand();
        if (item == null || item.getType() == Material.AIR) {
            return ChatColor.RED + "You are not holding an item in your hand";
        }

        if (args.length == 3) {
            if (args[2].toLowerCase().startsWith("r")) {
                ItemMeta im = item.getItemMeta();
                im.setLore(new ArrayList<String>());
                item.setItemMeta(im);
                return ChatColor.GREEN + "You have successfully reset the item's lore!";
            }
        }

        if (args.length < 4) {
            return usageError;
        }

        String toChange = args[1].toLowerCase();
        String option = args[2].toLowerCase();

        if (toChange.startsWith("n")) {
            ItemMeta im = item.getItemMeta();
            if (option.startsWith("s")) {
                String toSet = "";
                for (int i = 3; i < args.length - 1; i++) {
                    toSet += args[i] + " ";
                }
                toSet += args[args.length - 1];
                toSet = ChatColor.translateAlternateColorCodes('&', toSet);
                im.setDisplayName(toSet);
                item.setItemMeta(im);
                return ChatColor.GREEN + "You have successfully set the item's Name!";
            }

            if (item.hasItemMeta() && im.hasDisplayName()) {
                if (option.startsWith("c")) {
                    String test = args[3].toUpperCase();
                    try {
                        test = ChatColor.valueOf(test) + "";
                    } catch (IllegalArgumentException e) {
                        if (test.contains("&")) {
                            test = ChatColor.translateAlternateColorCodes('&', test);
                        } else {
                            return ChatColor.RED + "The Color " + args[3] + " could not be found.";
                        }
                    }

                    im.setDisplayName(test + ChatColor.stripColor(im.getDisplayName()));
                    item.setItemMeta(im);
                    return ChatColor.GREEN + "You have successfully changed the item's Color!";
                } else if (option.startsWith("a")) {
                    String toSet = "";
                    for (int i = 3; i < args.length - 1; i++) {
                        toSet += args[i] + " ";
                    }
                    toSet += args[args.length - 1];
                    im.setDisplayName(im.getDisplayName() + " " + toSet);
                    item.setItemMeta(im);
                    return ChatColor.GREEN + "You have successfully changed the item's Name!";
                } else if (option.startsWith("r")) {
                    im.setDisplayName(null);
                    item.setItemMeta(im);
                    return ChatColor.GREEN + "You have successfully reset the item's Name!";
                }
            } else {
                return ChatColor.RED + "Your item does not have a name to be changed, use '/ce change name set' first.";
            }
        } else if (toChange.startsWith("l")) {
            ItemMeta im = item.getItemMeta();
            if (option.startsWith("s")) {
                List<String> lore = new ArrayList<>();
                String toSet = "";
                for (int i = 3; i < args.length - 1; i++) {
                    toSet += args[i] + " ";
                }
                toSet += args[args.length - 1];
                lore.add(toSet);
                im.setLore(lore);
                item.setItemMeta(im);
                return ChatColor.GREEN + "You have successfully set the item's lore!";
            }

            if (item.hasItemMeta() && im.hasLore()) {
                List<String> lore = im.getLore();
                if (option.startsWith("c")) {
                    try {
                        ChatColor color = ChatColor.valueOf(args[3].toUpperCase());
                        List<String> l = new ArrayList<>();
                        for (String i : lore) {
                            l.add(color + ChatColor.stripColor(i));
                        }
                        im.setLore(l);
                        item.setItemMeta(im);
                        return ChatColor.GREEN + "You have successfully changed the color of the item's lore!";
                    } catch (IllegalArgumentException e) {
                        return ChatColor.RED + "The Color " + args[3] + " could not be found.";
                    }
                } else if (option.startsWith("a")) {
                    String toSet = "";
                    for (int i = 3; i < args.length - 1; i++) {
                        toSet += args[i] + " ";
                    }
                    toSet += args[args.length - 1];
                    lore.add(toSet);
                    im.setLore(lore);
                    item.setItemMeta(im);
                    return ChatColor.GREEN + "You have successfully added the new line to the lore!";
                }
            } else {
                return ChatColor.RED + "Your item does not have a lore to be changed, use '/ce change lore set' first.";
            }
        }
        return usageError;
    }
}
