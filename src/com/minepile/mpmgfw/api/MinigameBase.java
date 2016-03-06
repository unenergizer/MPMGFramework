package com.minepile.mpmgfw.api;

public abstract class MinigameBase {
	protected final String minigameName;
	protected final String worldName;
	private boolean isGameOver;

	public MinigameBase(final String minigameName, final String worldName, boolean isGameOver) {
		this.minigameName = minigameName;
		this.worldName = worldName;
		this.isGameOver = false;
	}
	
	/**
	 * Tell the minigame plugin to start execution.
	 */
	public abstract void startGame();

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
	public String getWorldName() {
		return worldName;
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
