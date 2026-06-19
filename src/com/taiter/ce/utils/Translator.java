package com.taiter.ce.utils;

import java.io.File;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Translator {

    private static FileConfiguration translationConfig;
    private static FileConfiguration fallbackConfig;

    public static void init(Plugin plugin) {
        String lang = plugin.getConfig().getString("Language", "en_US");
        File langDir = new File(plugin.getDataFolder(), "languages");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }

        copyDefaultLang(plugin, "en_US.yml");
        copyDefaultLang(plugin, "pt_BR.yml");

        File fallbackFile = new File(langDir, "en_US.yml");
        fallbackConfig = YamlConfiguration.loadConfiguration(fallbackFile);

        File langFile = new File(langDir, lang + ".yml");
        if (langFile.exists()) {
            translationConfig = YamlConfiguration.loadConfiguration(langFile);
        } else {
            plugin.getLogger().warning("[CE] Language file " + lang + ".yml not found. Falling back to en_US.");
            translationConfig = fallbackConfig;
        }
    }

    private static void copyDefaultLang(Plugin plugin, String fileName) {
        File file = new File(plugin.getDataFolder(), "languages/" + fileName);
        if (!file.exists()) {
            try {
                plugin.saveResource("languages/" + fileName, false);
            } catch (Exception e) {
                plugin.getLogger().warning("[CE] Could not save default resource: " + fileName);
            }
        }
    }

    public static String get(String key, String defaultValue) {
        String message = null;
        if (translationConfig != null) {
            message = translationConfig.getString(key);
        }
        if (message == null && fallbackConfig != null) {
            message = fallbackConfig.getString(key);
        }
        if (message == null) {
            return ChatColor.translateAlternateColorCodes('&', defaultValue);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String get(String key) {
        return get(key, key);
    }

    public static List<String> getStringList(String key) {
        List<String> list = null;
        if (translationConfig != null) {
            list = translationConfig.getStringList(key);
        }
        if ((list == null || list.isEmpty()) && fallbackConfig != null) {
            list = fallbackConfig.getStringList(key);
        }
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                list.set(i, ChatColor.translateAlternateColorCodes('&', list.get(i)));
            }
        }
        return list;
    }
}
