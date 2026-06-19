package com.taiter.ce.menus;

import com.taiter.ce.utils.Tools;
import com.taiter.ce.utils.Translator;
import com.taiter.ce.Main;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.taiter.ce.CItems.CItem;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.CEnchantment.Application;
import com.taiter.ce.Enchantments.EnchantManager;

import com.cryptomorin.xseries.XMaterial;

public class MenuManager {

    public static Inventory getPreviousInventory(String name) {
        if (name.equals(Tools.prefix + Translator.get("Menu.Title.Enchantments", "Enchantments")) || name.equals(Tools.prefix + "Enchantments")
                || name.equals(Tools.prefix + Translator.get("Menu.Title.Items", "Items")) || name.equals(Tools.prefix + "Items")
                || name.equals(Tools.prefix + Translator.get("Menu.Title.Config", "Config")) || name.equals(Tools.prefix + "Config"))
            return Main.CEMainMenu;
        else if (name.equals(Tools.prefix + Translator.get("Menu.Title.Enchanting", "Enchanting")) || name.equals(Tools.prefix + "Enchanting")
                || name.equals(Tools.prefix + Translator.get("Menu.Title.Armor", "Armor")) || name.equals(Tools.prefix + "Armor")
                || name.equals(Tools.prefix + Translator.get("Menu.Title.Bow", "Bow")) || name.equals(Tools.prefix + "Bow")
                || name.equals(Tools.prefix + Translator.get("Menu.Title.Tool", "Tool")) || name.equals(Tools.prefix + "Tool")
                || name.equals(Tools.prefix + Translator.get("Menu.Title.Global", "Global")) || name.equals(Tools.prefix + "Global")
                || name.equals(Tools.prefix + Translator.get("Menu.Title.Helmet", "Helmet")) || name.equals(Tools.prefix + "Helmet")
                || name.equals(Tools.prefix + Translator.get("Menu.Title.Boots", "Boots")) || name.equals(Tools.prefix + "Boots")
                || name.equals(Tools.prefix + Translator.get("Menu.Title.LevelSelection", "Level selection")) || name.equals(Tools.prefix + "Level selection"))
            return Main.CEEnchantmentMainMenu;
        return null;
    }

    public static Inventory getNextInventory(String name) {
        name = ChatColor.stripColor(name);
        if (name.equals(ChatColor.stripColor(Translator.get("Menu.Title.Enchantments", "Enchantments"))) || name.equals("Enchantments"))
            return Main.CEEnchantmentMainMenu;
        else if (name.equals(ChatColor.stripColor(Translator.get("Menu.Title.Items", "Items"))) || name.equals("Items"))
            return Main.CEItemMenu;
        else if (name.equals(ChatColor.stripColor(Translator.get("Menu.Title.Runecrafting", "Runecrafting"))) || name.equals("Runecrafting")) {
            Inventory einv = Bukkit.createInventory(null, InventoryType.FURNACE,
                    ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "abc" + ChatColor.RESET + ChatColor.DARK_PURPLE + Translator.get("Runecrafting.Title", " Runecrafting ") + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "cba");
            return einv;
        } else if (name.equals(ChatColor.stripColor(Translator.get("Menu.Title.Global", "Global"))) || name.equals("Global"))
            return Main.CEGlobalMenu;
        else if (name.equals(ChatColor.stripColor(Translator.get("Menu.Title.Bow", "Bow"))) || name.equals("Bow"))
            return Main.CEBowMenu;
        else if (name.equals(ChatColor.stripColor(Translator.get("Menu.Title.Helmet", "Helmet"))) || name.equals("Helmet"))
            return Main.CEHelmetMenu;
        else if (name.equals(ChatColor.stripColor(Translator.get("Menu.Title.Boots", "Boots"))) || name.equals("Boots"))
            return Main.CEBootsMenu;
        else if (name.equals(ChatColor.stripColor(Translator.get("Menu.Title.Armor", "Armor"))) || name.equals("Armor"))
            return Main.CEArmorMenu;
        else if (name.equals(ChatColor.stripColor(Translator.get("Menu.Title.Tool", "Tool"))) || name.equals("Tool"))
            return Main.CEToolMenu;
        return null;
    }

