package com.forgestorm.mgfw.core;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.constants.GameState;
import com.forgestorm.mgfw.core.constants.Messages;
import com.forgestorm.mgfw.core.display.ActionBarText;
import com.forgestorm.mgfw.core.display.TipAnnouncer;
import com.forgestorm.mgfw.core.kits.KitSelector;
import com.forgestorm.mgfw.core.teams.TeamSelector;

public class GameManager {

	private final MGFramework PLUGIN;
	private final GameArena GAME_ARENA;
	private final GameLobby GAME_LOBBY;
	private final KitSelector KIT_SELECTOR;
	private final TeamSelector TEAM_SELECTOR;

	private GameState gameState;
	private TipAnnouncer tips;
	private int gamesPlayed;
	private int minPlayers = 2;
	private int maxPlayers = 16;

	public GameManager(MGFramework plugin) {
		PLUGIN = plugin;
		GAME_ARENA = PLUGIN.getGameArena();
		GAME_LOBBY = PLUGIN.getGameLobby();
		KIT_SELECTOR = new KitSelector(PLUGIN);
		TEAM_SELECTOR = new TeamSelector(PLUGIN);
		tips = new TipAnnouncer(PLUGIN);

		//On first load, lets begin with setting up the game.
		setupGame();
	}

	/**
	 * This will load the minigame plugin and setup the game world.
	 */
	private void setupGame() {
		MinigamePluginManager mpm = PLUGIN.getMinigamePluginManager();

		//Set game state.
		gameState = GameState.SETUP_GAME;

		//Load game plugin.
		mpm.loadNextGamePlugin();

		//Load game world.
		GAME_ARENA.loadGameWorld();

		//Setup game lobby.
		setupLobby();
	}

	/**
	 * This will setup our game lobby.
	 */
	private void setupLobby() {

		//Set game state.
		gameState = GameState.SETUP_LOBBY;

		//Setup lobby world
		GAME_LOBBY.loadLobbyWorld();

		//Setup lobby kits.
		KIT_SELECTOR.spawnKits();

		//Setup lobby teams.
		TEAM_SELECTOR.spawnTeams();
		
		//Setup all the players currently in the lobby.
		GAME_LOBBY.setupAllLobbyPlayers();

		//Setup rotating game tips.
		tips.startTipMessages(PLUGIN.getMinigamePluginManager().getMinigameBase().getTips());

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

		//Set game state.
		gameState = GameState.GAME_STARTING;

		//Remove lobby players
		GAME_LOBBY.removeAllLobbyPlayers();

		//Stop game tips from displaying.
		tips.setShowTips(false);

		//Get all players and teleport them to the game world.
		GAME_ARENA.tpAllToGameWorld();


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
	 * @param setupNextGame Set to true, to load the next game.
	 */
	public void endGame(boolean setupNextGame) {
		GameArena GAME_ARENA = PLUGIN.getGameArena();
		GameLobby GAME_LOBBY = PLUGIN.getGameLobby();
		MinigamePluginManager mpm = PLUGIN.getMinigamePluginManager();

		gameState = GameState.GAME_ENDING;

		//If players are still online, lets show them the game scores.
		if (Bukkit.getOnlinePlayers().size() > 0) {
			//TODO: Show scores & MOVE TO GAME ARENA
		}

		//TODO: Save Scores (MySQL)

		//Remove the kits from the lobby world.
		KIT_SELECTOR.removeKits();
		
		//Remove the teams from the lobby world.
		TEAM_SELECTOR.removeTeams();

		//Unload the game plugin.
		mpm.disableCurrentGamePlugin();
		
		//Pull the players out of the arena map so it can be unloaded.
		GAME_LOBBY.tpAllToLobbySpawn();
		
		//Unload the game world.
		GAME_ARENA.unloadGameWorld();

		//Increment the number of games played.
		// gamesPlayed++; ???
		setGamesPlayed(getGamesPlayed() + 1);

		//Setup the next game.
		if (setupNextGame) {
			setupGame();
		} else {
			Bukkit.getLogger().warning("[MPMGFramework] We will not setup the next game!");
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
					if (countdown == 30 || countdown == 20 || countdown == 10 || countdown <= 5 && countdown > 1) {
						ActionBarText abt = new ActionBarText();
						String message = Messages.GAME_TIME_REMAINING_PLURAL.toString();
						for (Player players: Bukkit.getOnlinePlayers()) {
							abt.sendActionbarMessage(players, message.replace("%s", Integer.toString(countdown)));
							players.playSound(players.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
						}
					}
					
					if (countdown == 1) {
						ActionBarText abt = new ActionBarText();
						String message = Messages.GAME_TIME_REMAINING_SINGULAR.toString();
						for (Player players: Bukkit.getOnlinePlayers()) {
							abt.sendActionbarMessage(players, message);
							players.playSound(players.getLocation(), Sound.BLOCK_NOTE_HARP, 1f, 1f);
						}
					}

					countdown--;
				} else {
					cancel();

					//Do one last check to make sure the game should start.
					if (isMinimumPlayersMet() && gameState.equals(GameState.LOBBY_COUNTDOWN)) {
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
		return (Bukkit.getOnlinePlayers().size() >= minPlayers);
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
	 * Gets the maximum number of players this game arcade can support.
	 * 
	 * @return Retuns the maximum number of players the game arcade can hold.
	 */
	public int getMAX_PLAYERS() {
		return maxPlayers;
	}
	
	/**
	 * Gets an instance of the KitSelector class.
	 * @return Returns an instance of the KitSelector class.
	 */
	public KitSelector getKitSelector() {
		return KIT_SELECTOR;
	}

	/**
	 * Gets an instance of the KitSelector class.
	 * @return Returns an instance of the KitSelector class.
	 */
	public TeamSelector getTeamSelector() {
		return TEAM_SELECTOR;
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

	public int getMinPlayers() {
		return minPlayers;
	}

	public void setMinPlayers(int minPlayers) {
		this.minPlayers = minPlayers;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}
}
