package com.minepile.mpmgfw.core.constants;

public enum GameState {
	SETUP_GAME,			//The game plugin is being loaded.
	SETUP_LOBBY,		//The game lobby is being setup.
	LOBBY_WAITING,		//The game is ready to be played.
	LOBBY_COUNTDOWN,	//The lobby countdown has started.
	GAME_STARTING,		//The game countdown has started (still in lobby).
	GAME_RUNNING,		//The players have been moved to game world.
	GAME_ENDING			//The game is over. Unload the plugin.
}
