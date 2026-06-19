package com.taiter.ce.Enchantments;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.taiter.ce.Main;
import com.taiter.ce.utils.LevelConverter;

/*
* This file is part of Custom Enchantments
* Copyright (C) Taiterio 2015
*
* This program is free software: you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as published by the
* Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
* for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

public class EnchantManager {

    private static Set<CEnchantment> enchantments = new LinkedHashSet<CEnchantment>();
    private static Enchantment glowEnchantment;
    private static int maxEnchants = -1;

    private static String lorePrefix;
    private static String enchantBookName;

    static {
        //Load the glow enchantment
        glowEnchantment = registerGlowEnchantment();
    }

    private static Enchantment registerGlowEnchantment() {
        return Enchantment.DURABILITY;
    }

    public static ItemStack addEnchant(ItemStack item, CEnchantment ce) {
        return addEnchant(item, ce, 1);
    }

    public static ItemStack addEnchant(ItemStack item, CEnchantment ce, int level) {
        ItemMeta im = item.getItemMeta();
        List<String> lore = new ArrayList<String>();
        if (im.hasLore()) {
            lore = im.getLore();
            if (maxEnchants < enchantments.size()) {
                int counter = maxEnchants;
                for (String s : lore)
                    if (containsEnchantment(s)) {
                        counter--;
                        if (counter <= 0) {
                            return item;
                        }
                    }
            }
        }
        if (level > ce.getEnchantmentMaxLevel())
            level = ce.getEnchantmentMaxLevel();
        lore.add(lorePrefix + ce.getDisplayName() + " " + intToLevel(level));
        im.setLore(lore);
        try {
            im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        } catch (Throwable t) {}
        item.setItemMeta(im);
        item.addUnsafeEnchantment(glowEnchantment, 1);
        return item;
    }

    public static ItemStack addEnchantments(ItemStack item, HashMap<CEnchantment, Integer> list) {
        ItemMeta im = item.getItemMeta();
        List<String> lore = new ArrayList<String>();
        if (im.hasLore()) {
            lore = im.getLore();
            if (maxEnchants < enchantments.size()) {
                int counter = maxEnchants - list.size();
                for (String s : lore)
                    if (containsEnchantment(s)) {
                        counter--;
                        if (counter <= 0) {
                            return item;
                        }
                    }
            }
        }
        for (CEnchantment ce : list.keySet()) {
            int level = list.get(ce);
            if (level > ce.getEnchantmentMaxLevel())
                level = ce.getEnchantmentMaxLevel();
            lore.add(lorePrefix + ce.getDisplayName() + " " + intToLevel(level));
        }
        im.setLore(lore);
        try {
            im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        } catch (Throwable t) {}
        item.setItemMeta(im);
        item.addUnsafeEnchantment(glowEnchantment, 1);
        return item;
    }

    public static boolean hasEnchant(ItemStack item, CEnchantment ce) {
        ItemMeta im = item.getItemMeta();
        List<String> lore = im.getLore();
        for (String s : lore)
            if (s.startsWith(ce.getDisplayName()) || s.startsWith(lorePrefix + ce.getOriginalName()))
                return true;
        return false;
    }

    public static void removeEnchant(ItemStack item, CEnchantment ce) {
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore())
            return;
        ItemMeta im = item.getItemMeta();
        List<String> lore = im.getLore();
        for (String s : lore)
            if (s.startsWith(ce.getDisplayName()) || s.startsWith(ce.getOriginalName())) {
                lore.remove(s);
                im.setLore(lore);
                item.setItemMeta(im);
                boolean hasMoreCustom = false;
                for (String line : lore) {
                    if (containsEnchantment(line)) {
                        hasMoreCustom = true;
                        break;
                    }
                }
                if (!hasMoreCustom) {
                    if (item.getEnchantments().containsKey(glowEnchantment))
                        item.removeEnchantment(glowEnchantment);
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        try {
                            meta.removeItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
                        } catch (Throwable t) {}
                        item.setItemMeta(meta);
                    }
                }
                return;
            }
    }

    /**
     * Retrieves an enchantment by its original name. Assumes that the given String is not colored and equals the name
     * of an enchantment.
     * 
     * @param originalName
     *            The original name of the enchantment to retrieve
     * @return The enchantment specified by originalName
     */
    public static CEnchantment getInternalEnchantment(String originalName) {
        return enchantments.stream()
                .filter(ce -> ce.getOriginalName().equals(originalName))
                .findFirst()
                .orElse(null);
    }

    public static CEnchantment getEnchantment(String name) {
        if (name.length() > 3)
            for (CEnchantment ce : enchantments) {
                String enchantment = ChatColor.stripColor(ce.getDisplayName()).toLowerCase();
                name = ChatColor.stripColor(name).toLowerCase();
                if (name.startsWith(enchantment) || name.startsWith(ce.getOriginalName().toLowerCase())) {
                    String[] split = name.split(" ");
                    if (split.length == enchantment.split(" ").length + 1) {
                        name = name.substring(0, name.length() - 1 - split[split.length - 1].length());
                        if (name.equals(enchantment) || name.equals(ce.getOriginalName()))
                            return ce;
                    } else {
                        if (name.equals(enchantment) || name.equals(ce.getOriginalName()))
                            return ce;
                    }
                }
            }
        return null;
    }

    public static Set<CEnchantment> getEnchantments() {
        return enchantments;
    }

    public static Set<CEnchantment> getEnchantments(List<String> lore) {
        Set<CEnchantment> list = new LinkedHashSet<CEnchantment>();
        if (lore != null) {
            for (String line : lore) {
                if (line.length() > 3) {
                    String cleanLine = ChatColor.stripColor(line).toLowerCase();
                    for (CEnchantment ce : enchantments) {
                        String enchantment = ChatColor.stripColor(ce.getDisplayName()).toLowerCase();
                        String origName = ce.getOriginalName().toLowerCase();
                        if (cleanLine.startsWith(enchantment) || cleanLine.startsWith(origName)) {
                            String[] split = cleanLine.split(" ");
                            String nameWithoutLevel = cleanLine.substring(0, cleanLine.length() - 1 - split[split.length - 1].length());
                            if (nameWithoutLevel.equals(enchantment) || nameWithoutLevel.equals(ce.getOriginalName().toLowerCase())) {
                                list.add(ce);
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    public static HashMap<CEnchantment, Integer> getEnchantmentLevels(List<String> lore) {
        HashMap<CEnchantment, Integer> list = new HashMap<CEnchantment, Integer>();
        if (lore != null) {
            for (String line : lore) {
                if (line.length() > 3) {
                    String cleanLine = ChatColor.stripColor(line).toLowerCase();
                    for (CEnchantment ce : enchantments) {
                        String enchantment = ChatColor.stripColor(ce.getDisplayName()).toLowerCase();
                        String origName = ce.getOriginalName().toLowerCase();
                        if (cleanLine.startsWith(enchantment) || cleanLine.startsWith(origName)) {
                            String[] split = cleanLine.split(" ");
                            String nameWithoutLevel = cleanLine.substring(0, cleanLine.length() - 1 - split[split.length - 1].length());
                            if (nameWithoutLevel.equals(enchantment) || nameWithoutLevel.equals(ce.getOriginalName().toLowerCase())) {
                                list.put(ce, levelToInt(split[split.length - 1]));
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    public static Boolean hasEnchantments(ItemStack toTest) {
        if (toTest != null)
            if (toTest.hasItemMeta() && toTest.getItemMeta().hasLore())
                return toTest.getItemMeta().getLore().stream().anyMatch(EnchantManager::containsEnchantment);
        return false;
    }

    public static boolean isEnchantmentBook(ItemStack i) {
        if (i != null && i.getType().equals(Material.ENCHANTED_BOOK)) {
            if (i.hasItemMeta()) {
                if (i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().equals(enchantBookName)) {
                    return true;
                }
                if (hasEnchantments(i)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isEnchantable(String mat) {
        if (mat.contains("HELMET") || mat.contains("CHESTPLATE") || mat.contains("LEGGINGS") || mat.contains("BOOTS") || mat.contains("SWORD") || mat.contains("PICKAXE") || mat.contains("AXE")
                || mat.contains("SPADE") || mat.contains("HOE") || mat.equals("BOW"))
            return true;
        if ((Main.config.getBoolean("Global.Runecrafting.Disenchanting") && mat.equals("BOOK"))
                || ((Main.config.getBoolean("Global.Runecrafting.CanStackEnchantments") && mat.equals("ENCHANTED_BOOK"))))
            return true;
        return false;
    }

    public static Boolean containsEnchantment(List<String> toTest) {
        return toTest.stream().anyMatch(EnchantManager::containsEnchantment);
    }

    public static Boolean containsEnchantment(String toTest) {
        return enchantments.stream().anyMatch(ce -> containsEnchantment(toTest, ce));
    }

    public static Boolean containsEnchantment(List<String> toTest, CEnchantment ce) {
        return toTest.stream().anyMatch(s -> containsEnchantment(s, ce));
    }

    public static Boolean containsEnchantment(String toTest, CEnchantment ce) {
        if (toTest.startsWith(ChatColor.YELLOW + "" + ChatColor.ITALIC + "\""))
            toTest = lorePrefix + ChatColor.stripColor(toTest.replace("\"", ""));
        String next = "";
        if (toTest.startsWith(lorePrefix + ce.getOriginalName()))
            next = lorePrefix + ce.getOriginalName();
        if (toTest.startsWith(ce.getDisplayName()))
            next = ce.getDisplayName();
        if (next.isEmpty())
            return false;
        String nextTest = toTest.replace(next, "");

        if (nextTest.startsWith(" ") || nextTest.isEmpty())
            return true;
        return false;
    }

    public static String getLorePrefix() {
        return lorePrefix;
    }

    public static int getMaxEnchants() {
        return maxEnchants;
    }

    public static Enchantment getGlowEnchantment() {
        return glowEnchantment;
    }

    /*
     * This returns the enchantment level of the CE identified by checkEnchant
     */
    public static int getLevel(String checkEnchant) {
        int level = 1;
        if (checkEnchant.contains(" ")) {
            String[] splitName = checkEnchant.split(" ");
            String possibleLevel = splitName[splitName.length - 1];
            level = levelToInt(possibleLevel);
        }
        return level;
    }

    public static String intToLevel(int i) {
        return LevelConverter.intToLevel(i);
    }

    public static int levelToInt(String level) {
        return LevelConverter.levelToInt(level);
    }

    public static String getEnchantBookName() {
        return enchantBookName;
    }

    public static ItemStack getEnchantBook(CEnchantment ce) {
        return getEnchantBook(ce, 1);
    }

    public static ItemStack getEnchantBook(CEnchantment ce, int level) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta im = item.getItemMeta();
        im.setLore(Arrays.asList(new String[] { lorePrefix + ce.getDisplayName() + " " + intToLevel(level) }));
        try {
            im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        } catch (Throwable t) {}
        item.setItemMeta(im);
        item.addUnsafeEnchantment(glowEnchantment, 1);
        return item;
    }

    public static ItemStack getEnchantBook(HashMap<CEnchantment, Integer> list) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta im = item.getItemMeta();
        List<String> lore = new ArrayList<String>();
        for (CEnchantment ce : list.keySet()) {
            lore.add(lorePrefix + ce.getDisplayName() + " " + intToLevel(list.get(ce)));
        }
        im.setLore(lore);
        try {
            im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        } catch (Throwable t) {}
        item.setItemMeta(im);
        item.addUnsafeEnchantment(glowEnchantment, 1);
        return item;
    }

    public static void setLorePrefix(String newPrefix) {
        lorePrefix = newPrefix;
    }

    public static void setMaxEnchants(int newMax) {
        maxEnchants = newMax;
    }

    public static void setEnchantBookName(String newName) {
        enchantBookName = newName;
    }
}