    public static Inventory getEnchantmentMenu(Player p, String name) {
        if (!p.isOp() && !p.hasPermission("ce.ench.*")) {
            Inventory lInv = getNextInventory(name);
            Inventory enchantments = Bukkit.createInventory(null, lInv.getSize(), lInv.getTitle());
            enchantments.setContents(lInv.getContents());
            for (int i = 0; i < enchantments.getSize() - 2; i++) {
                ItemStack checkItem = enchantments.getItem(i);
                if (checkItem == null || checkItem.getType().equals(Material.AIR))
                    continue;
                ItemStack item = enchantments.getItem(i);
                ItemMeta im = item.getItemMeta();
                List<String> lore = new ArrayList<>();
                if (im.hasLore())
                    lore = im.getLore();
                for (CEnchantment ce : EnchantManager.getEnchantments()) {
                    if (im.getDisplayName().equals(ce.getDisplayName()))
                        if (!Tools.checkPermission(ce, p)) {
                            lore.add(Translator.get("Menu.Messages.NotPermittedUseThis", ChatColor.RED + "You are not permitted to use this"));
                            break;
                        }
                }
                im.setLore(lore);
                item.setItemMeta(im);
                enchantments.setItem(i, item);
            }
            return enchantments;
        }

        return getNextInventory(name);
    }

    public static Inventory getItemMenu(Player p) {
        if (!p.isOp() && !p.hasPermission("ce.item.*")) {
            Inventory lInv = Main.CEItemMenu;
            Inventory items = Bukkit.createInventory(null, lInv.getSize(), lInv.getTitle());
            items.setContents(lInv.getContents());
            for (int i = 0; i < items.getSize() - 2; i++) {
                ItemStack item = items.getItem(i);
                if (item == null || item.getType().equals(Material.AIR))
                    continue;
                ItemMeta im = item.getItemMeta();
                List<String> lore = new ArrayList<>();
                if (im.hasLore())
                    lore = im.getLore();
                for (CItem ci : Main.items)
                    if (item.getItemMeta().getDisplayName().equals(ci.getDisplayName())) {
                        if (!Tools.checkPermission(ci, p)) {
                            lore.add(Translator.get("Menu.Messages.NotPermittedUseThis", ChatColor.RED + "You are not permitted to use this"));
                            break;
                        }
                    }
                im.setLore(lore);
                item.setItemMeta(im);
            }
            return items;
        }

        return Main.CEItemMenu;
    }

