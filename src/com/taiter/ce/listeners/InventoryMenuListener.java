package com.taiter.ce.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import com.taiter.ce.Main;
import com.taiter.ce.menus.RunecraftingHandler;
import com.taiter.ce.utils.Tools;
import com.taiter.ce.utils.Translator;
import com.taiter.ce.menus.MenuManager;
import com.taiter.ce.menus.MenuPurchaseHandler;
import com.taiter.ce.Enchantments.EnchantManager;

public class InventoryMenuListener implements Listener {

    private final boolean useRuneCrafting = Main.plugin.getConfig().getBoolean("Global.Runecrafting.Enabled");

    @EventHandler(priority = EventPriority.HIGHEST)
    public void inventoryMenuPrevention(InventoryDragEvent event) {
        String title = event.getView().getTopInventory().getTitle();
        if (title.startsWith("CE") || title.startsWith(Tools.prefix)) {
            event.setCancelled(true);
        } else if (useRuneCrafting) {
            String invName = event.getView().getTopInventory().getName();
            String expected = ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "abc" + ChatColor.RESET + ChatColor.DARK_PURPLE + Translator.get("Runecrafting.Title", " Runecrafting ") + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "cba";
            String fallback = ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "abc" + ChatColor.RESET + ChatColor.DARK_PURPLE + " Runecrafting " + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "cba";
            if (invName.equals(expected) || invName.equals(fallback)) {
                RunecraftingHandler.updateRunecraftingInventory(event.getInventory());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void inventoryMenuPrevention(InventoryCreativeEvent event) {
        String title = event.getView().getTopInventory().getTitle();
        if (title.startsWith("CE") || title.startsWith(Tools.prefix)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void inventoryClose(InventoryCloseEvent event) {
        String invName = event.getInventory().getName();
        String expected = ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "abc" + ChatColor.RESET + ChatColor.DARK_PURPLE + Translator.get("Runecrafting.Title", " Runecrafting ") + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "cba";
        String fallback = ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "abc" + ChatColor.RESET + ChatColor.DARK_PURPLE + " Runecrafting " + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "cba";
        if (invName.equals(expected) || invName.equals(fallback)) {
            ItemStack[] contents = event.getInventory().getContents();
            HumanEntity p = event.getPlayer();
            org.bukkit.Location loc = p.getLocation().add(0, 1.25, 0);
            org.bukkit.util.Vector velocity = loc.getDirection().multiply(0.25);
            if (contents[0] != null && !contents[0].getType().equals(Material.AIR)) {
                if (p.getInventory().firstEmpty() == -1) {
                    p.getWorld().dropItem(loc, contents[0]).setVelocity(velocity);
                } else {
                    p.getInventory().addItem(contents[0]);
                }
            }
            if (contents[1] != null && !contents[1].getType().equals(Material.AIR)) {
                if (p.getInventory().firstEmpty() == -1) {
                    p.getWorld().dropItem(loc, contents[1]).setVelocity(velocity);
                } else {
                    p.getInventory().addItem(contents[1]);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void inventoryMenu(final InventoryClickEvent event) {
        if (event.getSlot() == -999 || event.getSlot() == -1) {
            return;
        }

        if (useRuneCrafting) {
            String invName = event.getView().getTopInventory().getName();
            String expected = ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "abc" + ChatColor.RESET + ChatColor.DARK_PURPLE + Translator.get("Runecrafting.Title", " Runecrafting ") + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "cba";
            String fallback = ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "abc" + ChatColor.RESET + ChatColor.DARK_PURPLE + " Runecrafting " + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "cba";
            if (invName.equals(expected) || invName.equals(fallback)) {
                RunecraftingHandler.handleRunecrafting(event);
                return;
            }
        }

        if (event.getView().getTopInventory().getType().equals(InventoryType.ANVIL)) {
            ItemStack toTest = event.getCurrentItem();
            if (!event.getClick().toString().contains("SHIFT")) {
                if (event.getRawSlot() <= 1) {
                    toTest = event.getCursor();
                } else {
                    return;
                }
            }
            if (toTest != null && !toTest.getType().equals(Material.AIR) && toTest.hasItemMeta()) {
                if (EnchantManager.isEnchantmentBook(toTest)) {
                    event.getWhoClicked().sendMessage(Translator.get("Menu.Messages.RepulsedAnvil", ChatColor.RED + "The book is being repulsed by the Anvil"));
                } else if (EnchantManager.hasEnchantments(toTest)) {
                    event.getWhoClicked().sendMessage(Translator.get("Menu.Messages.RepulsedAnvilItem", ChatColor.RED + "The item is being repulsed by the Anvil"));
                } else {
                    return;
                }
                event.setCancelled(true);
            }
            return;
        }

        if (event.getView().getTopInventory().getType().equals(InventoryType.CRAFTING) || event.getView().getTopInventory().getType().equals(InventoryType.CREATIVE)) {
            if (event.getSlotType() == InventoryType.SlotType.ARMOR && event.getClick() != ClickType.DOUBLE_CLICK) {
                CEventHandler.handleArmor((Player) event.getWhoClicked(), event.getCurrentItem(), true, event);
                CEventHandler.handleArmor((Player) event.getWhoClicked(), event.getCursor(), false, event);
                if (event.getCursor() == null) {
                    CEventHandler.handleArmor((Player) event.getWhoClicked(), event.getCurrentItem(), false, event);
                }
            } else if (event.getClick() == ClickType.SHIFT_LEFT) {
                ItemStack current = event.getCurrentItem();
                String typeS = current.getType().toString();
                PlayerInventory inv = event.getWhoClicked().getInventory();
                if ((typeS.endsWith("HELMET") && inv.getHelmet() == null) || (typeS.endsWith("CHESTPLATE") && inv.getChestplate() == null) || (typeS.endsWith("LEGGINGS") && inv.getLeggings() == null)
                        || (typeS.endsWith("BOOTS") && inv.getBoots() == null)) {
                    CEventHandler.handleArmor((Player) event.getWhoClicked(), event.getCurrentItem(), false, event);
                }
            }
        }

        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) {
            return;
        }

        String title = event.getView().getTopInventory().getTitle();
        if (title.startsWith("CE") || title.startsWith(Tools.prefix)) {
            Inventory topInv = event.getView().getTopInventory();
            final Player p = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            event.setCancelled(true);

            if (event.getRawSlot() == topInv.getSize() - 1) {
                p.closeInventory();
                p.openInventory(Tools.getPreviousInventory(topInv.getTitle()));
                return;
            }

            if (event.getRawSlot() < topInv.getSize()) {
                if (topInv.getTitle().equals(Tools.prefix + "Enchantments")
                        || topInv.getTitle().equals(Tools.prefix + Translator.get("Menu.Title.Enchantments", "Enchantments"))) {
                    p.closeInventory();
                    p.openInventory(Tools.getEnchantmentMenu(p, clickedItem.getItemMeta().getDisplayName()));
                    return;
                }

                if (topInv.getTitle().equals(Tools.prefix + "Main Menu")
                        || topInv.getTitle().equals(Tools.prefix + Translator.get("Menu.Title.MainMenu", "Main Menu"))) {
                    if (event.getRawSlot() == 4) {
                        p.closeInventory();
                        p.openInventory(Tools.getItemMenu(p));
                        return;
                    } else if (event.getRawSlot() == 6) {
                        if (p.hasPermission("ce.*") || p.hasPermission("ce.runecrafting")) {
                            p.closeInventory();
                            p.openInventory(Tools.getNextInventory(clickedItem.getItemMeta().getDisplayName()));
                            return;
                        } else {
                            p.sendMessage(Translator.get("Menu.Messages.NoPermissionUse", ChatColor.RED + "You do not have permission to use this!"));
                            return;
                        }
                    }
                }

                if (topInv.getTitle().equals(Tools.prefix + "Global") || topInv.getTitle().equals(Tools.prefix + Translator.get("Menu.Title.Global", "Global"))
                        || topInv.getTitle().equals(Tools.prefix + "Bow") || topInv.getTitle().equals(Tools.prefix + Translator.get("Menu.Title.Bow", "Bow"))
                        || topInv.getTitle().equals(Tools.prefix + "Armor") || topInv.getTitle().equals(Tools.prefix + Translator.get("Menu.Title.Armor", "Armor"))
                        || topInv.getTitle().equals(Tools.prefix + "Helmet") || topInv.getTitle().equals(Tools.prefix + Translator.get("Menu.Title.Helmet", "Helmet"))
                        || topInv.getTitle().equals(Tools.prefix + "Boots") || topInv.getTitle().equals(Tools.prefix + Translator.get("Menu.Title.Boots", "Boots"))
                        || topInv.getTitle().equals(Tools.prefix + "Tool") || topInv.getTitle().equals(Tools.prefix + Translator.get("Menu.Title.Tool", "Tool"))) {
                    if (p.isOp() || Tools.checkPermission(EnchantManager.getEnchantment(clickedItem.getItemMeta().getDisplayName()), p)) {
                        MenuManager.openLevelSelectionMenu(p, clickedItem);
                        return;
                    } else {
                        p.sendMessage(Translator.get("Menu.Messages.NoPermissionBuy", ChatColor.RED + "You do not have permission to buy this Enchantment."));
                        return;
                    }
                }

                if (topInv.getTitle().equals(Tools.prefix + "Items")
                        || topInv.getTitle().equals(Tools.prefix + Translator.get("Menu.Title.Items", "Items"))) {
                    MenuPurchaseHandler.handlePurchaseItem(p, clickedItem);
                    return;
                }

                if (topInv.getTitle().equals(Tools.prefix + "Level selection")
                        || topInv.getTitle().equals(Tools.prefix + Translator.get("Menu.Title.LevelSelection", "Level selection"))) {
                    MenuPurchaseHandler.handlePurchaseLevelSelection(p, clickedItem);
                    return;
                }
            }

            if (event.getRawSlot() < topInv.getSize()) {
                p.closeInventory();
                try {
                    p.openInventory(Tools.getNextInventory(clickedItem.getItemMeta().getDisplayName()));
                } catch (Exception e) {
                    p.sendMessage(Translator.get("Menu.Messages.NotImplemented", ChatColor.RED + "This feature is not yet implemented."));
                }
            }
        }
    }
}
