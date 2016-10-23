package com.forgestorm.mgfw.api;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.forgestorm.mgfw.MGFramework;

public class ScoreboardUpdater {
	
	private final MGFramework PLUGIN = (MGFramework) Bukkit.getPluginManager().getPlugin("FSMG-Framework");

	/**
	 * This will send the ArenaScoreboard the list of player scores.
	 * @param scoreMap
	 */
	public void sendScoreboardUpdates(HashMap<Player, Integer> scoreMap) {
		PLUGIN.getGameManager().getGameArena().getScoreboard().setScores(scoreMap);
	}
}
