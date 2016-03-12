package com.ryanmichela.trees;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Copyright 2014 Ryan Michela
 */
public class EditTreeCommand implements CommandExecutor {

  private final Plugin plugin;

  public EditTreeCommand(final Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command,
                           final String label, final String[] arg) {
    if (sender instanceof Player) {
      sender.sendMessage("You can only edit trees from the console.");
      return true;
    }

    if (arg.length != 1) {
      final File[] treeFiles = this.plugin.getDataFolder()
                                          .listFiles(new FilenameFilter() {

                                            @Override
                                            public boolean
                                                accept(final File dir,
                                                       final String name) {
                                              return name.endsWith(".xml");
                                            }
                                          });

      final String[] treeNames = new String[treeFiles.length];
      for (int i = 0; i < treeFiles.length; i++) {
        treeNames[i] = treeFiles[i].getName()
                                   .substring(0,
                                              treeFiles[i].getName()
                                                          .lastIndexOf('.'));
      }

      Arrays.sort(treeNames);

      sender.sendMessage("Available trees:");
      for (final File treeFile : treeFiles) {
        String treeName = treeFile.getName();
        treeName = treeName.substring(0, treeName.lastIndexOf('.'));
        sender.sendMessage(treeName);
      }
      return false;
    }

    try {
      final File pluginsDir = this.plugin.getDataFolder().getParentFile();
      final File gtPlugin[] = pluginsDir.listFiles(new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
          return name.startsWith("GiantTrees") && name.endsWith(".jar");
        }
      });

      final String gtPluginName = gtPlugin[0].getAbsolutePath();
      final String toOpen = new File(this.plugin.getDataFolder(), arg[0]
                                                                  + ".xml").getAbsolutePath();
      final ProcessBuilder pb = new ProcessBuilder("javaw", "-jar",
                                                   gtPluginName, toOpen);
      pb.start();
      sender.sendMessage("Loading " + arg[0] + "...");
    } catch (final IOException e) {
      this.plugin.getLogger().severe("Error starting Arbario: "
                                         + e.getMessage());
    }

    return true;
  }
}
