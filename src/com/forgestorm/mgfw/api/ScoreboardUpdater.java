package com.forgestorm.mgfw.api;

import java.util.HashMap;

import org.bukkit.entity.Player;

import com.forgestorm.mgfw.MGFramework;

public class ScoreboardUpdater {

	private final MGFramework PLUGIN;

	public ScoreboardUpdater(MGFramework plugin) {
		PLUGIN = plugin;
	}

	/**
	 * This will send the ArenaScoreboard the list of player scores.
	 * @param scoreMap
	 */
	public void sendScoreboardUpdates(HashMap<Player, Integer> scoreMap) {
		PLUGIN.getGameManager().getGAME_ARENA().getScoreboard().setScores(scoreMap);
	}
}
