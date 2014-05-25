package com.ryanmichela.trees;

import com.ryanmichela.trees.rendering.TreeRenderer;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Random;

/**
 * Copyright 2014 Ryan Michela
 */
public class CreateTreeCommand implements CommandExecutor {
    private Plugin plugin;
    private TreeRenderer renderer;
    private PopupHandler popup;

    public CreateTreeCommand(Plugin plugin) {
        this.plugin = plugin;
        renderer = new TreeRenderer(plugin);
        popup = new PopupHandler(plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arg) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("Cannot create giant trees from the console");
            return true;
        }

        if (arg.length != 1) {
            // incorrect number of arguments
            return false;
        }

        Player player = (Player) sender;
        if (player.hasPermission("gianttrees.create")) {
            Chunk chunk = player.getLocation().getChunk();
            World world = chunk.getWorld();
            Random seed = new Random(world.getSeed());

            popup.sendPopup(player, "Stand back!");

            String species = arg[0];
            File treeFile = new File(plugin.getDataFolder(), species + ".xml");
            File rootFile = new File(plugin.getDataFolder(), species + ".root.xml");
            renderer.renderTree(player.getLocation(), treeFile, rootFile, true, seed.nextInt());
        }
        return true;
    }
}
