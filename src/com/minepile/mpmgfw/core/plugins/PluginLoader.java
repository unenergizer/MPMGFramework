package com.minepile.mpmgfw.core.plugins;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;

public class PluginLoader {

	private Plugin loadedGamePlugin;

	/**
	 * This method will load a plugin from file.
	 * The file that gets loaded should be a game plugin.
	 * Warning: You can only load one game plugin at a time!
	 * 
	 * @param file The plugin to load.
	 */
	public void loadPlugin(File file) {
		PluginManager pm = Bukkit.getPluginManager();
		Plugin gamePlugin = null;

		//Load the game plugin.
		try {
			gamePlugin = pm.loadPlugin(file);
		} catch (UnknownDependencyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPluginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidDescriptionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Enable the plugin
		pm.getPlugins();
		pm.enablePlugin(gamePlugin);
		loadedGamePlugin = gamePlugin;
	}

	/**
	 * This method will disable the currently loaded game plugin.
	 */
	public void disablePlugin() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.disablePlugin(loadedGamePlugin);
		loadedGamePlugin = null;
	}

	/**
	 * Gets the currently loaded minigame plugin.
	 * @return
	 */
	public Plugin getLoadedGamePlugin() {
		return loadedGamePlugin;
	}
}
