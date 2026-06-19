package com.taiter.ce.effects;

import com.taiter.ce.utils.ReflectionHelper;
import java.lang.reflect.Constructor;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class EffectManager {

    public enum ParticleEffect {
        EXPLOSION_NORMAL,
        EXPLOSION_LARGE,
        EXPLOSION_HUGE,
        FIREWORKS_SPARK,
        WATER_BUBBLE,
        WATER_SPLASH,
        WATER_WAKE,
        SUSPENDED,
        SUSPENDED_DEPTH,
        CRIT,
        CRIT_MAGIC,
        SMOKE_NORMAL,
        SMOKE_LARGE,
        SPELL,
        SPELL_INSTANT,
        SPELL_MOB,
        SPELL_MOB_AMBIENT,
        SPELL_WITCH,
        DRIP_WATER,
        DRIP_LAVA,
        VILLAGER_ANGRY,
        VILLAGER_HAPPY,
        TOWN_AURA,
        NOTE,
        PORTAL,
        ENCHANTMENT_TABLE,
        FLAME,
        LAVA,
        FOOTSTEP,
        CLOUD,
        REDSTONE,
        SNOWBALL,
        SNOW_SHOVEL,
        SLIME,
        HEART,
        BARRIER,
        ITEM_CRACK,
        BLOCK_CRACK,
        BLOCK_DUST,
        WATER_DROP,
        ITEM_TAKE,
        MOB_APPEARANCE,
        END_ROD,
        DAMAGE_INDICATOR,
        SWEEP_ATTACK
    }

    private static Constructor<?> effectConstructor;
    private static Object[] particles;
    private static boolean useNativeParticles;

    static {
        try {
            Player.class.getMethod("spawnParticle", org.bukkit.Particle.class, Location.class, int.class, double.class, double.class, double.class, double.class);
            useNativeParticles = true;
        } catch (Throwable t) {
            useNativeParticles = false;
        }
    }

    private static org.bukkit.Particle getBukkitParticle(ParticleEffect effect) {
        try {
            return org.bukkit.Particle.valueOf(effect.name());
        } catch (Throwable t1) {
            String[] fallbackNames = null;
            switch (effect) {
                case EXPLOSION_NORMAL: fallbackNames = new String[]{"POOF", "EXPLOSION_NORMAL"}; break;
                case EXPLOSION_LARGE: fallbackNames = new String[]{"EXPLOSION", "EXPLOSION_LARGE"}; break;
                case EXPLOSION_HUGE: fallbackNames = new String[]{"GUST", "EXPLOSION_EMITTER", "EXPLOSION_HUGE"}; break;
                case FIREWORKS_SPARK: fallbackNames = new String[]{"FIREWORK", "FIREWORKS_SPARK"}; break;
                case WATER_BUBBLE: fallbackNames = new String[]{"BUBBLE", "WATER_BUBBLE"}; break;
                case WATER_SPLASH: fallbackNames = new String[]{"SPLASH", "WATER_SPLASH"}; break;
                case WATER_WAKE: fallbackNames = new String[]{"FISHING", "WATER_WAKE"}; break;
                case CRIT_MAGIC: fallbackNames = new String[]{"ENCHANTED_HIT", "CRIT_MAGIC"}; break;
                case SMOKE_NORMAL: fallbackNames = new String[]{"SMOKE", "SMOKE_NORMAL"}; break;
                case SMOKE_LARGE: fallbackNames = new String[]{"LARGE_SMOKE", "SMOKE_LARGE"}; break;
                case SPELL: fallbackNames = new String[]{"EFFECT", "SPELL"}; break;
                case SPELL_INSTANT: fallbackNames = new String[]{"INSTANT_EFFECT", "SPELL_INSTANT"}; break;
                case SPELL_MOB: fallbackNames = new String[]{"ENTITY_EFFECT", "SPELL_MOB"}; break;
                case SPELL_MOB_AMBIENT: fallbackNames = new String[]{"AMBIENT_ENTITY_EFFECT", "SPELL_MOB_AMBIENT"}; break;
                case SPELL_WITCH: fallbackNames = new String[]{"WITCH", "SPELL_WITCH"}; break;
                case DRIP_WATER: fallbackNames = new String[]{"DRIPPING_WATER", "DRIP_WATER"}; break;
                case DRIP_LAVA: fallbackNames = new String[]{"DRIPPING_LAVA", "DRIP_LAVA"}; break;
                case VILLAGER_ANGRY: fallbackNames = new String[]{"ANGRY_VILLAGER", "VILLAGER_ANGRY"}; break;
                case VILLAGER_HAPPY: fallbackNames = new String[]{"HAPPY_VILLAGER", "VILLAGER_HAPPY"}; break;
                case TOWN_AURA: fallbackNames = new String[]{"MYCELIUM", "TOWN_AURA"}; break;
                case ENCHANTMENT_TABLE: fallbackNames = new String[]{"ENCHANT", "ENCHANTMENT_TABLE"}; break;
                case REDSTONE: fallbackNames = new String[]{"DUST", "REDSTONE"}; break;
                case SNOW_SHOVEL: fallbackNames = new String[]{"ITEM_SNOWBALL", "SNOW_SHOVEL"}; break;
                case WATER_DROP: fallbackNames = new String[]{"RAIN", "WATER_DROP"}; break;
                default: break;
            }
            if (fallbackNames != null) {
                for (String name : fallbackNames) {
                    try {
                        return org.bukkit.Particle.valueOf(name);
                    } catch (Throwable t2) {}
                }
            }
            return null;
        }
    }

    public EffectManager() {
        if (!useNativeParticles) {
            try {
                effectConstructor = ReflectionHelper.getEffectPacketConstructor();
                particles = (Object[]) ReflectionHelper.loadEnumParticleValues();
            } catch (Throwable e) {
            }
        }
    }

    public static void playSound(Location loc, String sound, float volume, float pitch) {
        com.cryptomorin.xseries.XSound.matchXSound(sound)
                .ifPresent(xs -> xs.play(loc, volume, pitch));
    }

    public static void sendBlockEffect(List<Player> targets, Location loc, Vector offset, int blockID, float speed, int amount, byte data) {
        if (useNativeParticles) {
            try {
                org.bukkit.Material mat = org.bukkit.Material.getMaterial(blockID);
                if (mat == null) mat = org.bukkit.Material.STONE;
                
                Object blockData = Bukkit.class.getMethod("createBlockData", org.bukkit.Material.class).invoke(null, mat);
                org.bukkit.Particle nativePart = org.bukkit.Particle.valueOf("BLOCK");
                
                java.lang.reflect.Method spawnMethod = Player.class.getMethod("spawnParticle", 
                        org.bukkit.Particle.class, Location.class, int.class, double.class, double.class, double.class, double.class, Object.class);
                
                for (Player p : targets) {
                    spawnMethod.invoke(p, nativePart, loc, amount, offset.getX(), offset.getY(), offset.getZ(), (double) speed, blockData);
                }
                return;
            } catch (Throwable t) {
                try {
                    org.bukkit.Material mat = org.bukkit.Material.getMaterial(blockID);
                    if (mat == null) mat = org.bukkit.Material.STONE;
                    org.bukkit.Particle nativePart = org.bukkit.Particle.valueOf("BLOCK_CRACK");
                    
                    Object matData = new org.bukkit.material.MaterialData(mat, data);
                    java.lang.reflect.Method spawnMethod = Player.class.getMethod("spawnParticle", 
                            org.bukkit.Particle.class, Location.class, int.class, double.class, double.class, double.class, double.class, Object.class);
                    
                    for (Player p : targets) {
                        spawnMethod.invoke(p, nativePart, loc, amount, offset.getX(), offset.getY(), offset.getZ(), (double) speed, matData);
                    }
                    return;
                } catch (Throwable t2) {}
            }
        }

        if (effectConstructor != null && particles != null) {
            try {
                Object packet = effectConstructor.newInstance(particles[5], true, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), (float) offset.getX(), (float) offset.getY(), (float) offset.getZ(),
                        speed, amount, new int[] { blockID, data });
                for (Player p : targets)
                    ReflectionHelper.sendPacket(p, packet);
            } catch (Exception e) {
            }
        }
    }

    public static void sendEffect(List<Player> targets, ParticleEffect particle, Location loc, float speed, int amount) {
        sendEffect(targets, particle, loc, new Vector(Math.random(), Math.random(), Math.random()), speed, amount);
    }

    public static void sendEffect(List<Player> targets, ParticleEffect particle, Location loc, Vector offset, float speed, int amount) {
        if (useNativeParticles) {
            org.bukkit.Particle nativePart = getBukkitParticle(particle);
            if (nativePart != null) {
                Object data = null;
                try {
                    java.lang.reflect.Method getDataTypeMethod = nativePart.getClass().getMethod("getDataType");
                    Class<?> dataType = (Class<?>) getDataTypeMethod.invoke(nativePart);
                    if (dataType != null) {
                        if (dataType.getName().equals("org.bukkit.Color")) {
                            data = org.bukkit.Color.WHITE;
                        } else if (dataType.getName().equals("org.bukkit.Particle$DustOptions")) {
                            data = dataType.getConstructor(org.bukkit.Color.class, float.class)
                                    .newInstance(org.bukkit.Color.RED, 1.0f);
                        }
                    }
                } catch (Throwable t) {
                }
                for (Player p : targets) {
                    try {
                        if (data != null) {
                            p.spawnParticle(nativePart, loc, amount, offset.getX(), offset.getY(), offset.getZ(), speed, data);
                        } else {
                            p.spawnParticle(nativePart, loc, amount, offset.getX(), offset.getY(), offset.getZ(), speed);
                        }
                    } catch (Throwable t) {
                    }
                }
                return;
            }
        }

        if (effectConstructor != null && particles != null) {
            try {
                Object packet = effectConstructor.newInstance(particles[particle.ordinal()], true, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), (float) offset.getX(), (float) offset.getY(),
                        (float) offset.getZ(), speed, amount, null);
                for (Player p : targets)
                    ReflectionHelper.sendPacket(p, packet);
            } catch (Exception e) {
            }
        }
    }
}
