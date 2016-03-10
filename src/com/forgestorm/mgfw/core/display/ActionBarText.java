package com.forgestorm.mgfw.core.display;

import org.bukkit.entity.Player;

import io.puharesource.mc.titlemanager.api.ActionbarTitleObject;

public class ActionBarText {
	
	/**
	 * Sends the player a action bar message.
	 * @param player The player who will get the action bar message.
	 * @param message The message to send to the players action bar.
	 */
	public void sendActionbarMessage(Player player, String message) {
		new ActionbarTitleObject(message).send(player);
	}
}
