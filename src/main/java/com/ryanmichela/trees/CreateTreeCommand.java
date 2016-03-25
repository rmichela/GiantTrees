/*
 * Copyright (C) 2014 Ryan Michela
 * Copyright (C) 2016 Ronald Jack Jenkins Jr.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ryanmichela.trees;

import java.io.File;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import com.ryanmichela.trees.rendering.TreeRenderer;

public class CreateTreeCommand implements CommandExecutor {

  private final Plugin       plugin;
  private final PopupHandler popup;
  private final TreeRenderer renderer;

  public CreateTreeCommand(final Plugin plugin) {
    this.plugin = plugin;
    this.renderer = new TreeRenderer(plugin);
    this.popup = new PopupHandler(plugin);
  }

  @Override
  public boolean onCommand(final CommandSender sender, final Command command,
                           final String label, final String[] arg) {
    if (sender instanceof ConsoleCommandSender) {
      sender.sendMessage("Cannot create giant trees from the console");
      return true;
    }

    if (arg.length != 1) {
      // incorrect number of arguments
      return false;
    }

    final Player player = (Player) sender;
    if (player.hasPermission("gianttrees.create")) {
      final Chunk chunk = player.getLocation().getChunk();
      final World world = chunk.getWorld();
      final Random seed = new Random(world.getSeed());

      final String species = arg[0];
      final File treeFile = new File(this.plugin.getDataFolder(), species
                                                                  + ".xml");
      final File rootFile = new File(this.plugin.getDataFolder(), species
                                                                  + ".root.xml");

      if (!treeFile.exists()) {
        sender.sendMessage(ChatColor.RED + "Tree " + species
                           + " does not exist.");
        sender.sendMessage("Use \"/tree-edit " + species
                           + "\" from the server console to create it.");
        return true;
      }

      final Block highestSoil = this.getHighestSoil(player.getWorld()
                                                          .getHighestBlockAt(player.getLocation()));

      final Location base = highestSoil.getLocation();
      this.popup.sendPopup(player, "Stand back!");
      this.renderer.renderTreeWithHistory(base, treeFile, rootFile,
                                          seed.nextInt(), player, true);
    }
    return true;
  }

  @EventHandler
  public void onPlayerInteract(final PlayerInteractEvent event) {

  }

  Block getHighestSoil(Block highestBlock) {
    while ((highestBlock.getY() > 0)
           && (highestBlock.getType() != Material.DIRT)
           && // Includes podzol
           (highestBlock.getType() != Material.GRASS)
           && (highestBlock.getType() != Material.MYCEL)
           && (highestBlock.getType() != Material.SAND)) {
      highestBlock = highestBlock.getRelative(BlockFace.DOWN);
    }
    return highestBlock;
  }
}
