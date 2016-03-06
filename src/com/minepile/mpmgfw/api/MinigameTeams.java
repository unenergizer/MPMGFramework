package com.minepile.mpmgfw.api;

import java.util.ArrayList;

public abstract class MinigameTeams {

	/**
	 * Gets the minigames team.
	 * <p>
	 * You can implement colors into the names by doing
	 * something like: 
	 * <p>	
	 * ChatColor.GREEN + "Green Team"
	 * ChatColor.RED + "Angry Red Team"
	 * 
	 * @return Gets the minigame teams.
	 */
	public abstract ArrayList<String> setMinigameTeams();
}