    public static void generateInventories() {
        ItemStack backButton = XMaterial.NETHER_STAR.parseItem();
        ItemMeta tempMeta = backButton.getItemMeta();
        List<String> tempLore = new ArrayList<>();

        tempMeta.setDisplayName(Translator.get("Menu.Buttons.Back", ChatColor.AQUA + "Back"));
        backButton.setItemMeta(tempMeta);

        String itemPrefix = ChatColor.AQUA + "" + ChatColor.BOLD;

        Inventory MainMenu = Bukkit.createInventory(null, 9, Tools.prefix + Translator.get("Menu.Title.MainMenu", "Main Menu"));
        ItemStack Enchantments = XMaterial.ENCHANTED_BOOK.parseItem();
        ItemStack Items = XMaterial.END_PORTAL_FRAME.parseItem();
        ItemStack Runecrafting = XMaterial.ENCHANTING_TABLE.parseItem();

        tempMeta.setDisplayName(itemPrefix + Translator.get("Menu.Title.Enchantments", "Enchantments"));
        tempMeta.setLore(Translator.getStringList("Menu.Buttons.EnchantmentsLore"));
        Enchantments.setItemMeta(tempMeta);

        tempMeta.setDisplayName(itemPrefix + Translator.get("Menu.Title.Items", "Items"));
        tempMeta.setLore(Translator.getStringList("Menu.Buttons.ItemsLore"));
        Items.setItemMeta(tempMeta);

        tempMeta.setDisplayName(itemPrefix + Translator.get("Menu.Title.Runecrafting", "Runecrafting"));
        tempMeta.setLore(Translator.getStringList("Menu.Buttons.RunecraftingLore"));
        Runecrafting.setItemMeta(tempMeta);

        MainMenu.setItem(2, Enchantments);
        MainMenu.setItem(4, Items);
        if (Main.config.getBoolean("Global.Runecrafting.Enabled"))
            MainMenu.setItem(6, Runecrafting);

        Main.CEMainMenu = MainMenu;

        Inventory EnchantmentMenu = Bukkit.createInventory(null, 9, Tools.prefix + Translator.get("Menu.Title.Enchantments", "Enchantments"));
        EnchantmentMenu.setItem(8, backButton);
        ItemStack Global = XMaterial.ENCHANTED_BOOK.parseItem();
        ItemStack Bow = XMaterial.BOW.parseItem();
        ItemStack Armor = XMaterial.ANVIL.parseItem();
        ItemStack Tool = XMaterial.SHEARS.parseItem();
        ItemStack Helmet = XMaterial.DIAMOND_HELMET.parseItem();
        ItemStack Boots = XMaterial.DIAMOND_BOOTS.parseItem();

        tempMeta.setDisplayName(itemPrefix + Translator.get("Menu.Title.Global", "Global"));
        tempMeta.setLore(null);
        Global.setItemMeta(tempMeta);

        tempMeta.setDisplayName(itemPrefix + Translator.get("Menu.Title.Bow", "Bow"));
        Bow.setItemMeta(tempMeta);

        tempMeta.setDisplayName(itemPrefix + Translator.get("Menu.Title.Armor", "Armor"));
        Armor.setItemMeta(tempMeta);

        tempMeta.setDisplayName(itemPrefix + Translator.get("Menu.Title.Tool", "Tool"));
        Tool.setItemMeta(tempMeta);

        tempMeta.setDisplayName(itemPrefix + Translator.get("Menu.Title.Helmet", "Helmet"));
        Helmet.setItemMeta(tempMeta);

        tempMeta.setDisplayName(itemPrefix + Translator.get("Menu.Title.Boots", "Boots"));
        Boots.setItemMeta(tempMeta);

        EnchantmentMenu.setItem(1, Global);
        EnchantmentMenu.setItem(2, Bow);
        EnchantmentMenu.setItem(3, Armor);
        EnchantmentMenu.setItem(4, Tool);
        EnchantmentMenu.setItem(5, Helmet);
        EnchantmentMenu.setItem(6, Boots);

        Main.CEEnchantmentMainMenu = EnchantmentMenu;

        Inventory ArmorMenu = Bukkit.createInventory(null, 36, Tools.prefix + Translator.get("Menu.Title.Armor", "Armor"));
        ArmorMenu.setItem(35, backButton);

        int current = 0;
        ItemStack tempItem = XMaterial.ENCHANTED_BOOK.parseItem();
        for (CEnchantment ce : EnchantManager.getEnchantments())
            if (ce.getApplication() == Application.ARMOR) {
                tempMeta.setDisplayName(ce.getDisplayName());
                tempItem.setItemMeta(tempMeta);
                ArmorMenu.setItem(current, tempItem);
                tempLore.clear();
                tempMeta.setLore(tempLore);
                current++;
            }
        Main.CEArmorMenu = ArmorMenu;

        Inventory GlobalMenu = Bukkit.createInventory(null, 36, Tools.prefix + Translator.get("Menu.Title.Global", "Global"));
        GlobalMenu.setItem(35, backButton);

        current = 0;
        for (CEnchantment ce : EnchantManager.getEnchantments())
            if (ce.getApplication() == Application.GLOBAL) {
                tempMeta.setDisplayName(ce.getDisplayName());
                tempItem.setItemMeta(tempMeta);
                GlobalMenu.setItem(current, tempItem);
                tempLore.clear();
                tempMeta.setLore(tempLore);
                current++;
            }
        Main.CEGlobalMenu = GlobalMenu;

        Inventory ToolMenu = Bukkit.createInventory(null, 36, Tools.prefix + Translator.get("Menu.Title.Tool", "Tool"));
        ToolMenu.setItem(35, backButton);

        current = 0;
        for (CEnchantment ce : EnchantManager.getEnchantments())
            if (ce.getApplication() == Application.TOOL) {
                tempMeta.setDisplayName(ce.getDisplayName());
                tempItem.setItemMeta(tempMeta);
                ToolMenu.setItem(current, tempItem);
                tempLore.clear();
                tempMeta.setLore(tempLore);
                current++;
            }
        Main.CEToolMenu = ToolMenu;

        Inventory BowMenu = Bukkit.createInventory(null, 36, Tools.prefix + Translator.get("Menu.Title.Bow", "Bow"));
        BowMenu.setItem(35, backButton);

        current = 0;
        for (CEnchantment ce : EnchantManager.getEnchantments()) {
            if (ce.getApplication() == Application.BOW) {
                tempMeta.setDisplayName(ce.getDisplayName());
                tempItem.setItemMeta(tempMeta);
                BowMenu.setItem(current, tempItem);
                tempLore.clear();
                tempMeta.setLore(tempLore);
                current++;
            }
        }
        Main.CEBowMenu = BowMenu;

        Inventory HelmetMenu = Bukkit.createInventory(null, 36, Tools.prefix + Translator.get("Menu.Title.Helmet", "Helmet"));
        HelmetMenu.setItem(35, backButton);

        current = 0;
        for (CEnchantment ce : EnchantManager.getEnchantments())
            if (ce.getApplication() == Application.HELMET) {
                tempMeta.setDisplayName(ce.getDisplayName());
                tempItem.setItemMeta(tempMeta);
                HelmetMenu.setItem(current, tempItem);
                tempLore.clear();
                tempMeta.setLore(tempLore);
                current++;
            }
        Main.CEHelmetMenu = HelmetMenu;

        Inventory BootsMenu = Bukkit.createInventory(null, 36, Tools.prefix + Translator.get("Menu.Title.Boots", "Boots"));
        BootsMenu.setItem(35, backButton);

        current = 0;
        for (CEnchantment ce : EnchantManager.getEnchantments())
            if (ce.getApplication() == Application.BOOTS) {
                tempMeta.setDisplayName(ce.getDisplayName());
                tempItem.setItemMeta(tempMeta);
                BootsMenu.setItem(current, tempItem);
                tempLore.clear();
                tempMeta.setLore(tempLore);
                current++;
            }
        Main.CEBootsMenu = BootsMenu;

        Inventory ItemMenu = Bukkit.createInventory(null, 36, Tools.prefix + Translator.get("Menu.Title.Items", "Items"));
        ItemMenu.setItem(35, backButton);

        int currentItemSlot = 0;
        for (CItem ci : Main.items) {
            ItemStack newItem = new ItemStack(ci.getMaterial());
            tempMeta.setDisplayName(ci.getDisplayName());
            List<String> temp = ci.getDescription();

            if (Main.hasEconomy && ci.getCost() > 0)
                temp.add(Translator.get("Commands.CostPerLevel", ChatColor.GRAY + "Cost: " + ChatColor.WHITE + "%cost%").replace("%cost%", String.valueOf(ci.getCost())));
            tempMeta.setLore(temp);
            newItem.setItemMeta(tempMeta);
            ItemMenu.setItem(currentItemSlot, newItem);
            currentItemSlot++;
        }
        Main.CEItemMenu = ItemMenu;
    }

