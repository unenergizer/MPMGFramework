package com.forgestorm.mgfw.api;

import java.util.HashMap;

import org.bukkit.entity.Player;

import com.forgestorm.mgfw.MGFramework;

public class EndGame {
	
	private final MGFramework PLUGIN;
	
	public EndGame(MGFramework plugin) {
		PLUGIN = plugin;
	}
	
	/**
	 * This will toggle the end of the game in the framework.
	 */
	public void toggleEndGame(HashMap<Player, Integer> scoreMap) {
		PLUGIN.getGameManager().showEndGameResults(scoreMap);
	}
}
