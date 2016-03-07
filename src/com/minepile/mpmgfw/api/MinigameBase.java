package com.minepile.mpmgfw.api;

import java.util.ArrayList;

public abstract class MinigameBase {
	protected final String minigameName;
	protected final String arenaWorldName;
	protected final String lobbyWorldName;
	private boolean isGameOver;

	public MinigameBase(final String minigameName, final String arenaWorldName, final String lobbyWorldName, boolean isGameOver) {
		this.minigameName = minigameName;
		this.arenaWorldName = arenaWorldName;
		this.lobbyWorldName = lobbyWorldName;
		this.isGameOver = false;
	}
	
	/**
	 * Tell the minigame plugin to start execution.
	 */
	public abstract void startGame();

	
	/**
	 * Gets a list of minigame tips to show to the players while in the lobby world.
	 * @return Returns an array list of strings.
	 */
	public abstract ArrayList<String> getTips();

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
