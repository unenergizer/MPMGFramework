package com.forgestorm.mgfw.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.forgestorm.mgfw.MGFramework;

public class GetMinigameKits {
	
	private final MGFramework PLUGIN = (MGFramework) Bukkit.getPluginManager().getPlugin("FSMG-Framework");

	/**
	 * This is used to get the kit a player is using.
	 * @param player The player who we are getting kit info for.
	 * @return Returns the index at which the kit is at.
	 */
	public int getPlayerKit(Player player) {
		return PLUGIN.getGameManager().getKitSelector().getPlayerKit(player);
	}
}
