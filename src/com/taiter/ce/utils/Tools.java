package com.taiter.ce.utils;

import com.taiter.ce.Main;
import com.taiter.ce.effects.VisualEffects;
import com.taiter.ce.CBasic;
import com.taiter.ce.menus.MenuManager;
import com.taiter.ce.hooks.WorldGuardHook;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import com.taiter.ce.CBasic.Trigger;
import com.taiter.ce.CItems.CItem;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.CEnchantment.Application;
import com.taiter.ce.Enchantments.EnchantManager;

public class Tools {

    public static String prefix = "CE - ";
    public static Random random = new Random();

    public static boolean isApplicationCorrect(Application app, Material matToApplyTo) {
        String mat = matToApplyTo.toString();
        if (app == Application.BOW && mat.equals(Material.BOW.toString()))
            return true;
        else if (app == Application.BOOTS && mat.endsWith("BOOTS"))
            return true;
        else if (app == Application.HELMET && mat.endsWith("HELMET"))
            return true;
        else if (app == Application.ARMOR && (mat.endsWith("HELMET") || mat.endsWith("CHESTPLATE") || mat.endsWith("LEGGINGS") || mat.endsWith("BOOTS")))
            return true;
        else if (app == Application.TOOL && (mat.endsWith("PICKAXE") || mat.endsWith("SPADE") || mat.endsWith("AXE") || mat.endsWith("HOE")))
            return true;
        return false;
    }

    public static CItem getItemByOriginalname(String name) {
        for (CItem ci : Main.items)
            if (ci.getOriginalName().equals(name))
                return ci;
        return null;
    }

    public static CItem getItemByDisplayname(String name) {
        for (CItem ci : Main.items)
            if (ci.getDisplayName().equals(name))
                return ci;
        return null;
    }

    public static Inventory getPreviousInventory(String name) {
        return MenuManager.getPreviousInventory(name);
    }

    public static Inventory getNextInventory(String name) {
        return MenuManager.getNextInventory(name);
    }

    public static Inventory getEnchantmentMenu(Player p, String name) {
        return MenuManager.getEnchantmentMenu(p, name);
    }

    public static Inventory getItemMenu(Player p) {
        return MenuManager.getItemMenu(p);
    }

    public static void generateInventories() {
        MenuManager.generateInventories();
    }

    public static boolean checkPermission(CBasic cb, Player p) {
        String name = "ce.";
        if (cb instanceof CItem)
            name += "item.";
        else
            name += "ench.";
        
        if (p.hasPermission(name + "*"))
            return true;
        if (p.hasPermission(name + cb.getOriginalName()))
            return true;
        if (p.hasPermission(name + cb.getPermissionName()))
            return true;
        return false;
    }

    public static void convertOldConfig() {
        Main.plugin.getConfig().set("Global.Enchantments.CEnchantmentColor",
                (Boolean.parseBoolean(Main.config.getString("enchantments.lore.disableItalic")) ? "" : "ITALIC;") + ChatColor.valueOf(Main.config.getString("enchantments.lore.color")));
        Main.plugin.getConfig().set("Global.Enchantments.CEnchantmentTable", Boolean.parseBoolean(Main.config.getString("enchantmentTable")));
        Main.plugin.getConfig().set("Global.Enchantments.CEnchantingProbability", Integer.parseInt(Main.config.getString("enchantmentTableProbability")));
        Main.plugin.getConfig().set("Global.Enchantments.MaximumCustomEnchantments", Integer.parseInt(Main.config.getString("maximumEnchants")));

        Main.plugin.getConfig().set("enchantments.requirePermissions", null);
        Main.plugin.getConfig().set("enchantmentTable", null);
        Main.plugin.getConfig().set("enchantmentTableProbability", null);
        Main.plugin.getConfig().set("commandBypass", null);
        Main.plugin.getConfig().set("AntiMcMMOrepair", null);
        Main.plugin.getConfig().set("restrictEnchantments", null);
        Main.plugin.getConfig().set("maximumEnchants", null);
        Main.plugin.getConfig().set("enchantments", null);
        Main.plugin.getConfig().set("items", null);

        Main.plugin.saveConfig();
        Main.config = Main.plugin.getConfig();
    }

    public static void writeConfigEntries(CBasic ce) {
        for (String entry : ce.configEntries.keySet()) {
            String fullPath = (ce.getType().equals("Enchantment") ? "Enchantments" : ce.getType()) + "." + ce.getOriginalName() + "." + entry;
            if (!Main.plugin.getConfig().contains(fullPath))
                Main.plugin.getConfig().set(fullPath, ce.configEntries.get(entry));
        }
        Main.plugin.saveConfig();
        Main.plugin.reloadConfig();
        Main.config = Main.plugin.getConfig();
    }

