package com.forgestorm.mgfw.core.plugins;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;

public class PluginLoader {

	/**
	 * This method will load a plugin from file.
	 * The file that gets loaded should be a game plugin.
	 * Warning: You can only load one game plugin at a time!
	 * 
	 * @param file The plugin to load.
	 * @return 
	 */
	public Plugin loadPlugin(File file) {
		PluginManager pm = Bukkit.getPluginManager();
		Plugin plugin = null;;
		
		//Load the game plugin.
		try {
			plugin = pm.loadPlugin(file);
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
		
		return plugin;
	}
	
	/**
	 * Enables a game plugin.
	 * 
	 * @param gamePlugin The game plugin to enable.
	 * @return Retuns the plugin that was just enabled.
	 */
	public Plugin enablePlugin(Plugin gamePlugin) {
		PluginManager pm = Bukkit.getPluginManager();
		
		//Enable the plugin
		pm.getPlugins();
		pm.enablePlugin(gamePlugin);
		return gamePlugin;
	}

	/**
	 * This method will disable the currently loaded game plugin.
	 */
	public boolean disablePlugin(Plugin gamePlugin) {
		if (gamePlugin != null) {
			PluginManager pm = Bukkit.getPluginManager();
			pm.disablePlugin(gamePlugin);
			return true;
		} else {
			//The game is not loaded?
			return false;
		}
	}
}
