package com.forgestorm.mgfw.core.display;

import org.bukkit.entity.Player;

import io.puharesource.mc.titlemanager.api.TabTitleObject;

public class TabMenuText {

	
	/**
	 * Sends a player tab menu text.
	 * 
	 * @param player The player to send the updated tab menu to.
	 * @param header The message above the tab list.
	 * @param footer The message below the tab list.
	 */
	public void sendHeaderAndFooter(Player player, String header, String footer) {
		new TabTitleObject(header, footer).send(player);
	}
}
