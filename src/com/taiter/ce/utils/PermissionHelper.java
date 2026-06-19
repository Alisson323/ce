package com.taiter.ce.utils;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import com.taiter.ce.Main;
import com.taiter.ce.CItems.CItem;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.EnchantManager;

public class PermissionHelper {

    public static void writePermissions() {
        Permission mainNode = new Permission("ce.*", "The main permission node for Custom Enchantments.", PermissionDefault.OP);
        Permission runecrafting = new Permission("ce.runecrafting", "The permission for Runecrafting.", PermissionDefault.OP);
        runecrafting.addParent(mainNode, true);

        Permission cmdNode = new Permission("ce.cmd.*", "The permission node for CE's commands.", PermissionDefault.OP);
        Permission enchNode = new Permission("ce.ench.*", "The permission node for CE's EnchantManager.getEnchantments().", PermissionDefault.OP);
        Permission itemNode = new Permission("ce.item.*", "The permission node for CE's  items.", PermissionDefault.OP);

        cmdNode.addParent(mainNode, true);
        enchNode.addParent(mainNode, true);
        itemNode.addParent(mainNode, true);

        Permission cmdMenu = new Permission("ce.cmd.menu", "The permission for the CE command 'menu'");
        Permission cmdList = new Permission("ce.cmd.reload", "The permission for the CE command 'reload'");
        Permission cmdGive = new Permission("ce.cmd.give", "The permission for the CE command 'give'");
        Permission cmdChange = new Permission("ce.cmd.change", "The permission for the CE command 'change'");
        Permission cmdEnchant = new Permission("ce.cmd.enchant", "The permission for the CE command 'enchant'");
        Permission cmdRunecraft = new Permission("ce.cmd.runecrafting", "The permission for the CE command 'runecrafting'");

        cmdMenu.addParent(cmdNode, true);
        cmdList.addParent(cmdNode, true);
        cmdGive.addParent(cmdNode, true);
        cmdChange.addParent(cmdNode, true);
        cmdEnchant.addParent(cmdNode, true);
        cmdRunecraft.addParent(cmdNode, true);

        Bukkit.getServer().getPluginManager().addPermission(mainNode);
        Bukkit.getServer().getPluginManager().addPermission(runecrafting);
        Bukkit.getServer().getPluginManager().addPermission(cmdNode);
        Bukkit.getServer().getPluginManager().addPermission(enchNode);
        Bukkit.getServer().getPluginManager().addPermission(itemNode);
        Bukkit.getServer().getPluginManager().addPermission(cmdMenu);
        Bukkit.getServer().getPluginManager().addPermission(cmdList);
        Bukkit.getServer().getPluginManager().addPermission(cmdGive);
        Bukkit.getServer().getPluginManager().addPermission(cmdChange);
        Bukkit.getServer().getPluginManager().addPermission(cmdEnchant);
        Bukkit.getServer().getPluginManager().addPermission(cmdRunecraft);

        for (CItem ci : Main.items) {
            Permission itemTemp = new Permission("ce.item." + ci.getPermissionName(), "The permission for the CE Item '" + ci.getOriginalName() + "'.");
            itemTemp.addParent(itemNode, true);
            Bukkit.getServer().getPluginManager().addPermission(itemTemp);
        }

        for (CEnchantment ce : EnchantManager.getEnchantments()) {
            Permission enchTemp = new Permission("ce.ench." + ce.getPermissionName(), "The permission for the CE Enchantment '" + ce.getOriginalName() + "'.");
            enchTemp.addParent(enchNode, true);
            Bukkit.getServer().getPluginManager().addPermission(enchTemp);
        }
    }
}
