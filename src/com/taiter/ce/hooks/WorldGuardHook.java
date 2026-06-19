package com.taiter.ce.hooks;

import com.taiter.ce.Main;
import com.taiter.ce.utils.Translator;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.sk89q.worldguard.protection.GlobalRegionManager;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;

public class WorldGuardHook {

    private static Boolean isWorldGuardModern = null;

    private static boolean isModern() {
        if (isWorldGuardModern == null) {
            try {
                Class.forName("com.sk89q.worldguard.WorldGuard");
                isWorldGuardModern = true;
            } catch (ClassNotFoundException e) {
                isWorldGuardModern = false;
            }
        }
        return isWorldGuardModern;
    }

    private static boolean checkModern(Location l, Player p, String fs) {
        try {
            Class<?> wgClass = Class.forName("com.sk89q.worldguard.WorldGuard");
            Object wg = wgClass.getMethod("getInstance").invoke(null);
            Object platform = wgClass.getMethod("getPlatform").invoke(wg);
            Object container = platform.getClass().getMethod("getRegionContainer").invoke(platform);
            Object query = container.getClass().getMethod("createQuery").invoke(container);
            
            Class<?> adapterClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
            Object adaptedLocation = adapterClass.getMethod("adapt", Location.class).invoke(null, l);
            Object adaptedPlayer = adapterClass.getMethod("adapt", Player.class).invoke(null, p);
            
            Class<?> flagsClass = Class.forName("com.sk89q.worldguard.protection.flags.Flags");
            Object flag = null;
            if (fs.equalsIgnoreCase("BUILD")) {
                flag = flagsClass.getField("BUILD").get(null);
            } else if (fs.equalsIgnoreCase("PVP")) {
                flag = flagsClass.getField("PVP").get(null);
            } else {
                try {
                    flag = flagsClass.getField(fs.toUpperCase()).get(null);
                } catch (Exception ex) {
                    // Ignora
                }
            }
            
            if (flag != null) {
                Class<?> stateFlagClass = Class.forName("com.sk89q.worldguard.protection.flags.StateFlag");
                java.lang.reflect.Method testStateMethod = query.getClass().getMethod("testState", 
                        Class.forName("com.sk89q.worldedit.util.Location"), 
                        Class.forName("com.sk89q.worldguard.LocalPlayer"), 
                        stateFlagClass);
                
                return (boolean) testStateMethod.invoke(query, adaptedLocation, adaptedPlayer, flag);
            }
        } catch (Exception e) {
            // Silencioso
        }
        return true;
    }

    public static boolean checkWorldGuard(Location l, Player p, String fs, boolean sendMessage) {
        if (p.isOp())
            return true;

        if (Main.getWorldGuard() != null) {
            if (isModern()) {
                if (!checkModern(l, p, fs)) {
                    if (sendMessage)
                        p.sendMessage(Translator.get("Menu.Messages.NotPermittedUseThis", ChatColor.RED + "You are not permitted to use this"));
                    return false;
                }
                return true;
            }

            GlobalRegionManager grm = Main.getWorldGuard().getGlobalRegionManager();
            if (grm == null)
                return true;

            StateFlag f = null;
            for (Flag<?> df : DefaultFlag.flagsList)
                if (fs.equalsIgnoreCase(df.getName()))
                    f = (StateFlag) df;

            if (f != null) {
                if (f.equals(DefaultFlag.BUILD)) {
                    if (!grm.canBuild(p, l)) {
                        if (sendMessage)
                            p.sendMessage(Translator.get("Menu.Messages.NotPermittedUseThis", ChatColor.RED + "You are not permitted to use this"));
                        return false;
                    }
                } else if (!grm.allows(f, l, Main.getWorldGuard().wrapPlayer(p))) {
                    if (sendMessage)
                        p.sendMessage(Translator.get("Menu.Messages.NotPermittedUseThis", ChatColor.RED + "You are not permitted to use this"));
                    return false;
                }
            }
        }
        return true;
    }
}
