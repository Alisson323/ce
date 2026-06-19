package com.taiter.ce.listeners;

import com.taiter.ce.CBasic;
import com.taiter.ce.Main;
import java.util.HashSet;

public class CEListener implements org.bukkit.event.Listener {
    public final HashSet<CBasic> move = new HashSet<>();
    public final HashSet<CBasic> interact = new HashSet<>();
    public final HashSet<CBasic> interactE = new HashSet<>();
    public final HashSet<CBasic> interactR = new HashSet<>();
    public final HashSet<CBasic> interactL = new HashSet<>();
    public final HashSet<CBasic> damageTaken = new HashSet<>();
    public final HashSet<CBasic> damageGiven = new HashSet<>();
    public final HashSet<CBasic> damageNature = new HashSet<>();
    public final HashSet<CBasic> shootBow = new HashSet<>();
    public final HashSet<CBasic> projectileThrow = new HashSet<>();
    public final HashSet<CBasic> projectileHit = new HashSet<>();
    public final HashSet<CBasic> death = new HashSet<>();
    public final HashSet<CBasic> blockPlaced = new HashSet<>();
    public final HashSet<CBasic> blockBroken = new HashSet<>();
    public final HashSet<CBasic> wearItem = new HashSet<>();

    public final boolean useRuneCrafting = Main.plugin.getConfig().getBoolean("Global.Runecrafting.Enabled");
}
