package com.taiter.ce.hooks;

import com.taiter.ce.Main;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Updater {
    private final Main main;
    private URL updateListURL;
    private URL updateDownloadURL;
    private String currentVersion;
    private String newVersion;
    private String newMD5;
    public boolean hasUpdate = false;
    public boolean hasChecked = false;

    public Updater(Main main) {
        this.main = main;
        currentVersion = Main.plugin.getDescription().getVersion();
        try {
            updateListURL = new URL("https://api.curseforge.com/servermods/files?projectIds=54406");
        } catch (MalformedURLException e) {
        }
    }

    public void updateCheck() {
        try {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] Checking for updates...");

            URLConnection connection = updateListURL.openConnection();
            connection.setConnectTimeout(5000);
            connection.addRequestProperty("User-Agent", "Custom Enchantments - Update Checker");
            connection.setDoOutput(true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = reader.readLine();
            JSONArray array = (JSONArray) JSONValue.parse(response);
            JSONObject newestUpdate = (JSONObject) array.get(array.size() - 1);

            newVersion = newestUpdate.get("name").toString().replace("Custom Enchantments ", "").trim();
            newMD5 = newestUpdate.get("md5").toString();

            int newLength = newVersion.length();
            int currentLength = currentVersion.length();

            double versionNew;
            double versionCurrent;

            boolean newHasSubVersion = false;
            boolean currentHasSubVersion = false;

            try {
                versionNew = Double.parseDouble(newVersion);
            } catch (NumberFormatException ex) {
                newHasSubVersion = true;
                versionNew = Double.parseDouble(newVersion.substring(0, newVersion.length() - 1));
            }

            try {
                versionCurrent = Double.parseDouble(currentVersion);
            } catch (NumberFormatException ex) {
                currentHasSubVersion = true;
                versionCurrent = Double.parseDouble(currentVersion.substring(0, currentVersion.length() - 1));
            }

            if ((versionNew > versionCurrent) || ((versionNew == versionCurrent) && newHasSubVersion && currentHasSubVersion
                    && ((byte) newVersion.toCharArray()[newLength - 1] > (byte) currentVersion.toCharArray()[currentLength - 1]))) {
                hasUpdate = true;
                main.hasUpdate = true;
                updateDownloadURL = new URL(newestUpdate.get("downloadUrl").toString().replace("\\.", ""));
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] A new update is available!");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] The new version is " + ChatColor.AQUA + newVersion + ChatColor.GREEN + ".");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] You are currently using " + ChatColor.AQUA + currentVersion + ChatColor.GREEN + ".");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] You can use '/ce update applyupdate' to update automatically.");

            } else {
                hasUpdate = false;
                main.hasUpdate = false;
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] You are using the latest Version of CE!");
            }
            hasChecked = true;
            main.hasChecked = true;
        } catch (IOException ioex) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] Failed to check for updates");
        }
    }

    public void update() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] Updating to Version " + newVersion + " started");

        BufferedInputStream input = null;
        FileOutputStream output = null;

        try {
            boolean notify = Boolean.parseBoolean(Main.config.getString("Global.Updates.UpdateNotifications"));
            int updateSize = updateDownloadURL.openConnection().getContentLength();
            File file = new File(Main.plugin.getDataFolder().getParent(), "CustomEnchantments.jar");
            input = new BufferedInputStream(updateDownloadURL.openStream());
            output = new FileOutputStream(file);

            int bufferSize = (int) Math.ceil(updateSize / 100);
            byte[] data = new byte[bufferSize];
            int downloaded = 0;
            int cRead;

            while ((cRead = input.read(data, 0, bufferSize)) != -1) {
                output.write(data, 0, cRead);
                downloaded += cRead;

                if (notify) {
                    int percentage = ((downloaded * 100) / updateSize);
                    if (percentage % 25 == 0)
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] Downloaded " + percentage + "% (" + downloaded + "/" + updateSize + " Bytes).");
                }
            }

            testFile(file, bufferSize);
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[CE] Update " + newVersion + " successfully downloaded. Restart/Reload the Server to apply changes.");
            currentVersion = newVersion;
            hasUpdate = false;
            main.hasUpdate = false;
            input.close();
            output.close();
        } catch (Exception e) {
            try {
                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
            } catch (IOException ex) {
            }
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[CE] Updating to Version " + newVersion + " failed");
        }
    }

    private boolean testFile(File f, int bufferSize) throws Exception {
        InputStream fis = new FileInputStream(f);
        byte[] buffer = new byte[bufferSize];
        MessageDigest md = MessageDigest.getInstance("MD5");
        int cRead;

        while ((cRead = fis.read(buffer)) != -1)
            md.update(buffer, 0, cRead);

        fis.close();

        byte[] result = md.digest();
        StringBuilder md5 = new StringBuilder();
        for (byte b : result) {
            md5.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }

        return md5.toString().equals(newMD5);
    }
}
