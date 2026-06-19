package com.taiter.ce.effects;

import com.taiter.ce.Main;
import java.util.Random;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class VisualEffects {

    private static Color fireworkColor(int i) {
        switch (i) {
        default:
        case 1:
            return Color.SILVER;
        case 2:
            return Color.AQUA;
        case 3:
            return Color.BLACK;
        case 4:
            return Color.BLUE;
        case 5:
            return Color.FUCHSIA;
        case 6:
            return Color.GRAY;
        case 7:
            return Color.GREEN;
        case 8:
            return Color.LIME;
        case 9:
            return Color.MAROON;
        case 10:
            return Color.NAVY;
        case 11:
            return Color.OLIVE;
        case 12:
            return Color.ORANGE;
        case 13:
            return Color.PURPLE;
        case 14:
            return Color.RED;
        case 15:
            return Color.YELLOW;
        case 16:
            return Color.TEAL;
        }
    }

    public static Firework shootFirework(Location loc, Random rand) {
        int type = rand.nextInt(5) + 1;
        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        Type ft = null;
        switch (type) {
        case 1:
            ft = Type.BALL;
            break;
        case 2:
            ft = Type.BALL_LARGE;
            break;
        case 3:
            ft = Type.BURST;
            break;
        case 4:
            ft = Type.CREEPER;
            break;
        case 5:
            ft = Type.STAR;
            break;
        }
        FireworkEffect effect = FireworkEffect.builder().flicker(rand.nextBoolean()).withColor(fireworkColor(rand.nextInt(16) + 1)).withFade(fireworkColor(rand.nextInt(16) + 1))
                .trail(rand.nextBoolean()).with(ft).trail(rand.nextBoolean()).build();
        meta.addEffect(effect);
        firework.setFireworkMeta(meta);
        return firework;
    }

    public static void applyBleed(final Player target, final int bleedDuration) {
        target.sendMessage(ChatColor.RED + "You are Bleeding!");
        target.setMetadata("ce.bleed", new FixedMetadataValue(Main.plugin, null));
        new BukkitRunnable() {
            int seconds = bleedDuration;

            @Override
            public void run() {
                if (seconds >= 0) {
                    if (!target.isDead() && target.hasMetadata("ce.bleed")) {
                        target.damage(1 + (((Damageable) target).getHealth() / 15));
                        seconds--;
                    } else {
                        target.removeMetadata("ce.bleed", Main.plugin);
                        this.cancel();
                    }
                } else {
                    target.removeMetadata("ce.bleed", Main.plugin);
                    target.sendMessage(ChatColor.GREEN + "You have stopped Bleeding!");
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.plugin, 0L, 20L);
    }
}
