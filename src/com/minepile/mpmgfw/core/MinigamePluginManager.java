package com.minepile.mpmgfw.core;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.plugin.Plugin;

import com.minepile.mpmgfw.MPMGFramework;
import com.minepile.mpmgfw.api.Minigame;
import com.minepile.mpmgfw.api.MinigamePlugin;
import com.minepile.mpmgfw.core.constants.GameState;
import com.minepile.mpmgfw.core.plugins.PluginFinder;
import com.minepile.mpmgfw.core.plugins.PluginLoader;

public class MinigamePluginManager {

	private final MPMGFramework PLUGIN;
	private final PluginLoader PLUGIN_LOADER;
	private final String FILE_PATH = "plugins/MPMGFramework/games";
	
	private Minigame minigame;
	private ArrayList<File> gamePlugins;
	private Integer gamePluginIndex;
	
	public MinigamePluginManager(MPMGFramework plugin) {
		PLUGIN = plugin;
		PLUGIN_LOADER = new PluginLoader();
		
		//On first load, load 
		loadNextGamePlugin();
	}
	
	/**
	 * Loads the next plugin in the gamePlugins array list.
	 */
	public void loadNextGamePlugin() {
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
			
			//Make sure our array will not go out of bounds.
			if(gamePluginIndex > gamePlugins.size()) {
				gamePluginIndex = 0;
			}
		}
		
		//Load the game plugin.
		PLUGIN_LOADER.loadPlugin(gamePlugins.get(gamePluginIndex));
		
		//Try loading the plugin.
		tryLoad(PLUGIN_LOADER.getLoadedGamePlugin());
		
		//The game plugin is loaded. Lets begin waiting on players.
		PLUGIN.getGameManager().setGameState(GameState.LOBBY_WAITING);
	}
	
	/**
	 * This will try to load the API contents of the loaded plugin.
	 * 
	 * @param plugin The Minigame plugin that is currently loaded.
	 */
	public void tryLoad(final Plugin plugin) {
		
		if (plugin instanceof MinigamePlugin) {
            final Minigame minigamePlugin = ((MinigamePlugin) plugin).getMinigame();

            minigame = minigamePlugin;
        }
	}
	
	/**
	 * This will disable the current game plugin that is loaded.
	 */
	public void disableCurrentGamePlugin() {
		PLUGIN_LOADER.disablePlugin();
	}

	/**
	 * Returns a list of the currently loaded minigame.
	 * 
	 * @return	Returns a list of the currently loaded minigame.
	 */
	public Minigame getMinigame() {
		return minigame;
	}
	
	/**
	 * Gets the current game index of the total games loaded.
	 * <p>
	 * All game plugins (files) are loaded into an array list.
	 * Depending on the gamePluginIndex chosen, will mitigate
	 * what game will be loaded and played.
	 * 
	 * @return The index of the current game loaded.
	 */
	public Integer getGamePluginIndex() {
		return gamePluginIndex;
	}
	
	/**
	 * This will set the gamePluginIndex which will decide what
	 * game will be played next.  If an index above the array
	 * size is chosen, nothing will happen.  This is untested.
	 * 
	 * @param gamePluginIndex Sets the next game to be tested.
	 */
	public void setGamePluginIndex(Integer gamePluginIndex) {
		if(gamePluginIndex > gamePlugins.size()) {
			return;
		} else {
			this.gamePluginIndex = gamePluginIndex;
		}
	}
}
