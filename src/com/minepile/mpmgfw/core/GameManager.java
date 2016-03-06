package com.minepile.mpmgfw.core;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.minepile.mpmgfw.MPMGFramework;
import com.minepile.mpmgfw.core.constants.GameState;
import com.minepile.mpmgfw.core.kits.KitSelector;
import com.minepile.mpmgfw.profiles.PlayerProfile;

import net.md_5.bungee.api.ChatColor;

public class GameManager {

	private final MPMGFramework PLUGIN;
	private final int MIN_PLAYERS = 1;
	private final KitSelector KIT_SELECTOR;

	private GameState gameState;
	private int gamesPlayed;
	private HashMap<Player, PlayerProfile> playerProfile;

	public GameManager(MPMGFramework plugin) {
		PLUGIN = plugin;
		KIT_SELECTOR = new KitSelector(PLUGIN);
		playerProfile = new HashMap<Player, PlayerProfile>();

		//On first load, lets begin with setting up the game.
		setupGame();
	}

	/**
	 * This will load the minigame plugin and setup the game world.
	 */
	private void setupGame() {
		MinigamePluginManager mpm = PLUGIN.getMinigamePluginManager();
		GameArena gameArena = PLUGIN.getGameArena();
		
		//Set game state.
		gameState = GameState.SETUP_GAME;

		//Load game plugin.
		mpm.loadNextGamePlugin();

		//Load game world.
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

		//Setup lobby kits.
		KIT_SELECTOR.spawnKits();

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

		//TODO: Give kit
		//TODO: Do team spawn
		//TODO: Show Scoreboard
		//TODO: Bossbar Announcer
		//TODO: Show Game Description

		//Let the plugin know to start the game.
		PLUGIN.getMinigamePluginManager().getMinigameBase().startGame();

		//Start the async check to see if the game is over.
		pollForGameOver();

	}

	/**
	 * The game has ended, so now we will show scores and do some cleanup.
	 * This will also unload the current minigame plugin.
	 */
	public void endGame(boolean setupNextGame) {
		GameArena gameArena = PLUGIN.getGameArena();
		GameLobby gameLobby = PLUGIN.getGameLobby();
		MinigamePluginManager mpm = PLUGIN.getMinigamePluginManager();

		gameState = GameState.GAME_ENDING;

		//If players are still online, lets show them the game scores.
		if (Bukkit.getOnlinePlayers().size() > 0) {
			//TODO: Show scores
		}

		//TODO: Save Scores (MySQL)
		
		//Remove the kits from the lobby world.
		KIT_SELECTOR.removeKits();
		
		//Unload the game plugin.
		mpm.disableCurrentGamePlugin();
		
		//Pull the players out of the arena map so it can be unloaded.
		gameLobby.setupLobbyPlayers(playerProfile);
		
		//Unload the game world.
		gameArena.unloadGameWorld();
		
		//Increment the number of games played.
		// gamesPlayed++; ???
		setGamesPlayed(getGamesPlayed() + 1);

		//Setup the next game.
		if (setupNextGame) {
			setupGame();
		}
	}

	/**
	 * This will start the game countdown.
	 */
	public void startCountdown() {
		gameState = GameState.LOBBY_COUNTDOWN;
		
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
					if (isMinimumPlayersMet() && gameState.equals(GameState.LOBBY_COUNTDOWN)) {
						Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Starting Game");
						startGame();
					} else {
						//Set game state.
						gameState = GameState.LOBBY_WAITING;
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
				boolean isGameOver = mgpm.getMinigameBase().isGameOver();

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
		return Bukkit.getOnlinePlayers().size() == 0;
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

	public int getGamesPlayed() {
		return gamesPlayed;
	}

	public void setGamesPlayed(int gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}

	public HashMap<Player, PlayerProfile> getPlayerProfile() {
		return playerProfile;
	}
}
