package com.forgestorm.mgfw.core;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.constants.GameState;
import com.forgestorm.mgfw.core.constants.Messages;
import com.forgestorm.mgfw.core.display.TipAnnouncer;
import com.forgestorm.mgfw.selector.KitSelector;
import com.forgestorm.mgfw.selector.TeamSelector;
import com.forgestorm.servercore.core.display.ActionBarText;

import lombok.Getter;
import lombok.Setter;

@Getter
public class GameManager {

	private final MGFramework PLUGIN;
	
	private GameArena gameArena;
	private GameLobby gameLobby;	
	private KitSelector kitSelector;
	private TeamSelector teamSelector;
	private TipAnnouncer tips;
	
	@Setter
	private GameState gameState;
	
	@Setter
	private int gamesPlayed, minPlayers, maxPlayers;

	public GameManager(MGFramework plugin) {
		PLUGIN = plugin;
		minPlayers = 2;
		maxPlayers = 16;

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
		
		//Create new Game Arena
		gameArena = new GameArena(PLUGIN);
		
		//Load game world.
		gameArena.loadGameWorld();

		//Setup game lobby.
		setupLobby();
	}

	/**
	 * This will setup our game lobby.
	 */
	private void setupLobby() {

		//Set game state.
		gameState = GameState.SETUP_LOBBY;
		
		gameLobby = new GameLobby(PLUGIN);

		//Setup lobby world
		gameLobby.loadLobbyWorld();

		//Setup the lobby scoreboard.
		gameLobby.getScoreboard().createScoreboard();
		
		kitSelector = new KitSelector(PLUGIN, this);
		
		//Setup lobby kits.
		kitSelector.spawnKitEntities();

		//Setup lobby teams.
		teamSelector = new TeamSelector(PLUGIN, this);
		teamSelector.spawnTeamsEntities();
		teamSelector.createHolograms();
		teamSelector.assignAllPlayerTeams();

		//Setup all the players currently in the lobby.
		gameLobby.setupAllLobbyPlayers();

		//Unhide players
		gameLobby.showHiddenPlayers();

		//Setup rotating game tips.
		tips = new TipAnnouncer(PLUGIN);
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
	public void startGame() {

		//Set game state.
		gameState = GameState.GAME_STARTING;

		//Remove lobby players
		gameLobby.removeAllLobbyPlayers();

		//Destroy the lobby scoreboard.
		gameLobby.getScoreboard().destroyScoreboard();

		//Setup the lobby scoreboard.
		gameArena.getScoreboard().createScoreboard();

		//Stop game tips from displaying.
		tips.setShowTips(false);

		//Setup all of the arena players.
		gameArena.setupAllArenaPlayers();

		//Show game description and rules.
		gameArena.showGameRules();

		//Let the plugin know to start the game.
		PLUGIN.getMinigamePluginManager().getMinigameBase().startGame();

		//Start the async check to see if the game is over.
		//pollForGameOver();

	}

	/**
	 * The game has ended, so now we will show scores and do some cleanup.
	 * This will also unload the current minigame plugin.
	 * @param setupNextGame Set to true, to load the next game.
	 */
	public void endGame(boolean setupNextGame) {
		MinigamePluginManager mpm = PLUGIN.getMinigamePluginManager();

		gameState = GameState.GAME_ENDING;

		//If players are still online, lets show them the game scores.
		if (Bukkit.getOnlinePlayers().size() > 0) {
			//TODO: Show scores & MOVE TO GAME ARENA
		}

		//TODO: Save Scores (MySQL)

		//Remove the kits from the lobby world.
		kitSelector.removeKits();

		//Remove the teams from the lobby world.
		teamSelector.removeTeams();

		//Destroy the lobby scoreboard.
		//gameArena.getScoreboard().destroyScoreboard();

		//Remove spectators
		gameArena.removeAllSpectators();

		//Unload the game plugin.
		mpm.disableCurrentGamePlugin();

		//Pull the players out of the arena map so it can be unloaded.
		gameLobby.tpAllToLobbySpawn();

		//Unload the game world.
		gameArena.unloadGameWorld();

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
				boolean allTeamsHavePlayers = getTeamSelector().allTeamsHavePlayers();

				if (allTeamsHavePlayers) {
					//If the minimum number of players is met, lets do a countdown!
					if (isMinimumPlayersMet() && gameState.equals(GameState.LOBBY_COUNTDOWN)) {

						//Show countdown in chat.
						if (countdown == 30 || countdown == 20 || countdown == 10 || countdown <= 5 && countdown > 1) {
							ActionBarText abt = new ActionBarText();
							String message = Messages.GAME_TIME_REMAINING_PLURAL.toString();

							for (Player players: Bukkit.getOnlinePlayers()) {
								abt.sendActionbarMessage(players, message.replace("%s", Integer.toString(countdown)));
								players.playSound(players.getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 1f);
							}
						}

						//Show the 1 second left countdown message.
						if (countdown == 1) {
							cancel();

							ActionBarText abt = new ActionBarText();
							String message = Messages.GAME_TIME_REMAINING_SINGULAR.toString();

							for (Player players: Bukkit.getOnlinePlayers()) {
								abt.sendActionbarMessage(players, message);
								players.playSound(players.getLocation(), Sound.BLOCK_NOTE_HARP, 1f, 1f);
							}

							//Do one last check to make sure the game should start.
							if (isMinimumPlayersMet() && gameState.equals(GameState.LOBBY_COUNTDOWN)) {
								startGame();
							}
						}
					} else { //Not enough players! Someone left, so let's cancel the countdown and reset everything.
						cancel();

						//Set game state.
						gameState = GameState.LOBBY_WAITING;

						//Show action bar message.
						ActionBarText abt = new ActionBarText();
						String message = Messages.GAME_COUNTDOWN_NOT_ENOUGH_PLAYERS.toString();

						for (Player players: Bukkit.getOnlinePlayers()) {
							//Show message
							abt.sendActionbarMessage(players, message);
							//Play notification sound.
							players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BASS, 1F, .5F);
						}
					}
				} else {
					cancel();

					//Set game state.
					gameState = GameState.LOBBY_WAITING;

					//Show action bar message.
					ActionBarText abt = new ActionBarText();
					String message = Messages.GAME_COUNTDOWN_ALL_TEAMS_NEED_PLAYERS.toString();

					for (Player players: Bukkit.getOnlinePlayers()) {
						//Show message
						abt.sendActionbarMessage(players, message);
						//Play notification sound.
						players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BASS, 1F, .5F);
					}
				}

