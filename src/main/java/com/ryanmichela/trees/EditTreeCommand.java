package com.ryanmichela.trees;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Copyright 2014 Ryan Michela
 */
public class EditTreeCommand implements CommandExecutor {
    private Plugin plugin;

    public EditTreeCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arg) {
        if (sender instanceof Player) {
            sender.sendMessage("You can only edit trees from the console.");
            return true;
        }

        if (arg.length != 1) {
            return false;
        }

        try {
            File pluginsDir = plugin.getDataFolder().getParentFile();
            File gtPlugin[] = pluginsDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith("GiantTrees") && name.endsWith(".jar");
                }
            });

            String gtPluginName = gtPlugin[0].getAbsolutePath();
            String toOpen = new File(plugin.getDataFolder(), arg[0] + ".xml").getAbsolutePath();
            ProcessBuilder pb = new ProcessBuilder("javaw", "-jar", gtPluginName, toOpen);
            pb.start();
            sender.sendMessage("Loading " + arg[0] + "...");
        } catch (IOException e) {
            plugin.getLogger().severe("Error starting Arbario: " + e.getMessage());
        }

        return true;
    }
}
