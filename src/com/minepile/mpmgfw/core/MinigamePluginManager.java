package com.minepile.mpmgfw.core;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.plugin.Plugin;

import com.minepile.mpmgfw.api.Minigame;
import com.minepile.mpmgfw.api.MinigamePlugin;
import com.minepile.mpmgfw.core.plugins.PluginFinder;
import com.minepile.mpmgfw.core.plugins.PluginLoader;

public class MinigamePluginManager {

	private final PluginLoader PLUGIN_LOADER;
	private final String FILE_PATH;

	private Minigame minigame;
	private ArrayList<File> gamePlugins;
	private Integer gamePluginIndex;

	public MinigamePluginManager() {
		PLUGIN_LOADER = new PluginLoader();
		FILE_PATH = "plugins/MPMGFramework/games";
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
			if(gamePluginIndex > gamePlugins.size() - 1) {
				gamePluginIndex = 0;
			}
		}

		//Load the game plugin.
		PLUGIN_LOADER.loadPlugin(gamePlugins.get(gamePluginIndex));

		//Try loading the plugin.
		setMinigamePluginInstance(PLUGIN_LOADER.getLoadedGamePlugin());
	}

	/**
	 * This will disable the current game plugin that is loaded.
	 */
	public void disableCurrentGamePlugin() {
		//Now disable the game plugin.
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
	 * This will get the game plugins instance for grabbing .
	 * 
	 * @param plugin The Minigame plugin that is currently loaded.
	 */
	private void setMinigamePluginInstance(final Plugin plugin) {

		if (plugin instanceof MinigamePlugin) {
			final Minigame minigamePlugin = ((MinigamePlugin) plugin).getMinigame();

			minigame = minigamePlugin;
		}
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
		if(gamePluginIndex > gamePlugins.size() - 1) {
			return;
		} else {
			this.gamePluginIndex = gamePluginIndex;
		}
	}
}
