package com.minepile.mpmgfw.core;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import com.minepile.mpmgfw.MPMGFramework;
import com.minepile.mpmgfw.core.constants.GameState;
import com.minepile.mpmgfw.core.plugins.PluginFinder;
import com.minepile.mpmgfw.core.plugins.PluginLoader;

import net.md_5.bungee.api.ChatColor;

public class GameManager {
	
	private GameState gameState;
	private MiniGame miniGame;
	
	private ArrayList<File> gamePlugins;
	private Integer gamePluginIndex;
	
	private final MPMGFramework PLUGIN;
	private final PluginLoader PLUGIN_LOADER;
	//private final int MAX_PLAYERS = 16;
	private final int MIN_PLAYERS = 1;
	private final String FILE_PATH = "plugins/MPMGFramework/games";
	private final Location lobbySpawn;
	
	public GameManager(MPMGFramework plugin) {
		this.PLUGIN = plugin;
		gameState = GameState.lOBBY_RESETTING;
		PLUGIN_LOADER = new PluginLoader();
		lobbySpawn = new Location(Bukkit.getWorlds().get(0), 0.5, 76, 0.5);
		
		//If first start, load a new game!
		loadNextGamePlugin();
	}
	
	/**
	 * Loads the next plugin in the gamePlugins array list.
	 */
	private void loadNextGamePlugin() {
		//Find game plugins.
		if (gamePlugins == null) {
			PluginFinder pf = new PluginFinder(FILE_PATH);
			gamePlugins = pf.getPlugins();
		}
		
		//Track which game to pick next.
		if (gamePluginIndex == null) {
			//This is going to be the first game we load.
			//Pick the first game from the gamePlugins list.
			gamePluginIndex = 0;
			
		} else {
			//Pick the next game from the gamePlugins list.
			gamePluginIndex++;
			
			//Make sure we can not go out of bounds.
			if(gamePluginIndex > gamePlugins.size()) {
				gamePluginIndex = 0;
			}
		}
		
		//Load the game plugin.
		PLUGIN_LOADER.loadPlugin(gamePlugins.get(gamePluginIndex));
		
		//The game plugin is loaded. Lets begin waiting on players.
		gameState = GameState.LOBBY_WAITING;
	}
	
	/**
	 * This will disable the last game plugin that was loaded.
	 */
	private void disableLastGamePlugin() {
		PLUGIN_LOADER.disablePlugin();
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

                    if (gameState == GameState.GAME_STARTING && isMinimumMet()) {
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
		
		//Teleport Players
		for(Player player: Bukkit.getOnlinePlayers()) {
			player.teleport(new Location(miniGame.getWorldDupe().getMiniGameWorld(), 0, 90, 0));
		}
		
		//Set the game state as now running.
		gameState = GameState.GAME_RUNNING;
	}
	
	/**
	 * This will end the current game plugin.
	 * 
	 */
	public void endGame() {
		//Do closing game tasks.
		
		//Finally disable the loaded game plugin.
		disableLastGamePlugin();
	}
	
	/**
	 * Does the lobby have the minimal amount of players.
	 * @return True if the minimal amount of players exist.
	 */
	public boolean isMinimumMet() {
		return (Bukkit.getOnlinePlayers().size() >= MIN_PLAYERS);
	}
	
	/**
	 * Figures out if the game should start.
	 * @return True if the game should start.
	 */
	public boolean shouldStart() {
		return (gameState.equals(GameState.LOBBY_WAITING) && isMinimumMet());
	}
	
	/**
	 * Figures out if the game should end.
	 * @return This will figure out if the game should end.
	 */
	public boolean shouldEnd() {
		return Bukkit.getOnlinePlayers().size() < 1;
	}
	
	/**
	 * Tests whether or not the game plugin is running.
	 * @return True if the game is currently running.
	 */
	public boolean isGameRunning() {
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
	
	/**
	 * Gets the main lobby's spawn point!
	 * @return A location with the lobby's spawn point.
	 */
	public Location getLobbySpawn() {
		return lobbySpawn;
	}
}
