package com.forgestorm.mgfw.api;

import org.bukkit.entity.Player;

import com.forgestorm.mgfw.MGFramework;

public class GetMinigameKits {
	
	private MGFramework PLUGIN;
	
	public GetMinigameKits(MGFramework plugin) {
		PLUGIN = plugin;
	}
	
	/**
	 * This is used to get the kit a player is using.
	 * @param player The player who we are getting kit info for.
	 * @return Returns the index at which the kit is at.
	 */
	public int getPlayerKit(Player player) {
		return PLUGIN.getGameManager().getKitSelector().getPlayerKit(player);
	}
}
