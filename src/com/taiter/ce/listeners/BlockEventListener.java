package com.taiter.ce.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import com.taiter.ce.Main;
import com.taiter.ce.utils.Tools;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.EnchantManager;

public class BlockEventListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void BlockPlaceEvent(BlockPlaceEvent e) {
        CEventHandler.handleEvent(e.getPlayer(), e, Main.listener.blockPlaced);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void BlockBreakEvent(BlockBreakEvent e) {
        if (e.getBlock().hasMetadata("ce.Ice")) {
            e.setCancelled(true);
        }

        CEventHandler.handleEvent(e.getPlayer(), e, Main.listener.blockBroken);
        if (e.getBlock().hasMetadata("ce.mine")) {
            Block b = e.getBlock();
            b.removeMetadata("ce.mine", Main.plugin);
            Block[] blocks = { b.getRelative(0, 1, 0), b.getRelative(1, 0, 0), b.getRelative(-1, 0, 0), b.getRelative(0, 0, 1), b.getRelative(0, 0, -1) };

            for (Block block : blocks) {
                if (block.hasMetadata("ce.mine.secondary")) {
                    String[] s = block.getMetadata("ce.mine.secondary").get(0).asString().split(" ");
                    Location loc = new Location(e.getPlayer().getWorld(), Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
                    Location blockLoc = b.getLocation();
                    if (loc.getBlockX() == blockLoc.getBlockX() && loc.getBlockY() == blockLoc.getBlockY() && loc.getBlockZ() == blockLoc.getBlockZ()) {
                        block.removeMetadata("ce.mine.secondary", Main.plugin);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void BlockFromToEvent(BlockFromToEvent e) {
        if (e.getBlock().hasMetadata("ce.Ice")) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void SignChangeEvent(SignChangeEvent e) {
        if (e.getLine(0).equals("[CustomEnchant]")) {
            if (!e.getPlayer().isOp()) {
                e.setCancelled(true);
            } else {
                String ench = e.getLine(1);
                CEnchantment ce = EnchantManager.getEnchantment(ench);
                if (ce == null) {
                    ce = EnchantManager.getEnchantment(ench);
                }
                if (ce == null) {
                    for (CEnchantment ceT : EnchantManager.getEnchantments()) {
                        if (EnchantManager.containsEnchantment(ench, ceT)) {
                            ce = ceT;
                        }
                    }
                }
                if (ce == null) {
                    e.getPlayer().sendMessage(ChatColor.RED + "Could not find Custom Enchantment " + ench + ".");
                    e.setCancelled(true);
                    return;
                }
                if (Main.hasEconomy) {
                    try {
                        Integer.parseInt(e.getLine(3).replaceAll("\\D+", ""));
                    } catch (NumberFormatException ex) {
                        e.getPlayer().sendMessage(ChatColor.RED + "The cost you entered is invalid.");
                        e.setCancelled(true);
                        return;
                    }
                } else {
                    e.getPlayer().sendMessage(ChatColor.GRAY + "You are not using a compatible economy plugin, so the cost will not be used.");
                }
                e.getPlayer().sendMessage(ChatColor.GREEN + "Successfully created a sign shop for the enchantment " + ench + ".");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void EnchantItemEvent(EnchantItemEvent e) {
        if (e.getExpLevelCost() == 30) {
            if (Tools.random.nextInt(100) < (Float.parseFloat(Main.config.getString("Global.Enchantments.CEnchantingProbability")))) {
                CEventHandler.handleEnchanting(e);
            }
        }
    }
}
