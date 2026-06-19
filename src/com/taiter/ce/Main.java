package com.taiter.ce;

import com.taiter.ce.CItems.tools.HealingShovel;
import com.taiter.ce.CItems.utilities.PoisonIvy;
import com.taiter.ce.CItems.utilities.FireworkBattery;
import com.taiter.ce.CItems.weapons.NecromancersStaff;
import com.taiter.ce.CItems.boots.RocketBoots;
import com.taiter.ce.CItems.CItem;
import com.taiter.ce.CItems.utilities.Powergloves;
import com.taiter.ce.CItems.traps.PiranhaTrap;
import com.taiter.ce.CItems.weapons.Pyroaxe;
import com.taiter.ce.CItems.weapons.Deathscythe;
import com.taiter.ce.CItems.boots.HermesBoots;
import com.taiter.ce.CItems.utilities.Medikit;
import com.taiter.ce.CItems.boots.DruidBoots;
import com.taiter.ce.CItems.bows.BeastmastersBow;
import com.taiter.ce.CItems.boots.LivefireBoots;
import com.taiter.ce.CItems.bows.HookshotBow;
import com.taiter.ce.CItems.traps.BearTrap;
import com.taiter.ce.CItems.utilities.PotionLauncher;
import com.taiter.ce.CItems.weapons.AssassinsBlade;
import com.taiter.ce.CItems.bows.Minigun;
import com.taiter.ce.CItems.traps.Landmine;
import com.taiter.ce.CItems.utilities.Flamethrower;
import com.taiter.ce.CItems.traps.PricklyBlock;
import com.taiter.ce.CItems.utilities.Bandage;
import com.taiter.ce.CItems.utilities.Firecracker;
import com.taiter.ce.CItems.weapons.ThorsAxe;
import com.taiter.ce.utils.Tools;
import com.taiter.ce.hooks.Updater;
import com.taiter.ce.listeners.CEListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.milkbowl.vault.economy.Economy;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Enchantments.CEnchantment.Application;
import com.taiter.ce.Enchantments.EnchantManager;
import com.taiter.ce.Enchantments.Global.IceAspect;
import com.taiter.ce.commands.CeCommand;
import com.taiter.ce.listeners.*;
import com.taiter.ce.utils.Translator;

public final class Main extends JavaPlugin {

    public static Plugin plugin;
    public static FileConfiguration config;
    public static CEListener listener;
    public static CeCommand commandC;
    private static ClassLoader classLoader;

    public static Set<CItem> items;
    public static Boolean createExplosions;
    public static Boolean hasRPGItems = false;

    public static Inventory CEMainMenu;
    public static Inventory CEEnchantmentMainMenu;
    public static Inventory CEItemMenu;
    public static Inventory CEConfigMenu;
    public static Inventory CEArmorMenu;
    public static Inventory CEBootsMenu;
    public static Inventory CEBowMenu;
    public static Inventory CEGlobalMenu;
    public static Inventory CEHelmetMenu;
    public static Inventory CEToolMenu;

    public static Economy econ = null;
    public static Boolean hasEconomy = false;
    public static Plugin econPl;

    public boolean hasUpdate = false;
    public boolean hasChecked = false;
    public Updater updater;

