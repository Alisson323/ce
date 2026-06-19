package com.taiter.ce.listeners;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import net.milkbowl.vault.economy.EconomyResponse;
import com.taiter.ce.listeners.CEventHandler;
import com.taiter.ce.Main;
import com.taiter.ce.utils.Tools;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.EnchantManager;
import com.taiter.ce.CItems.CItem;

public class PlayerEventListener implements Listener {

    private final boolean useRuneCrafting = Main.plugin.getConfig().getBoolean("Global.Runecrafting.Enabled");

    @EventHandler
    public void PlayerPickupItemEvent(PlayerPickupItemEvent event) {
        if (event.getItem().hasMetadata("ce.Volley")) {
            event.getItem().remove();
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerInteractEvent(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        CEventHandler.handleEvent(p, e, Main.listener.interact);

        if (e.getAction().toString().startsWith("LEFT")) {
            CEventHandler.handleEvent(p, e, Main.listener.interactL);
        } else if (e.getAction().toString().startsWith("RIGHT")) {
            CEventHandler.handleEvent(p, e, Main.listener.interactR);

            if (useRuneCrafting) {
                if (e.getClickedBlock() != null && e.getClickedBlock().getType().equals(Material.ANVIL)) {
                    ItemStack i = p.getItemInHand();
                    if (EnchantManager.hasEnchantments(i) || EnchantManager.isEnchantmentBook(i)) {
                        if (!p.hasPermission("ce.*") && !p.hasPermission("ce.runecrafting")) {
                            return;
                        }
                        e.setCancelled(true);
                        p.setItemInHand(new ItemStack(Material.AIR));
                        Inventory einv = Bukkit.createInventory(p, org.bukkit.event.inventory.InventoryType.FURNACE, ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "abc" + ChatColor.RESET + ChatColor.DARK_PURPLE
                                + " Runecrafting " + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "cba");
                        einv.setContents(new ItemStack[]{new ItemStack(Material.AIR), i, new ItemStack(Material.AIR)});
                        p.openInventory(einv);
                        return;
                    }
                }
            }

            if (p.getItemInHand().getType() != Material.AIR) {
                ItemStack i = p.getItemInHand();
                String mat = i.getType().toString();
                PlayerInventory inv = p.getInventory();
                if ((mat.endsWith("BOOTS") && inv.getBoots() == null) || (mat.endsWith("LEGGINGS") && inv.getLeggings() == null) || (mat.endsWith("CHESTPLATE") && inv.getChestplate() == null)
                        || (mat.endsWith("HELMET") && inv.getHelmet() == null)) {
                    CEventHandler.handleArmor(p, e.getItem(), false, e);
                }
            }
        }

        if (e.getClickedBlock() != null && e.getClickedBlock().getType().toString().contains("SIGN")) {
            if (((Sign) e.getClickedBlock().getState()).getLine(0).equals("[CustomEnchant]")) {
                if (Main.hasEconomy) {
                    if (p.getItemInHand().getType() != Material.AIR) {
                        Sign sign = ((Sign) e.getClickedBlock().getState());
                        CEnchantment ce = EnchantManager.getEnchantment(sign.getLine(1));
                        if (ce == null) {
                            ce = EnchantManager.getEnchantment(sign.getLine(1));
                        }
                        if (ce == null) {
                            for (CEnchantment ceT : EnchantManager.getEnchantments()) {
                                if (EnchantManager.containsEnchantment(sign.getLine(1), ceT)) {
                                    ce = ceT;
                                }
                            }
                        }
                        if (ce == null) {
                            return;
                        }

                        ItemStack inHand = p.getItemInHand();
                        if (!Tools.isApplicable(inHand, ce)) {
                            p.sendMessage(ChatColor.RED + "This enchantment can not be applied to this item.");
                            return;
                        }

                        int cost = 0;
                        try {
                            cost = Integer.parseInt(sign.getLine(3).replaceAll("\\D+", ""));
                        } catch (NumberFormatException ex) {
                            return;
                        }

                        List<String> lore = new ArrayList<>();
                        ItemMeta im = inHand.getItemMeta();

                        if (inHand.getItemMeta().hasLore()) {
                            lore = inHand.getItemMeta().getLore();

                            if (EnchantManager.getEnchantments(lore).size() == EnchantManager.getMaxEnchants()) {
                                p.sendMessage(ChatColor.RED + "You already have the maximum amount of enchantments!");
                                return;
                            }

                            for (int i = 0; i < lore.size(); i++) {
                                if (EnchantManager.containsEnchantment(lore.get(i), ce)) {
                                    int newLevel = EnchantManager.getLevel(lore.get(i)) + 1;
                                    if (newLevel <= ce.getEnchantmentMaxLevel()) {
                                        if (Main.econ.getBalance(p.getName()) >= cost) {
                                            EconomyResponse ecr = Main.econ.withdrawPlayer(p.getName(), cost);
                                            if (ecr.transactionSuccess()) {
                                                p.sendMessage(ChatColor.GREEN + "Upgraded enchantment " + ce.getDisplayName() + ChatColor.GREEN + " for " + ChatColor.WHITE + cost + " "
                                                        + ((cost == 1) ? Main.econ.currencyNameSingular() : Main.econ.currencyNamePlural()) + ChatColor.GREEN + ".");
                                            } else {
                                                p.sendMessage(ChatColor.RED + "An economy error has occured:");
                                                p.sendMessage(ChatColor.RED + ecr.errorMessage);
                                                return;
                                            }
                                        } else {
                                            p.sendMessage(ChatColor.RED + "You do not have enough money to buy this!");
                                            return;
                                        }
                                        lore.set(i, ce.getDisplayName() + " " + EnchantManager.intToLevel(newLevel));
                                        im.setLore(lore);
                                        inHand.setItemMeta(im);
                                        return;
                                    } else {
                                        p.sendMessage(ChatColor.RED + "You already have the maximum level of this enchantment!");
                                        return;
                                    }
                                }
                            }
                        }

                        if (Main.econ.getBalance(p.getName()) >= cost) {
                            EconomyResponse ecr = Main.econ.withdrawPlayer(p.getName(), cost);
                            if (ecr.transactionSuccess()) {
                                p.sendMessage(ChatColor.GREEN + "Bought enchantment " + ce.getDisplayName() + ChatColor.GREEN + " for " + ChatColor.WHITE + cost + " "
                                        + ((cost == 1) ? Main.econ.currencyNameSingular() : Main.econ.currencyNamePlural()) + ChatColor.GREEN + ".");
                            } else {
                                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "An economy error has occured:");
                                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + ecr.errorMessage);
                                return;
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "You do not have enough money to buy this!");
                            return;
                        }

                        lore.add(ce.getDisplayName() + " I");
                        im.setLore(lore);
                        inHand.setItemMeta(im);
                        if (!inHand.containsEnchantment(EnchantManager.getGlowEnchantment())) {
                            inHand.addUnsafeEnchantment(EnchantManager.getGlowEnchantment(), 0);
                        }
                        return;
                    } else {
                        p.sendMessage(ChatColor.RED + "You do not have an item in your hand.");
                        return;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PlayerInteractEntityEvent(PlayerInteractEntityEvent e) {
        CEventHandler.handleEvent(e.getPlayer(), e, Main.listener.interactE);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PlayerDeathEvent(PlayerDeathEvent e) {
        CEventHandler.handleEvent(e.getEntity(), e, Main.listener.death);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PlayerMoveEvent(PlayerMoveEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();

        if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
            CEventHandler.handleEvent(e.getPlayer(), e, Main.listener.move);
            CEventHandler.handleMines(e.getPlayer(), e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PlayerItemBreakEvent(PlayerItemBreakEvent e) {
        for (ItemStack i : e.getPlayer().getInventory().getArmorContents()) {
            if (i != null && i.getType() != Material.AIR) {
                if (i.getAmount() == 0) {
                    CEventHandler.handleArmor(e.getPlayer(), e.getBrokenItem(), true, e);
                }
            }
        }
    }
}
