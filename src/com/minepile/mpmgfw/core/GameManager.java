package com.minepile.mpmgfw.core;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.minepile.mpmgfw.MPMGFramework;
import com.minepile.mpmgfw.core.constants.GameState;

import net.md_5.bungee.api.ChatColor;

public class GameManager {

	private GameState gameState;

	private final MPMGFramework PLUGIN;
	private final int MIN_PLAYERS = 1;
	private final Location lobbySpawn;

	private String lobbyWorld = "mg-lobby";

	public GameManager(MPMGFramework plugin) {
		this.PLUGIN = plugin;
		gameState = GameState.lOBBY_RESETTING;
		lobbySpawn = new Location(Bukkit.getWorld(lobbyWorld), 0.5, 76, 0.5);

		//On first load, begin lobby setup.
		initLobby();
	}

	/**
	 * Initialize (setup) the minigame lobby.
	 */
	private void initLobby() {

		//Edit world properties.
		Bukkit.setSpawnRadius(0);
		World world = Bukkit.getWorlds().get(0);
		world.setSpawnFlags(false, false);
		world.setGameRuleValue("doMobSpawning", "false");

		//Remove entities from the world.
		for (Entity entity : world.getEntities()) {
			if (!(entity instanceof Player) && entity instanceof LivingEntity) {
				entity.remove();
			}
		}

		//Spawn players in the lobby.
		for (Player players: Bukkit.getOnlinePlayers()) {
			tpToLobbySpawn(players);
		}

		//TODO: Kit Selection
		//TODO: Team Selection
		//TODO: Scoreboard
		//TODO: Bossbar Announcer

		//The lobby is setup, lets change the game state.
		gameState = GameState.LOBBY_WAITING;
		
		//Check to see if the game should start.
		//Check needed for server reloads.
		if (shouldMinigameStart()) {
			startCountdown();
		}
	}

	/**
	 * Teleports a player to the lobby spawn pad.
	 * 
	 * @param player The player to teleport to lobby spawn.
	 */
	public void tpToLobbySpawn(Player player) {
		player.teleport(lobbySpawn);
	}

	/**
	 * This will start the game countdown.
	 */
	public void startCountdown() {
		gameState = GameState.GAME_STARTING;

		new BukkitRunnable() {
			int countdown = 30;

			@Override
			public void run() {
				if (countdown != 0) {
					if (countdown == 30 || countdown == 20 || countdown == 10 || countdown <= 5 && countdown > 0) {
						Bukkit.broadcastMessage(ChatColor.YELLOW + "Game will start in " + ChatColor.RED + countdown + ChatColor.YELLOW + " seconds.");
					}

					countdown--;
				} else {
					cancel();

					if (gameState == GameState.GAME_STARTING && isMinimumPlayersMet()) {
						startGame();
					} else {
						gameState = GameState.LOBBY_WAITING;
					}
				}
			}
		}.runTaskTimer(PLUGIN, 0, 20);
	}



	/**
	 * This will start the loaded plugin game.
	 */
	private void startGame() {
		//Debug Message
		Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + ">> START GAME <<");

		//Teleport players to the game world.
		for(Player players: Bukkit.getOnlinePlayers()) {
			World world = PLUGIN.getMinigamePluginManager().getWorldDupe().getMiniGameWorld();
			Location tempLoc = new Location(world, 0, 90, 0);

			players.teleport(tempLoc);
		}
		
		//Let the plugin know to start the game.
		PLUGIN.getMinigamePluginManager().getMinigame().startGame();
		
		//Start the async check to see if the game is over.
		pollForGameOver();
		
		//Set the game state as now running.
		gameState = GameState.GAME_RUNNING;
	}

	/**
	 * This will end the current game plugin.
	 * 
	 */
	public void endGame() {
		MinigamePluginManager mpm = PLUGIN.getMinigamePluginManager();
		
		//Do closing game tasks.
		gameState = GameState.GAME_ENDING;

		//Finally disable the loaded game plugin.
		mpm.disableCurrentGamePlugin();
		
		//Load the next minigame plugin.
		mpm.loadNextGamePlugin();
		
		//Game is done, setup the lobby again.
		initLobby();
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
		return (gameState.equals(GameState.LOBBY_WAITING) && isMinimumPlayersMet());
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
					endGame();
				}
			}
		}.runTaskTimer(PLUGIN, 0, 20);
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

	/**
	 * Gets the main lobby's spawn point!
	 * @return A location with the lobby's spawn point.
	 */
	public Location getLobbySpawn() {
		return lobbySpawn;
	}

	/**
	 * Gets the lobby's world name.
	 * @return The name of the lobby world.
	 */
	public String getLobbyWorld() {
		return lobbyWorld;
	}
}
