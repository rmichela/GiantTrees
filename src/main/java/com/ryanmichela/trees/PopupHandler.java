package com.ryanmichela.trees;

import me.desht.dhutils.ItemMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Copyright 2014 Ryan Michela
 */
public class PopupHandler {
    private Plugin plugin;

    public PopupHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    public void sendPopup(Player player, String message) {
        try {
            ItemMessage itemMessage = new ItemMessage(plugin);
            itemMessage.sendMessage(player, message);
        } catch (Exception e) {
            player.sendMessage(message);
        }
    }
}
