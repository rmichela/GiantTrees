package com.ryanmichela.trees;

import me.desht.dhutils.ItemMessage;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Copyright 2014 Ryan Michela
 */
public class PopupHandler {

  private final Plugin plugin;

  public PopupHandler(final Plugin plugin) {
    this.plugin = plugin;
  }

  public void sendPopup(final Player player, final String message) {
    try {
      final ItemMessage itemMessage = new ItemMessage(this.plugin);
      itemMessage.sendMessage(player, message);
    } catch (final Exception e) {
      player.sendMessage(message);
    }
  }
}
