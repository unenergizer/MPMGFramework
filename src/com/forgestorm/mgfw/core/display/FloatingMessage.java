package com.forgestorm.mgfw.core.display;

import org.bukkit.entity.Player;

import io.puharesource.mc.titlemanager.api.TitleObject;

public class FloatingMessage {
	
	/**
	 * Sends the player a floating message.
	 * @param player The player who will get the floating message.
	 * @param title The top (big) message to send to the player.
	 * @param subtitle The bottom (little) message to send the the player.
	 */
	public void sendFloatingMessage(Player player, String title, String subtitle) {
		new TitleObject(title, subtitle).send(player);
	}
}
