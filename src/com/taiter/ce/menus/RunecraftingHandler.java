package com.taiter.ce.menus;

import com.taiter.ce.utils.Tools;
import com.taiter.ce.utils.Translator;
import com.taiter.ce.Main;
import com.taiter.ce.effects.EffectManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import com.taiter.ce.effects.EffectManager.ParticleEffect;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.EnchantManager;

public class RunecraftingHandler {

    private static final boolean stackEnchantments = Main.config.getBoolean("Global.Runecrafting.CanStackEnchantments");
    private static final boolean disenchanting = Main.config.getBoolean("Global.Runecrafting.Disenchanting");
    private static final boolean transform = Main.config.getBoolean("Global.Runecrafting.TransformationEffect");

    public static void handleRunecrafting(final InventoryClickEvent event) {
        if (event.getView() == null)
            return;
        if (event.getRawSlot() >= 0 && event.getRawSlot() < 3) {
            final Inventory inv = event.getView().getTopInventory();

            ItemStack item = event.getCursor();
            ItemStack current = event.getCurrentItem();

            event.setCancelled(true);

            if (event.getClick().isShiftClick()) {
                if (event.getSlot() != 2) {
                    if (event.getView().getBottomInventory().firstEmpty() != -1) {
                        event.getView().getBottomInventory().addItem(event.getCurrentItem());
                        event.setCurrentItem(new ItemStack(Material.AIR));
                        updateRunecraftingInventory(inv);
                    }
                }
                return;
            }

            switch (event.getSlot()) {
            case 0:
                if (item != null && item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                    event.getWhoClicked().setItemOnCursor(item.clone());
                    if (current != null && !current.getType().equals(Material.AIR))
                        event.getWhoClicked().getInventory().addItem(current);
                    item.setAmount(1);
                } else {
                    event.getWhoClicked().setItemOnCursor(current);
                }
                inv.setItem(0, item);
                updateRunecraftingInventory(inv);
                break;
            case 1:
                if (item != null && !item.getType().equals(Material.AIR) && !EnchantManager.isEnchantmentBook(item) && !EnchantManager.hasEnchantments(item)) {
                    event.setCancelled(true);
                    break;
                }

                inv.setItem(1, item);
                event.getWhoClicked().setItemOnCursor(current);
                updateRunecraftingInventory(inv);
                break;
            case 2:
                final ItemStack result = inv.getItem(2);
                if (result != null && !result.getType().equals(Material.AIR)) {
                    if (result.hasItemMeta() && result.getItemMeta().hasDisplayName()
                            && (result.getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "Transforming...")
                                    || result.getItemMeta().getDisplayName().equals(ChatColor.DARK_RED + "Incompatible Enchantment")
                                    || result.getItemMeta().getDisplayName().equals(Translator.get("Runecrafting.Transforming"))
                                    || result.getItemMeta().getDisplayName().equals(Translator.get("Runecrafting.Incompatible"))))
                        return;

                    final Player p = (Player) event.getWhoClicked();
                    ItemMeta im = result.getItemMeta();
                    List<String> lore = im.getLore();

                    String costPrefix = Translator.get("Commands.CostPrefix", "Cost: ");
                    String costPrefixFallback = ChatColor.GRAY + "Cost: ";

                    if (lore != null && !lore.isEmpty() && (lore.get(lore.size() - 1).startsWith(costPrefix) || lore.get(lore.size() - 1).startsWith(costPrefixFallback))) {
                        int levelCost = 0;
                        double moneyCost = 0;
                        String lastLoreLine = lore.get(lore.size() - 1);
                        String cleanCost = ChatColor.stripColor(lastLoreLine);

                        String costPrefixClean = ChatColor.stripColor(costPrefix).trim();
                        String costPrefixFallbackClean = "Cost:";

                        cleanCost = cleanCost.replace(costPrefixClean, "").replace(costPrefixFallbackClean, "").trim();

                        String[] costSplit = cleanCost.split(" ");
                        String resultString = "";

                        String levelsLabel = ChatColor.stripColor(Translator.get("Commands.Levels", " Levels ")).trim();
                        String levelsLabelFallback = "Levels";

                        if (cleanCost.contains(levelsLabel) || cleanCost.contains(levelsLabelFallback)) {
                            levelCost = Integer.parseInt(costSplit[0]);
                            resultString += ChatColor.WHITE + "" + ChatColor.BOLD + levelCost + " " + Translator.get("Commands.Levels", " Levels ");
                            if (costSplit.length >= 3) {
                                try {
                                    moneyCost = Double.parseDouble(costSplit[2]);
                                    resultString += ChatColor.GREEN + " " + Translator.get("Commands.And", "and") + " " + ChatColor.WHITE + ChatColor.BOLD + moneyCost + " " + (Main.hasEconomy ? Main.econ.currencyNamePlural() : "");
                                } catch (NumberFormatException ex) {
                                    // Ignorar se não for número
                                }
                            }
                        } else {
                            try {
                                moneyCost = Double.parseDouble(costSplit[0]);
                                resultString += ChatColor.WHITE + "" + ChatColor.BOLD + moneyCost + " " + (Main.hasEconomy ? Main.econ.currencyNamePlural() : "");
                            } catch (NumberFormatException ex) {
                                // Ignorar se não for número
                            }
                        }

                        if (!p.getGameMode().equals(GameMode.CREATIVE)) {
                            if (p.getLevel() < levelCost) {
                                p.sendMessage(Translator.get("Runecrafting.LevelNotHighEnough", ChatColor.RED + "Your level is not high enough!"));
                                return;
                            }
                            p.setLevel(p.getLevel() - levelCost);
                        }

                        if (moneyCost > 0 && Main.hasEconomy) {
                            if (Main.econ.getBalance(p.getName()) < moneyCost) {
                                p.sendMessage(Translator.get("Runecrafting.NotEnoughMoney", ChatColor.RED + "You do not have enough money!"));
                                return;
                            }
                            Main.econ.withdrawPlayer(p.getName(), moneyCost);
                        }

                        p.sendMessage(Translator.get("Runecrafting.Transformed", ChatColor.GREEN + "Used %item% for the transformation.").replace("%item%", resultString));

                        lore = lore.subList(0, lore.size() - 2);
                        im.setLore(lore);
                        result.setItemMeta(im);
                    }
                    EffectManager.playSound(p.getLocation(), "BLOCK_ANVIL_USE", 1f, 2f);
                    EffectManager.playSound(p.getLocation(), "ENTITY_FIREWORK_LAUNCH", 1f, 1.5f);

                    inv.clear();

                    if (!result.getType().equals(Material.ENCHANTED_BOOK) && transform) {
                        final ItemStack transformation = new ItemStack(Material.POTATO);
                        ItemMeta tim = transformation.getItemMeta();
                        tim.setDisplayName(Translator.get("Runecrafting.Transforming", ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "Transforming..."));
                        transformation.setItemMeta(tim);

                        final List<Player> targets = new ArrayList<>();
                        targets.add(p);
                        for (Entity e : p.getNearbyEntities(30, 30, 30))
                            if (e instanceof Player)
                                targets.add((Player) e);

                        new BukkitRunnable() {
                            int counter = 50;
                            Material[] mats = Material.values();

                            @Override
                            public void run() {
                                if (counter <= 0) {
                                    inv.setItem(2, new ItemStack(Material.AIR));
                                    String expectedTitle = ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "abc" + ChatColor.RESET + ChatColor.DARK_PURPLE + Translator.get("Runecrafting.Title", " Runecrafting ") + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "cba";
                                    String expectedTitleFallback = ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "abc" + ChatColor.RESET + ChatColor.DARK_PURPLE + " Runecrafting " + ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "cba";
                                    if (p.getOpenInventory() != null && (p.getOpenInventory().getTitle().equals(expectedTitle) || p.getOpenInventory().getTitle().equals(expectedTitleFallback)))
                                        p.setItemOnCursor(result);
                                    else
                                        p.getInventory().addItem(result);
                                    this.cancel();
                                    return;
                                }
                                EffectManager.sendEffect(targets, ParticleEffect.SPELL_MOB, p.getLocation(), new Vector(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1), 1, 100);
                                transformation.setType(mats[Tools.random.nextInt(mats.length - 1)]);
                                inv.setItem(2, transformation);
                                counter--;
                            }
                        }.runTaskTimer(Main.plugin, 0, 2);
                    } else {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                event.getWhoClicked().setItemOnCursor(result);
                            }
                        }.runTaskLater(Main.plugin, 1);
                    }
                }
                break;
            }

        } else {
            if (event.isShiftClick()) {
                event.setCancelled(true);
                Inventory top = event.getView().getTopInventory();
                ItemStack current = event.getCurrentItem().clone();
                ItemStack topItem = current.clone();
                topItem.setAmount(1);

                if (EnchantManager.isEnchantmentBook(current) || EnchantManager.hasEnchantments(current)) {
                    current.setAmount(current.getAmount() - 1);
                    if (current.getAmount() == 0)
                        current.setType(Material.AIR);
                    if (top.getItem(1) == null || top.getItem(1).getType().equals(Material.AIR)) {
                        top.setItem(1, topItem);
                        event.setCurrentItem(current);
                        updateRunecraftingInventory(top);
                    } else if (top.getItem(0) == null || top.getItem(0).getType().equals(Material.AIR)) {
                        top.setItem(0, topItem);
                        event.setCurrentItem(current);
                        updateRunecraftingInventory(top);
                    }
                } else if (EnchantManager.isEnchantable(current.getType().toString())) {
                    current.setAmount(current.getAmount() - 1);
                    if (current.getAmount() == 0)
                        current.setType(Material.AIR);
                    if ((top.getItem(0) == null || top.getItem(0).getType().equals(Material.AIR))) {
                        top.setItem(0, topItem);
                        event.setCurrentItem(current);
                        updateRunecraftingInventory(top);
                    }
                }
            }
        }
    }

    public static void updateRunecraftingInventory(final Inventory inv) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack top = inv.getItem(0);
                ItemStack bot = inv.getItem(1);

                if (top == null || bot == null || top.getType().equals(Material.AIR) || bot.getType().equals(Material.AIR)) {
                    inv.setItem(2, new ItemStack(Material.AIR));
                    return;
                }

                int levelCost = 0;
                double moneyCost = 0;

                if (disenchanting && top.getType().equals(Material.BOOK) && !bot.getType().equals(Material.ENCHANTED_BOOK)) {
                    HashMap<CEnchantment, Integer> enchs = EnchantManager.getEnchantmentLevels(bot.getItemMeta().getLore());

                    for (CEnchantment ce : enchs.keySet()) {
                        int lvl = ce.getRunecraftCostLevel(enchs.get(ce));
                        double money = ce.getRunecraftCostMoney(enchs.get(ce));

                        if (lvl > 0)
                            levelCost += lvl;
                        if (money > 0)
                            moneyCost += money;
                    }

                    ItemStack book = EnchantManager.getEnchantBook(enchs);
                    ItemMeta im = book.getItemMeta();
                    List<String> lore = im.getLore();

                    if (levelCost > 0 || moneyCost > 0) {
                        lore.add("");
                        lore.add(getCostString(levelCost, moneyCost));
                        im.setLore(lore);
                        book.setItemMeta(im);
                    }

                    inv.setItem(2, book);
                    return;
                }

                if (top.getType().equals(Material.BOOK)) {
                    return;
                }

                if (EnchantManager.isEnchantmentBook(bot)) {
                    ItemStack item = top.clone();
                    HashMap<CEnchantment, Integer> botList = EnchantManager.getEnchantmentLevels(bot.getItemMeta().getLore());
                    HashMap<CEnchantment, Integer> topList = EnchantManager.getEnchantmentLevels(top.getItemMeta().getLore());

                    if (stackEnchantments) {
                        if (EnchantManager.isEnchantmentBook(top)) {
                            for (CEnchantment ce : topList.keySet())
                                if (botList.containsKey(ce)) {
                                    int newLevel = botList.get(ce) + topList.get(ce);

                                    if (newLevel > ce.getEnchantmentMaxLevel())
                                        newLevel = ce.getEnchantmentMaxLevel();

                                    int lvl = ce.getRunecraftCostLevel((newLevel - botList.get(ce)));
                                    double money = ce.getRunecraftCostMoney((newLevel - botList.get(ce)));

                                    if (lvl > 0)
                                        levelCost += lvl;
                                    if (money > 0)
                                        moneyCost += money;

                                    botList.replace(ce, newLevel);
                                } else {
                                    if (botList.size() < EnchantManager.getMaxEnchants()) {
                                        int newLevel = topList.get(ce);

                                        int lvl = ce.getRunecraftCostLevel(newLevel);
                                        double money = ce.getRunecraftCostMoney(newLevel);

                                        if (lvl > 0)
                                            levelCost += lvl;
                                        if (money > 0)
                                            moneyCost += money;

                                        botList.put(ce, newLevel);
                                    } else
                                        break;
                                }
                            ItemStack book = EnchantManager.getEnchantBook(botList);
                            ItemMeta im = book.getItemMeta();
                            List<String> lore = im.getLore();

                            if (levelCost > 0 || moneyCost > 0) {
                                lore.add("");
                                lore.add(getCostString(levelCost, moneyCost));
                                im.setLore(lore);
                                book.setItemMeta(im);
                            }
                            inv.setItem(2, book);
                            return;
                        }
                    } else if (EnchantManager.hasEnchantments(top) || EnchantManager.isEnchantmentBook(top)) {
                        return;
                    }

                    for (CEnchantment ce : botList.keySet())
                        if (!topList.containsKey(ce) && Tools.isApplicable(item, ce)) {
                            int newLevel = botList.get(ce);

                            int lvl = ce.getRunecraftCostLevel(newLevel);
                            double money = ce.getRunecraftCostMoney(newLevel);

                            if (lvl > 0)
                                levelCost += lvl;
                            if (money > 0)
                                moneyCost += money;

                            item = EnchantManager.addEnchant(item, ce, newLevel);
                        }
                    if (EnchantManager.getEnchantments(item.getItemMeta().getLore()).size() > topList.size()) {
                        if (levelCost > 0 || moneyCost > 0) {
                            ItemMeta im = item.getItemMeta();
                            List<String> lore = im.getLore();
                            lore.add("");
                            lore.add(getCostString(levelCost, moneyCost));
                            im.setLore(lore);
                            item.setItemMeta(im);
                        }

                        inv.setItem(2, item);
                    } else {
                        ItemMeta im = item.getItemMeta();
                        im.setDisplayName(Translator.get("Runecrafting.Incompatible", ChatColor.DARK_RED + "Incompatible Enchantment"));
                        im.setLore(new ArrayList<String>());
                        item.setItemMeta(im);
                        item.setType(Material.BARRIER);
                        inv.setItem(2, item);
                    }
                    return;
                }
                inv.setItem(2, new ItemStack(Material.AIR));
            }
        }.runTaskLater(Main.plugin, 2);
    }

    private static String getCostString(int levelCost, double moneyCost) {
        String costPrefix = Translator.get("Commands.CostPrefix", ChatColor.GRAY + "Cost: ");
        String levelsLabel = Translator.get("Commands.Levels", " Levels ");
        String andLabel = Translator.get("Commands.And", "and");

        String costString = costPrefix + (levelCost > 0 ? ChatColor.WHITE + "" + ChatColor.BOLD + levelCost + ChatColor.GOLD + levelsLabel : "");
        if (Main.hasEconomy && moneyCost > 0) {
            String currency = (moneyCost == 1 ? Main.econ.currencyNameSingular() : Main.econ.currencyNamePlural());
            if (levelCost > 0) {
                costString += ChatColor.GREEN + " " + andLabel + " ";
            }
            costString += ChatColor.WHITE + "" + ChatColor.BOLD + moneyCost + " " + ChatColor.GOLD + currency;
        }
        return costString;
    }
}
