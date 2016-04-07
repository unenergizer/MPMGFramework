package com.forgestorm.mgfw.api;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.entity.Player;

import com.forgestorm.mgfw.MGFramework;

public class GetMinigameTeams {
	
	private MGFramework PLUGIN;
	
	public GetMinigameTeams(MGFramework plugin) {
		PLUGIN = plugin;
	}
	
	/**
	 * This will return a list of sorted teams to the minigame plugins.
	 * @return Returns a concurrent map of teams and players on each team for the minigame plugins.
	 */
	public ConcurrentMap<Integer, ArrayList<Player>> getSortedTeams() {
		return PLUGIN.getGameManager().getTeamSelector().getSortedTeams();
	}
}
