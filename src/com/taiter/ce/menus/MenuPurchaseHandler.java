package com.taiter.ce.menus;

import com.taiter.ce.utils.Tools;
import com.taiter.ce.utils.Translator;
import com.taiter.ce.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.milkbowl.vault.economy.EconomyResponse;
import com.taiter.ce.CItems.CItem;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.EnchantManager;

public class MenuPurchaseHandler {

    public static void handlePurchaseItem(Player p, ItemStack clickedItem) {
        CItem ci = Tools.getItemByDisplayname(clickedItem.getItemMeta().getDisplayName());
        if (!p.hasPermission("ce.item.*") && !p.hasPermission("ce.item." + ci.getPermissionName())) {
            p.sendMessage(Translator.get("Menu.Messages.NoPermissionBuy", ChatColor.RED + "You do not have permission to buy this Item!"));
            return;
        }

        if (p.getInventory().firstEmpty() != -1) {
            double cost = ci.getCost();
            if (Main.hasEconomy && !p.isOp() && cost > 0) {
                if (Main.econ.getBalance(p.getName()) >= cost) {
                    EconomyResponse ecr = Main.econ.withdrawPlayer(p.getName(), cost);
                    if (ecr.transactionSuccess()) {
                        String currency = (cost == 1) ? Main.econ.currencyNameSingular() : Main.econ.currencyNamePlural();
                        p.sendMessage(Translator.get("Menu.Messages.Purchased", ChatColor.GREEN + "Purchased %item% " + ChatColor.GREEN + "for " + ChatColor.WHITE + "%cost% %currency%")
                                .replace("%item%", clickedItem.getItemMeta().getDisplayName())
                                .replace("%cost%", String.valueOf(cost))
                                .replace("%currency%", currency));
                        ItemMeta im = clickedItem.getItemMeta();
                        im.setLore(ci.getDescription());
                        clickedItem.setItemMeta(im);
                    } else {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "An economy error has occured:");
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + ecr.errorMessage);
                        p.closeInventory();
                        return;
                    }
                } else {
                    p.sendMessage(Translator.get("Menu.Messages.NoMoney", ChatColor.RED + "You do not have enough money to buy this!"));
                    return;
                }
            } else {
                p.sendMessage(Translator.get("Menu.Messages.Created", ChatColor.GREEN + "Created %item%" + ChatColor.GREEN + "!").replace("%item%", clickedItem.getItemMeta().getDisplayName()));
            }

            p.getInventory().addItem(clickedItem);
            p.closeInventory();
        } else {
            p.sendMessage(Translator.get("Menu.Messages.NoSpace", ChatColor.RED + "You do not have enough space in your inventory!"));
        }
    }

    public static void handlePurchaseLevelSelection(Player p, ItemStack clickedItem) {
        String enchantmentName = clickedItem.getItemMeta().getDisplayName();
        CEnchantment ce = EnchantManager.getEnchantment(enchantmentName);
        int level = EnchantManager.getLevel(enchantmentName);
        double cost = ce.getCost(level);

        if (p.getInventory().firstEmpty() != -1) {
            if (Main.hasEconomy && !p.isOp() && cost > 0) {
                if (Main.econ.getBalance(p.getName()) >= cost) {
                    EconomyResponse ecr = Main.econ.withdrawPlayer(p.getName(), cost);
                    if (ecr.transactionSuccess()) {
                        String currency = (cost == 1) ? Main.econ.currencyNameSingular() : Main.econ.currencyNamePlural();
                        p.sendMessage(Translator.get("Menu.Messages.Purchased", ChatColor.GREEN + "Purchased %item% " + ChatColor.GREEN + "for " + ChatColor.WHITE + "%cost% %currency%")
                                .replace("%item%", clickedItem.getItemMeta().getDisplayName())
                                .replace("%cost%", String.valueOf(cost))
                                .replace("%currency%", currency));
                    } else {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "An economy error has occured:");
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + ecr.errorMessage);
                        p.closeInventory();
                        return;
                    }
                } else {
                    p.sendMessage(Translator.get("Menu.Messages.NoMoney", ChatColor.RED + "You do not have enough money to buy this!"));
                    return;
                }
            } else {
                p.sendMessage(Translator.get("Menu.Messages.Created", ChatColor.GREEN + "Created %item%" + ChatColor.GREEN + "!").replace("%item%", clickedItem.getItemMeta().getDisplayName()));
            }

            p.getInventory().addItem(EnchantManager.getEnchantBook(ce, level));
            p.closeInventory();
        } else {
            p.sendMessage(Translator.get("Menu.Messages.NoSpace", ChatColor.RED + "You do not have enough space in your inventory!"));
        }
    }
}