				countdown--;
			}
		}.runTaskTimer(PLUGIN, 20, 20);
	}

	/**
	 * This will show the end game results, then toggle the endGame(true) method.
	 */
	public void showEndGameResults(HashMap<Player, Integer> scoreMap) {
		//Show games scores.
		//This method will envoke the endGame(true) method of this class.
		//Make sure we only show the scores if the game is running. This is an "/admin endgame" command bug fix.
		if (gameState.equals(GameState.GAME_RUNNING)) {
			gameArena.showGameScores(scoreMap);
		}
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
		boolean allTeamsHavePlayers = getTeamSelector().allTeamsHavePlayers();
		return isMinimumPlayersMet() && gameState.equals(GameState.LOBBY_WAITING) && allTeamsHavePlayers;
	}

	/**
	 * Figures out if the game should end.
	 * @return This will figure out if the game should end.
	 */
	public boolean shouldMinigameEnd() {
		int playersOnline = Bukkit.getOnlinePlayers().size();
		int spectatorsOnline = 0;

		//Get spectator count.
		for (Player players: Bukkit.getOnlinePlayers()) {
			boolean isSpectator = PLUGIN.getProfile(players).isSpectator();

			if (isSpectator) {
				spectatorsOnline++;
			}
		}

		//Test if game should end.
		if (playersOnline == 0 || playersOnline == spectatorsOnline) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Tests whether or not the game plugin is running.
	 * @return True if the game is currently running.
	 */
	public boolean isMinigameRunning() {
		switch (getGameState()) {
		case ARENA_SHOW_RULES:
		case ARENA_SHOW_SCORES:
		case GAME_ENDING:
		case GAME_RUNNING:
		case GAME_STARTING:
			return true;
		default:
			return false;
		}
	}
}
