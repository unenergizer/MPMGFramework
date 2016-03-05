package com.minepile.mpmgfw.api;

public abstract class Minigame {
    protected final String name;
    protected final String worldName;
    private boolean isGameOver;

    public Minigame(final String name, final String worldName, boolean isGameOver) {
        this.name = name;
        this.worldName = worldName;
        this.isGameOver = false;
    }
    
    public abstract void startGame();

    public String getName() {
        return name;
    }

    public String getWorldName() {
        return worldName;
    }

	public boolean isGameOver() {
		return isGameOver;
	}

	public void setGameOver(boolean isGameOver) {
		this.isGameOver = isGameOver;
	}
}
