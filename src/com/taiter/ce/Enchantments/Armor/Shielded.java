package com.taiter.ce.Enchantments.Armor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/*
* This file is part of Custom Enchantments
* Copyright (C) Taiterio 2015
*
* This program is free software: you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as published by the
* Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
* for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.utils.ReflectionHelper;
import com.taiter.ce.Enchantments.CEnchantment;

public class Shielded extends CEnchantment {

    int baseStrength;
    int strengthPerLevel;
    long cooldown;

    private static Method bukkitGetAbsorption;
    private static Method bukkitSetAbsorption;
    private static Method getAbsorptionHearts;
    private static Method setAbsorptionHearts;

    static {
        try {
            bukkitGetAbsorption = Player.class.getMethod("getAbsorptionAmount");
            bukkitSetAbsorption = Player.class.getMethod("setAbsorptionAmount", double.class);
        } catch (Throwable t) {
            try {
                bukkitGetAbsorption = Player.class.getMethod("getAbsorptionAmount");
                bukkitSetAbsorption = Player.class.getMethod("setAbsorptionAmount", float.class);
            } catch (Throwable t2) {
            }
        }
        if (bukkitGetAbsorption == null) {
            try {
                Class<?> entityHuman = ReflectionHelper.getNMSClass("EntityHuman");
                if (entityHuman != null) {
                    getAbsorptionHearts = entityHuman.getDeclaredMethod("getAbsorptionHearts");
                    setAbsorptionHearts = entityHuman.getDeclaredMethod("setAbsorptionHearts", float.class);
                }
            } catch (Throwable t) {
            }
        }
    }

    public Shielded(Application app) {
        super(app);
        configEntries.put("BaseStrength", 4);
        configEntries.put("StrengthPerLevel", 2);
        configEntries.put("Cooldown", 30);
        triggers.add(Trigger.DAMAGE_TAKEN);
    }

    @Override
    public void effect(Event e, ItemStack item, int level) {
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
        Player player = (Player) event.getEntity();
        if (getAbsorptionHearts(player) <= 0) {
            setAbsorptionHearts(player, baseStrength + level * strengthPerLevel);
            generateCooldown(player, cooldown);
        }
    }

    @Override
    public void initConfigEntries() {
        baseStrength = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".BaseStrength"));
        strengthPerLevel = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".StrengthPerLevel"));
        cooldown = Long.parseLong(getConfig().getString("Enchantments." + getOriginalName() + ".Cooldown"));
    }

    private float getAbsorptionHearts(Player player) {
        if (bukkitGetAbsorption != null) {
            try {
                Number val = (Number) bukkitGetAbsorption.invoke(player);
                return val.floatValue();
            } catch (Throwable t) {
            }
        }
        if (getAbsorptionHearts != null) {
            try {
                Number val = (Number) getAbsorptionHearts.invoke(ReflectionHelper.getEntityHandle(player));
                return val.floatValue();
            } catch (Throwable t) {
            }
        }
        return 0;
    }

    private void setAbsorptionHearts(Player player, float newValue) {
        if (bukkitSetAbsorption != null) {
            try {
                Class<?>[] paramTypes = bukkitSetAbsorption.getParameterTypes();
                if (paramTypes.length > 0) {
                    if (paramTypes[0] == double.class) {
                        bukkitSetAbsorption.invoke(player, (double) newValue);
                    } else {
                        bukkitSetAbsorption.invoke(player, newValue);
                    }
                }
                return;
            } catch (Throwable t) {
            }
        }
        if (setAbsorptionHearts != null) {
            try {
                setAbsorptionHearts.invoke(ReflectionHelper.getEntityHandle(player), newValue);
            } catch (Throwable t) {
            }
        }
    }

}
