package com.taiter.ce.hooks;

import com.taiter.ce.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.sk89q.worldguard.protection.GlobalRegionManager;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;

public class WorldGuardHook {

    public static boolean checkWorldGuard(Location l, Player p, String fs, boolean sendMessage) {
        if (p.isOp())
            return true;

        if (Main.getWorldGuard() != null) {
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
                            p.sendMessage(ChatColor.RED + "You cannot use this here!");
                        return false;
                    }
                } else if (!grm.allows(f, l, Main.getWorldGuard().wrapPlayer(p))) {
                    if (sendMessage)
                        p.sendMessage(ChatColor.RED + "You cannot use this here!");
                    return false;
                }
            }
        }
        return true;
    }
}
