package com.taiter.ce.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import com.taiter.ce.listeners.CEventHandler;
import com.taiter.ce.Main;
import com.taiter.ce.utils.Tools;
import com.taiter.ce.CItems.CItem;

public class EntityEventListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void antiArrowSpam(ProjectileHitEvent event) {
        if (event.getEntityType().equals(EntityType.ARROW)) {
            Arrow arrow = (Arrow) event.getEntity();
            ProjectileSource shooter = arrow.getShooter();
            if (shooter instanceof Player) {
                if (arrow.hasMetadata("ce.minigunarrow")) {
                    if (((Player) shooter).getGameMode().equals(GameMode.CREATIVE)) {
                        arrow.remove();
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        Entity damaged = e.getEntity();

        if (damager.getUniqueId().equals(damaged.getUniqueId())) {
            return;
        }

        if (damaged instanceof Player) {
            CEventHandler.handleEvent((Player) damaged, e, Main.listener.damageTaken);
        }

        if (damager instanceof Player) {
            CEventHandler.handleEvent((Player) damager, e, Main.listener.damageGiven);
        } else if (damager instanceof Arrow) {
            if (damager.hasMetadata("ce.bow.item") || damager.hasMetadata("ce.bow.enchantment")) {
                CEventHandler.handleBows((Player) ((Projectile) damager).getShooter(), e);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void EntityDamageEvent(EntityDamageEvent e) {
        Entity damaged = e.getEntity();

        if (damaged instanceof Player) {
            CEventHandler.handleEvent((Player) damaged, e, Main.listener.damageNature);

            if (damaged.hasMetadata("ce.springs")) {
                e.setCancelled(true);
                Vector vel = damaged.getVelocity();
                vel.setY((vel.getY() * -0.75) > 1 ? vel.getY() * -0.75 : 0);
                damaged.setVelocity(vel);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void EntityExplodeEvent(EntityExplodeEvent e) {
        if (e.getEntity() != null && e.getEntity().hasMetadata("ce.explosive")) {
            e.getEntity().remove();
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void EntityShootBowEvent(EntityShootBowEvent e) {
        Entity shooter = e.getEntity();

        if (shooter instanceof Player) {
            CEventHandler.handleEvent((Player) shooter, e, Main.listener.shootBow);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void ProjectileHitEvent(ProjectileHitEvent e) {
        ProjectileSource shooter = e.getEntity().getShooter();

        if (shooter instanceof Player) {
            if (e.getEntity().hasMetadata("ce.projectile.item")) {
                CItem ci = Tools.getItemByOriginalname(e.getEntity().getMetadata("ce.projectile.item").get(0).asString());
                if (ci != null) {
                    ci.effect(e, (Player) shooter);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void ProjectileLaunchEvent(ProjectileLaunchEvent e) {
        ProjectileSource shooter = e.getEntity().getShooter();

        if (shooter instanceof Player) {
            CEventHandler.handleEvent((Player) shooter, e, Main.listener.projectileThrow);
        }
    }
}
