package com.taiter.ce.commands;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import com.taiter.ce.Main;

public class CeCommand {

    private final Main main;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public CeCommand(Main m) {
        this.main = m;
        registerSubCommands();
    }

    private void registerSubCommands() {
        subCommands.put("reload", new ReloadSubCommand(main));
        subCommands.put("update", new UpdateSubCommand(main));
        subCommands.put("give", new GiveSubCommand(main));
        subCommands.put("runecrafting", new RunecraftingSubCommand(main));
        subCommands.put("list", new ListSubCommand(main));
        subCommands.put("remove", new RemoveSubCommand(main));
        subCommands.put("menu", new MenuSubCommand(main));
        subCommands.put("enchant", new EnchantSubCommand(main));
        subCommands.put("change", new ChangeSubCommand(main));
    }

    public String processCommand(CommandSender sender, String[] args) {
        if (args.length >= 1) {
            String name = args[0].toLowerCase();

            if (name.startsWith("u")) {
                name = "update";
            } else if (name.startsWith("g")) {
                name = "give";
            } else if (name.startsWith("rune")) {
                name = "runecrafting";
            } else if (name.startsWith("l")) {
                name = "list";
            } else if (name.startsWith("m")) {
                name = "menu";
            } else if (name.startsWith("i") || name.startsWith("e")) {
                name = "enchant";
            } else if (name.startsWith("c")) {
                name = "change";
            }

            SubCommand cmd = subCommands.get(name);
            if (cmd != null) {
                return cmd.execute(sender, args);
            }
        }

        return ChatColor.RED + "Correct Usage: /ce <Reload/List/Remove/Enchant/Menu/Change/Give/Update>";
    }
}
