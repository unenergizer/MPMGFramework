package com.forgestorm.mgfw.api;

public interface MinigamePlugin {
	
	/**
	 * Gets the minigame base instance.
	 * @return Returns the minigame base instance.
	 */
	MinigameBase getMinigameBase();
	
	/**
	 * Gets the minigame kits instance.
	 * @return Returns the minigame kits instance.
	 */
	MinigameKits getMinigameKits();
	
	/**
	 * Gets the minigame teams instance.
	 * @return Returns the minigame teams instance.
	 */
	MinigameTeams getMinigameTeams();
}
