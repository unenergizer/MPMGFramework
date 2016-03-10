package com.forgestorm.mgfw.core;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.plugin.Plugin;

import com.forgestorm.mgfw.api.MinigameBase;
import com.forgestorm.mgfw.api.MinigameKits;
import com.forgestorm.mgfw.api.MinigamePlugin;
import com.forgestorm.mgfw.api.MinigameTeams;
import com.forgestorm.mgfw.core.plugins.PluginFinder;
import com.forgestorm.mgfw.core.plugins.PluginLoader;

public class MinigamePluginManager {

	private final PluginLoader PLUGIN_LOADER;
	private final String FILE_PATH;

	private MinigameBase minigameBase;
	private MinigameKits minigameKit;
	private MinigameTeams minigameTeams;
	
	private Plugin loadedGamePlugin;
	private ArrayList<Plugin> gamePlugins;
	private ArrayList<File> gameFiles;
	private Integer gamePluginIndex;

	public MinigamePluginManager() {
		PLUGIN_LOADER = new PluginLoader();
		FILE_PATH = "plugins/MPMGFramework/games";
		setLoadedGamePlugin(null);
		gamePlugins = new ArrayList<Plugin>();
	}

	/**
	 * Loads the next plugin in the gamePlugins array list.
	 */
	public void loadNextGamePlugin() {
		//Find game plugins.
		if (gameFiles == null) {
			PluginFinder pf = new PluginFinder(FILE_PATH);
			gameFiles = pf.getPlugins();
			
			//Load all game plugins.
			for(File files: gameFiles) {
				
				//Load the game plugins and add it to the array list.
				gamePlugins.add(PLUGIN_LOADER.loadPlugin(files));
				
			}
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
			if(gamePluginIndex > gameFiles.size() - 1) {
				gamePluginIndex = 0;
			}
		}
		
		//Enable the next plugin.
		Plugin plugin = gamePlugins.get(gamePluginIndex);
		PLUGIN_LOADER.enablePlugin(plugin);
		loadedGamePlugin = plugin;

		//Try loading the plugin.
		setMinigamePluginInstance(loadedGamePlugin);
	}

	/**
	 * This will disable the current game plugin that is loaded.
	 */
	public void disableCurrentGamePlugin() {
		//Now disable the game plugin.
		PLUGIN_LOADER.disablePlugin(loadedGamePlugin);
	}

	/**
	 * This will get the game plugins instance for grabbing .
	 * 
	 * @param plugin The Minigame plugin that is currently loaded.
	 */
	private void setMinigamePluginInstance(final Plugin plugin) {

		if (plugin instanceof MinigamePlugin) {
			final MinigameBase minigameBase = ((MinigamePlugin) plugin).getMinigameBase();
			final MinigameKits minigameKit = ((MinigamePlugin) plugin).getMinigameKits();
			final MinigameTeams minigameTeams = ((MinigamePlugin) plugin).getMinigameTeams();

			this.minigameBase = minigameBase;
			this.minigameKit = minigameKit;
			this.minigameTeams = minigameTeams;
		}
	}

	/**
	 * Returns a instance of the currently loaded minigame.
	 * 
	 * @return	Returns a instance of the currently loaded minigame.
	 */
	public MinigameBase getMinigameBase() {
		return minigameBase;
	}

	/**
	 * Returns a instance of the currently loaded minigame kits.
	 * 
	 * @return	Returns a instance of the currently loaded minigame kits.
	 */
	public MinigameKits getMinigameKit() {
		return minigameKit;
	}

	/**
	 * Returns a instance of the currently loaded minigame teams.
	 * 
	 * @return	Returns a instance of the currently loaded minigame teams.
	 */
	public MinigameTeams getMinigameTeams() {
		return minigameTeams;
	}

	public Plugin getLoadedGamePlugin() {
		return loadedGamePlugin;
	}

	public void setLoadedGamePlugin(Plugin loadedGamePlugin) {
		this.loadedGamePlugin = loadedGamePlugin;
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
		if(gamePluginIndex > gameFiles.size() - 1) {
			return;
		} else {
			this.gamePluginIndex = gamePluginIndex;
		}
	}
}