    public static void openLevelSelectionMenu(Player p, ItemStack clickedItem) {
        Inventory levelMenu = Bukkit.createInventory(p, 9, Tools.prefix + Translator.get("Menu.Title.LevelSelection", "Level selection"));
        ItemStack backButton = XMaterial.NETHER_STAR.parseItem();
        ItemMeta tempMeta = backButton.getItemMeta();
        List<String> tempLore = new ArrayList<>();

        tempMeta.setDisplayName(Translator.get("Menu.Buttons.Back", ChatColor.AQUA + "Back"));
        backButton.setItemMeta(tempMeta);
        levelMenu.setItem(8, backButton);

        tempMeta = clickedItem.getItemMeta();
        String enchName = tempMeta.getDisplayName();
        CEnchantment ce = EnchantManager.getEnchantment(enchName + " I");

        for (int i = 1; i <= ce.getEnchantmentMaxLevel(); i++) {
            if (i > 5)
                break;

            double cost = ce.getCost(i);
            tempLore.clear();
            if (Main.hasEconomy && cost > 0) {
                tempLore.add("");
                tempLore.add(Translator.get("Commands.CostPerLevel", ChatColor.GRAY + "Cost: " + ChatColor.WHITE + ChatColor.BOLD + "%cost%").replace("%cost%", String.valueOf(cost)));
            }

            ItemStack newItem = clickedItem.clone();
            String fullName = enchName + " " + EnchantManager.intToLevel(i);
            tempMeta.setDisplayName(fullName);
            tempMeta.setLore(tempLore);
            newItem.setItemMeta(tempMeta);
            levelMenu.setItem(i - 1, newItem);
        }
        p.closeInventory();
        p.openInventory(levelMenu);
    }
}
