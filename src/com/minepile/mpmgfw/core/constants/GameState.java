package com.minepile.mpmgfw.core.constants;

public enum GameState {
	lOBBY_RESETTING,	//Load external game plugin and assets
	LOBBY_WAITING,		//Wait for players and countdown timer
	GAME_STARTING,		//Game is starting, go to game world and give player game instructions.
	GAME_RUNNING,		//Game is running.
	GAME_ENDING			//Game is over, show player scores. Go back to lobby world.
}
