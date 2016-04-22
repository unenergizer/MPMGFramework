package com.forgestorm.mgfw.api;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.forgestorm.mgfw.MGFramework;

public class GetMinigameTeams {
	
	private final MGFramework PLUGIN = (MGFramework) Bukkit.getPluginManager().getPlugin("MPMG-Framework");
	
	/**
	 * This will return a list of sorted teams to the minigame plugins.
	 * @return Returns a concurrent map of teams and players on each team for the minigame plugins.
	 */
	public ConcurrentMap<Integer, ArrayList<Player>> getSortedTeams() {
		return PLUGIN.getGameManager().getTeamSelector().getSortedTeams();
	}
}
