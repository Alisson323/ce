package com.taiter.ce.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import com.taiter.ce.Main;
import com.taiter.ce.utils.Tools;
import com.taiter.ce.Enchantments.EnchantManager;

public class ReloadSubCommand implements SubCommand {
    private final Main main;

    public ReloadSubCommand(Main main) {
        this.main = main;
    }

    @Override
    public String execute(CommandSender sender, String[] args) {
        String node = "ce.cmd.*";
        String requiredPermission = "ce.cmd.reload";
        if (!sender.hasPermission(node) && !sender.hasPermission(requiredPermission) && !sender.isOp()) {
            return ChatColor.RED + "You do not have permission to use this command.";
        }

        Main.plugin.reloadConfig();
        Main.config = Main.plugin.getConfig();

        EnchantManager.getEnchantments().clear();
        Main.items.clear();
        main.initializeListener();

        Main.makeLists(true, false);

        EnchantManager.setMaxEnchants(Integer.parseInt(Main.config.getString("Global.Enchantments.MaximumCustomEnchantments")));
        EnchantManager.setLorePrefix(Main.resolveEnchantmentColor());
        EnchantManager.setEnchantBookName(ChatColor.translateAlternateColorCodes('&', Main.config.getString("Global.Books.Name")));

        Tools.generateInventories();

        return ChatColor.GREEN + "The Custom Enchantments config has been reloaded successfully.";
    }
}
