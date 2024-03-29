package com.forgestorm.mgfw.api;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public abstract class MinigameBase {
	private final String minigameName;
	private final String arenaWorldName;
	private final String lobbyWorldName;
	private boolean isGameOver;

	public MinigameBase(final String minigameName, final String arenaWorldName, final String lobbyWorldName, boolean isGameOver) {
		this.minigameName = minigameName;
		this.arenaWorldName = arenaWorldName;
		this.lobbyWorldName = lobbyWorldName;
		this.isGameOver = false;
	}
	
	/**
	 * Tell the minigame plugin to initialize any needed variables.
	 */
	public abstract void initialize();
	
	/**
	 * Tell the minigame plugin to start game play.
	 */
	public abstract void startGame();
	
	/**
	 * This will execute when a game should end.
	 */
	public abstract void endGame();

	/**
	 * This will execute when a player quits the server.
	 */
	public abstract void onPlayerQuit(Player player);
	
	/**
	 * This will delegate if the minigame plugin will override the spectator spawning code.
	 * @return Returns true if the plugin will overide the frameworks spectator code.
	 */
	public abstract boolean isOverrideSpectator();
	
	/**
	 * Gets a list of minigame tips to show to the players while in the lobby world.
	 * @return Returns an array list of strings.
	 */
	public abstract ArrayList<String> getTips();
	
	/**
	 * Gets a list of minigame rules to show when the player is spawned in the arena world.
	 * <p>
	 * Their should be no more than 5 lines.
	 * @return Returns an array list of strings.
	 */
	public abstract ArrayList<String> getGameRules();

	/**
	 * Gets the player friendly name of the current minigame.
	 * @return The minigame name.
	 */
	public String getMinigameName() {
		return minigameName;
	}

	/**
	 * Gets the game world (arena) the player will play in.
	 * @return	The name of the game world.
	 */
	public String getArenaWorldName() {
		return arenaWorldName;
	}

	/**
	 * Gets the lobby world the player will select kits and teams in.
	 * @return	The name of the game world.
	 */
	public String getLobbyWorldName() {
		return lobbyWorldName;
	}

	/**
	 * Test to see if the minigame is over.
	 * @return Returns true if the game is over.
	 */
	public boolean isGameOver() {
		return isGameOver;
	}

	/**
	 * Sets the isGameOver boolean variable to true or false.
	 * 
	 * @param isGameOver Set to true to end the game.
	 */
	public void setGameOver(boolean isGameOver) {
		this.isGameOver = isGameOver;
	}
}
