package com.minepile.mpmgfw.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.minepile.mpmgfw.MPMGFramework;
import com.minepile.mpmgfw.core.constants.GameState;

import net.md_5.bungee.api.ChatColor;

public class GameManager {

	private final MPMGFramework PLUGIN;
	private final int MIN_PLAYERS = 1;

	private GameState gameState;
	private Integer gamesPlayed;

	public GameManager(MPMGFramework plugin) {
		PLUGIN = plugin;

		//On first load, lets begin with setting up the game.
		setupGame();
	}

	/**
	 * This will load the minigame plugin and setup the game world.
	 */
	private void setupGame() {
		//Set game state.
		gameState = GameState.SETUP_GAME;

		//Load game plugin.
		MinigamePluginManager mpm = PLUGIN.getMinigamePluginManager();
		mpm.loadNextGamePlugin();

		//Load game world.
		GameArena gameArena = PLUGIN.getGameArena();
		gameArena.loadGameWorld();

		//Setup game lobby.
		setupLobby();
	}

	/**
	 * This will setup our game lobby.
	 */
	private void setupLobby() {
		GameLobby gameLobby = PLUGIN.getGameLobby();

		//Set game state.
		gameState = GameState.SETUP_LOBBY;

		//Setup lobby world
		gameLobby.loadLobbyWorld();

		//Setup lobby players.
		for (Player players: Bukkit.getOnlinePlayers()) {
			//TODO: Setup lobby player profiles and items.
			
			//Get all players and teleport them to the lobby world.	
			gameLobby.tpToLobbySpawn(players);
		}

		//TODO: Kit Selection
		//TODO: Team Selection
		//TODO: Scoreboard
		//TODO: Bossbar Announcer


		//Set game state.
		gameState = GameState.LOBBY_WAITING;
		
		//Check if game needs to start.
		if (shouldMinigameStart()) {
			startCountdown();
		}
	}

	/**
	 * This will move players out of the lobby and into the game map.
	 * Thus starting the game.
	 */
	private void startGame() {
		GameArena gameArena = PLUGIN.getGameArena();

		//Set game state.
		gameState = GameState.GAME_STARTING;

		//Setup minigame players.
		for (Player players: Bukkit.getOnlinePlayers()) {
			//TODO: Setup player profiles and items for minigame.
			
			//Get all players and teleport them to the game world.
			gameArena.tpToGameWorld(players, 0, 90, 0);
		}

		//TODO: Kit Selection
		//TODO: Team Selection
		//TODO: Scoreboard
		//TODO: Bossbar Announcer
		//TODO: Show Game Description

		//Let the plugin know to start the game.
		PLUGIN.getMinigamePluginManager().getMinigame().startGame();

		//Start the async check to see if the game is over.
		pollForGameOver();

	}

	/**
	 * The game has ended, so now we will show scores and do some cleanup.
	 * This will also unload the current minigame plugin.
	 */
	public void endGame(boolean setupNextGame) {
		GameArena gameArena = PLUGIN.getGameArena();
		MinigamePluginManager mpm = PLUGIN.getMinigamePluginManager();

		gameState = GameState.GAME_ENDING;

		//If players are still online, lets show them the game scores.
		if (Bukkit.getOnlinePlayers().size() > 0) {
			//TODO: Show scores
		}

		//TODO: Save Scores (MySQL)

		//Unload the game world.
		gameArena.unloadGameWorld();

		//Unload the game plugin.
		mpm.disableCurrentGamePlugin();
		
		//Increment the number of games played.
		gamesPlayed++;

		//Setup the next game.
		if (setupNextGame) {
			setupGame();
		}
	}

	/**
	 * This will start the game countdown.
	 */
	public void startCountdown() {
		
		new BukkitRunnable() {
			int countdown = 30;

			@Override
			public void run() {
				if (countdown != 0) {
					
					//Show countdown in chat.
					if (countdown == 30 || countdown == 20 || countdown == 10 || countdown <= 5 && countdown > 0) {
						Bukkit.broadcastMessage(ChatColor.YELLOW + "Game will start in " + ChatColor.RED + countdown + ChatColor.YELLOW + " seconds.");
					}
					
					countdown--;
				} else {
					cancel();
					
					//Do one last check to make sure the game should start.
					if (shouldMinigameStart()) {
						Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Starting Game");
						startGame();
					}
				}
			}
		}.runTaskTimer(PLUGIN, 0, 20);
	}

	/**
	 * Continuously polls the loaded minigame to check if it has ended.
	 */
	public void pollForGameOver() {

		MinigamePluginManager mgpm = PLUGIN.getMinigamePluginManager();

		new BukkitRunnable() {

			@Override
			public void run() {
				boolean isGameOver = mgpm.getMinigame().isGameOver();

				//Continuously poll to see if the minigame is over.
				if (isGameOver) {
					//Stop the thread first.
					cancel();

					//Then end the thread.
					endGame(true);
				}
			}
		}.runTaskTimer(PLUGIN, 0, 20);
	}

	/**
	 * Does the lobby have the minimal amount of players.
	 * @return True if the minimal amount of players exist.
	 */
	public boolean isMinimumPlayersMet() {
		return (Bukkit.getOnlinePlayers().size() >= MIN_PLAYERS);
	}

	/**
	 * Figures out if the game should start.
	 * @return True if the game should start.
	 */
	public boolean shouldMinigameStart() {
		return isMinimumPlayersMet() && gameState.equals(GameState.LOBBY_WAITING);
	}

	/**
	 * Figures out if the game should end.
	 * @return This will figure out if the game should end.
	 */
	public boolean shouldMinigameEnd() {
		return Bukkit.getOnlinePlayers().size() < 1;
	}

	/**
	 * Tests whether or not the game plugin is running.
	 * @return True if the game is currently running.
	 */
	public boolean isMinigameRunning() {
		switch (getGameState()) {
		case GAME_ENDING:
		case GAME_RUNNING:
		case GAME_STARTING:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Gets the current state of the game manager.
	 * @return The state of the game manager.
	 */
	public GameState getGameState() {
		return gameState;
	}

	/**
	 * Sets the state of the game manager.
	 * @param state The GameState enum to set.
	 */
	public void setGameState(GameState state) {
		this.gameState = state;
	}
}