    public static void resolveLists() {
        for (CEnchantment ce : EnchantManager.getEnchantments())
            for (Trigger t : ce.getTriggers())
                getAppropriateList(t).add(ce);
        for (CItem ci : Main.items)
            for (Trigger t : ci.getTriggers())
                getAppropriateList(t).add(ci);
    }

    public static boolean checkWorldGuard(Location l, Player p, String fs, boolean sendMessage) {
        return WorldGuardHook.checkWorldGuard(l, p, fs, sendMessage);
    }

    private static HashSet<CBasic> getAppropriateList(Trigger t) {
        if (t == Trigger.BLOCK_BROKEN)
            return Main.listener.blockBroken;
        else if (t == Trigger.BLOCK_PLACED)
            return Main.listener.blockPlaced;
        else if (t == Trigger.INTERACT)
            return Main.listener.interact;
        else if (t == Trigger.INTERACT_ENTITY)
            return Main.listener.interactE;
        else if (t == Trigger.INTERACT_LEFT)
            return Main.listener.interactL;
        else if (t == Trigger.INTERACT_RIGHT)
            return Main.listener.interactR;
        else if (t == Trigger.DEATH)
            return Main.listener.death;
        else if (t == Trigger.DAMAGE_GIVEN)
            return Main.listener.damageGiven;
        else if (t == Trigger.DAMAGE_TAKEN)
            return Main.listener.damageTaken;
        else if (t == Trigger.DAMAGE_NATURE)
            return Main.listener.damageNature;
        else if (t == Trigger.SHOOT_BOW)
            return Main.listener.shootBow;
        else if (t == Trigger.PROJECTILE_HIT)
            return Main.listener.projectileHit;
        else if (t == Trigger.PROJECTILE_THROWN)
            return Main.listener.projectileThrow;
        else if (t == Trigger.WEAR_ITEM)
            return Main.listener.wearItem;
        else if (t == Trigger.MOVE)
            return Main.listener.move;
        return null;
    }

    public static List<CEnchantment> getEnchantList(Application app) {
        List<CEnchantment> list = new ArrayList<>();
        for (CEnchantment ce : EnchantManager.getEnchantments())
            if (ce.getApplication() == app)
                list.add(ce);
        return list;
    }

    public static HashSet<CEnchantment> getEnchantList(Application app, Player p) {
        HashSet<CEnchantment> list = new HashSet<>();
        for (CEnchantment ce : EnchantManager.getEnchantments())
            if (ce.getApplication() == app)
                if (!Boolean.parseBoolean(Main.config.getString("Global.Enchantments.RequirePermissions")) || checkPermission(ce, p))
                    list.add(ce);
        return list;
    }

    public static Application getApplicationByMaterial(Material material) {
        String mat = material.toString();
        if (mat.equals(Material.BOW.toString()))
            return Application.BOW;
        else if (mat.endsWith("BOOTS"))
            return Application.BOOTS;
        else if (mat.endsWith("HELMET"))
            return Application.HELMET;
        else if (mat.endsWith("BOOTS") || mat.endsWith("LEGGINGS") || mat.endsWith("CHESTPLATE") || mat.endsWith("HELMET"))
            return Application.ARMOR;
        else if (mat.endsWith("PICKAXE") || mat.endsWith("SPADE") || mat.endsWith("AXE") || mat.endsWith("HOE"))
            return Application.TOOL;
        return Application.GLOBAL;
    }

    public static boolean isApplicable(ItemStack i, CEnchantment ce) {
        if ((ce.getApplication() == Application.ARMOR && ce.getApplication() != Application.GLOBAL
                && (i.getType().toString().endsWith("HELMET") || i.getType().toString().endsWith("CHESTPLATE") || i.getType().toString().endsWith("LEGGINGS")
                        || i.getType().toString().endsWith("BOOTS")))
                || (ce.getApplication() == Application.TOOL && (i.getType().toString().endsWith("PICKAXE") || i.getType().toString().endsWith("SPADE") || i.getType().toString().endsWith("_AXE")
                        || i.getType().toString().endsWith("HOE")))
                || (ce.getApplication() == Application.HELMET && ce.getApplication() != Application.GLOBAL && i.getType().toString().endsWith("HELMET"))
                || (ce.getApplication() == Application.BOOTS && ce.getApplication() != Application.GLOBAL && i.getType().toString().endsWith("BOOTS"))
                || (ce.getApplication() == Application.BOW && i.getType().equals(Material.BOW)) || ce.getApplication() == Application.GLOBAL)
            return true;
        return false;
    }

    public static Firework shootFirework(Location loc, Random rand) {
        return VisualEffects.shootFirework(loc, rand);
    }

    public static void applyBleed(Player target, int bleedDuration) {
        VisualEffects.applyBleed(target, bleedDuration);
    }

    public static List<Location> getLinePlayer(Player player, int length) {
        return LocationHelper.getLinePlayer(player, length);
    }

    public static List<Location> getCone(Location loc) {
        return LocationHelper.getCone(loc);
    }
}