    @SuppressWarnings("deprecation")
    @Override
    public void onEnable() {
        plugin = this;
        commandC = new CeCommand(this);
        classLoader = this.getClassLoader();
        items = new HashSet<>();
        updater = new Updater(this);

        this.saveDefaultConfig();
        config = this.getConfig();
        config.options().copyDefaults(true);
        this.saveConfig();
        Translator.init(this);
        Tools.prefix = Translator.get("Menu.Prefix", "CE - ");
        if (config.contains("enchantments"))
            Tools.convertOldConfig();

        initializeListener();

        try {
            createExplosions = Boolean.parseBoolean(Main.config.getString("Global.CreateExplosions"));
            EnchantManager.setMaxEnchants(Integer.parseInt(config.getString("Global.Enchantments.MaximumCustomEnchantments")));
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] Config error, please check the values of");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] Global.CreateExplosions (Has to be true or false) and");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] Global.Enchantments.MaximumCustomEnchantments (Has to be a number)");
        }

        EnchantManager.setLorePrefix(resolveEnchantmentColor());
        EnchantManager.setEnchantBookName(ChatColor.translateAlternateColorCodes('&', Main.config.getString("Global.Books.Name")));

        if (setupEconomy()) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] Vault has been detected!");
            hasEconomy = true;
        }

        if (Main.getWorldGuard() != null)
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] WorldGuard has been detected!");

        if (this.getServer().getPluginManager().getPlugin("RPG_Items") != null)
            hasRPGItems = true;

        makeLists(true, true);

        if (EnchantManager.getEnchantments().size() == 0)
            return;

        try {
            com.taiter.ce.utils.PermissionHelper.writePermissions();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        Tools.generateInventories();

        if (Boolean.parseBoolean(config.getString("Global.Updates.CheckOnStartup"))) {
            this.getServer().getScheduler().scheduleAsyncDelayedTask(this, new BukkitRunnable() {
                @Override
                public void run() {
                    if (!hasChecked)
                        updateCheck();
                }
            }, Integer.parseInt(config.getString("Global.Updates.CheckDelay")));
        }
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(plugin);
        for (CEnchantment c : EnchantManager.getEnchantments())
            if (c instanceof IceAspect)
                for (HashMap<org.bukkit.block.Block, String> list : ((IceAspect) c).IceLists)
                    ((IceAspect) c).deleteIce(list);
    }

    public void updateCheck() {
        updater.updateCheck();
    }

    public void update() {
        updater.update();
    }

    public static WorldGuardPlugin getWorldGuard() {
        Plugin worldguard = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        if (worldguard != null && worldguard instanceof WorldGuardPlugin && worldguard.isEnabled())
            return (WorldGuardPlugin) worldguard;
        return null;
    }

    public void initializeListener() {
        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }

        listener = new CEListener();

        getServer().getPluginManager().registerEvents(new InventoryMenuListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerEventListener(), this);
        getServer().getPluginManager().registerEvents(new EntityEventListener(), this);
        getServer().getPluginManager().registerEvents(new BlockEventListener(), this);
    }

    public static String resolveEnchantmentColor() {
        String color = Main.plugin.getConfig().getString("Global.Enchantments.CEnchantmentColor");
        if (color.contains(";")) {
            String[] temp = color.split(";");
            color = "";
            for (String c : temp)
                try {
                    color += ChatColor.valueOf(c.toUpperCase());
                } catch (Exception e) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[CE] ERROR: The ChatColor '" + c
                            + "' was not found, please check the list of Bukkit ChatColors and update the ChatColor Section. The ChatColor will be ignored to ensure that CE is still working.");
                }
        } else {
            try {
                color = ChatColor.valueOf(Main.config.getString("Global.Enchantments.CEnchantmentColor").toUpperCase()).toString();
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[CE] ERROR: The ChatColor '" + color
                        + "' was not found, please check the list of Bukkit ChatColors and update the ChatColor Section. The ChatColor will be ignored to ensure that CE is still working.");
            }
        }
        return color;
    }



    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;
        econ = rsp.getProvider();
        return econ != null;
    }

    public static void makeLists(boolean finalize, boolean printSuccess) {
        long time = System.currentTimeMillis();
        try {
            String path = plugin.getDataFolder().getAbsolutePath();
            String classSource = Bukkit.getPluginManager().getPlugin("CustomEnchantments").getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
            String seperator = classSource.contains("/") ? "/" : "\\\\";
            String[] classSourceSplit = classSource.split(seperator);
            path = path.substring(0, path.length() - 18) + classSourceSplit[classSourceSplit.length - 1].replace("%20", " ");
            java.util.jar.JarFile jar = new java.util.jar.JarFile(path);
            java.util.Enumeration<java.util.jar.JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                String entryName = entries.nextElement().getName();
                if (!entryName.contains("$") && entryName.contains("Enchantments") && entryName.endsWith(".class")
                        && !(entryName.contains("CEnchantment") || entryName.contains("EnchantManager") || entryName.contains("GlowEnchantment")))
                    try {
                        Application app = null;
                        String className = entryName.replace(".class", "");
                        if (entryName.contains("/")) {
                            app = Application.valueOf(entryName.split("/")[4].toUpperCase());
                            className = className.replaceAll("/", ".");
                        } else if (entryName.contains("\\")) {
                            app = Application.valueOf(entryName.split("\\\\")[4].toUpperCase());
                            className = className.replaceAll("\\\\", ".");
                        }
                        EnchantManager.getEnchantments().add((CEnchantment) classLoader.loadClass(className).getDeclaredConstructor(Application.class).newInstance(app));
                    } catch (ClassNotFoundException e) {
                    }
            }
            jar.close();
        } catch (Exception e) {
            if (e instanceof java.io.FileNotFoundException) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] Custom Enchantments could not be started,");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] please make sure that you the plugins jar");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] in your plugin folder is named 'CustomEnchantments'.");
            } else {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] Custom Enchantments could not be loaded,");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] please report this error on the Bukkit page of the plugin");
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] by sending the following to Taiterio via PM:");
                e.printStackTrace();
            }
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        if (finalize)
            for (CEnchantment ce : new HashSet<>(EnchantManager.getEnchantments()))
                ce.finalizeEnchantment();

        if (printSuccess)
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] All Enchantments have been loaded.");

        items.add(new Minigun("Minigun", ChatColor.AQUA, "Fires a Volley of Arrows", 0, Material.BOW));
        items.add(new BeastmastersBow("Beastmaster's Bow", ChatColor.AQUA, "Tame the wilderness;and turn nature against your foes!", 0, Material.BOW));
        items.add(new HookshotBow("Hookshot Bow", ChatColor.AQUA, "Everyone is just one hook away", 0, Material.BOW));
        items.add(new HermesBoots("Hermes Boots", ChatColor.GOLD, "These boots are made for walkin'", 100, Material.DIAMOND_BOOTS));
        items.add(new LivefireBoots("Livefire Boots", ChatColor.DARK_RED, "Leave a burning trail...;Because it's fun!", 0, Material.DIAMOND_BOOTS));
        items.add(new RocketBoots("Rocket Boots", ChatColor.AQUA, "Up we go!; ;WARNING: May cause dismemberment,;            death;            and explosions", 0, Material.DIAMOND_BOOTS));
        items.add(new DruidBoots("Druid Boots", ChatColor.DARK_GREEN, "Let the nature rejuvenate you!", 0, Material.DIAMOND_BOOTS));
        items.add(new Flamethrower("Flamethrower", ChatColor.DARK_RED, "Burn, baby, burn!", 0, Material.FLINT_AND_STEEL));
        items.add(new NecromancersStaff("Necromancer's Staff of Destruction", ChatColor.AQUA, "Wreak chaos everywhere,;Because why not?", 0, Material.STICK));
        items.add(new ThorsAxe("Thor's Axe", ChatColor.GOLD, "Smite your enemies down with mighty thunder!;Note: Batteries not included.", 0, Material.DIAMOND_AXE));
        items.add(new Pyroaxe("Pyroaxe", ChatColor.DARK_RED, "Are your enemies burning?;Do you want to make their situation worse?;Then this is just perfect for you!", 0, Material.DIAMOND_AXE));
        items.add(new AssassinsBlade("Assassin's Blade", ChatColor.AQUA, "Sneak up on your enemies and hit them hard!; ;(High chance of failure against Hacked Clients)", 200, Material.GOLD_SWORD));
        items.add(new HealingShovel("Healing Shovel", ChatColor.GREEN, "Smacking other people in the face;has never been healthier!", 600, Material.GOLD_SPADE));
        items.add(new Firecracker("Firecracker", ChatColor.DARK_RED, "Makes every situation a good situation!", 0, Material.SNOW_BALL));
        items.add(new FireworkBattery("Firework-Battery", ChatColor.DARK_RED, "Make the sky shine bright with colors!", 0, Material.REDSTONE_BLOCK));
        items.add(new BearTrap("Bear Trap", ChatColor.GRAY, "Just hope that it does not contain bears...", 0, Material.IRON_PLATE));
        items.add(new PiranhaTrap("Piranha Trap", ChatColor.GRAY, "Who came up with this?", 0, Material.WOOD_PLATE));
        items.add(new PoisonIvy("Poison Ivy", ChatColor.DARK_GREEN, "If you're too cheap to afford ladders,;just take this, it'll work just fine!", 0, Material.VINE));
        items.add(new PricklyBlock("Prickly Block", ChatColor.LIGHT_PURPLE, "Just build a labyrinth out of these,;people will love you for it!", 0, Material.SAND));
        items.add(new Landmine("Landmine", ChatColor.GRAY, "Just don't trigger it yourself, please.", 0, Material.GOLD_PLATE));
        items.add(new Powergloves("Powergloves", ChatColor.AQUA, "Throw all your problems away!", 500, Material.QUARTZ));
        items.add(new Medikit("Medikit", ChatColor.GREEN, "Treats most of your ailments,;it even has a box of juice!", 2000, Material.NETHER_STALK));
        items.add(new Bandage("Bandage", ChatColor.GREEN, "It has little hearts on it,;so you know it's good", 1000, Material.PAPER));
        items.add(new Deathscythe("Deathscythe", ChatColor.DARK_GRAY, "An ancient evil lies within...", 400, Material.GOLD_HOE));
        items.add(new PotionLauncher("Potion Launcher", ChatColor.DARK_GRAY,
                "Instructions: Put potion into the righthand slot;                of the potion launcher,;                aim and fire!; ;Manufactured by " + ChatColor.MAGIC + "Taiterio", 20,
                Material.HOPPER));

        if (finalize)
            for (CItem ci : items)
                ci.finalizeItem();

        if (printSuccess)
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] All Items have been loaded.");

        deleteInactive();

        if (printSuccess)
            if (Boolean.parseBoolean(Main.config.getString("Global.Logging.Enabled")))
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] Took " + (System.currentTimeMillis() - time) + "ms to initialize Custom Enchantments.");
    }

    private static void deleteInactive() {
        Set<CEnchantment> e = new LinkedHashSet<>(EnchantManager.getEnchantments());
        Set<CItem> i = new LinkedHashSet<>(items);
        for (CEnchantment ce : e) {
            if (!Boolean.parseBoolean(config.getString("Enchantments." + ce.getOriginalName() + ".Enabled"))) {
                EnchantManager.getEnchantments().remove(ce);
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] Custom Enchantment " + ce.getOriginalName() + " is disabled in the config.");
            }
        }
        for (CItem ci : i) {
            if (!Boolean.parseBoolean(config.getString("Items." + ci.getOriginalName() + ".Enabled"))) {
                items.remove(ci);
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] Custom Item " + ci.getOriginalName() + " is disabled in the config.");
            }
        }
        Tools.resolveLists();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("customenchantments") || cmd.getName().equalsIgnoreCase("ce") || label.equalsIgnoreCase("encantamentos") || label.equalsIgnoreCase("encantos") || label.equalsIgnoreCase("ec")) {
            String result = commandC.processCommand(sender, args);
            if (!result.equals(""))
                sender.sendMessage(result);
            return true;
        }
        return false;
    }
}
