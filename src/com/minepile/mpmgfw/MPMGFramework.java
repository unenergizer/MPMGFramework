package com.minepile.mpmgfw;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.minepile.mpmgfw.core.GameManager;
import com.minepile.mpmgfw.core.MinigamePluginManager;
import com.minepile.mpmgfw.listeners.PlayerJoin;
import com.minepile.mpmgfw.listeners.PlayerQuit;

public class MPMGFramework extends JavaPlugin {
	
	private GameManager gameManager;
	private MinigamePluginManager minigamePluginManager;
	
	@Override
	public void onEnable() {
		
		gameManager = new GameManager(this);
		minigamePluginManager = new MinigamePluginManager(this);
		
        //Register Event listeners
        registerListeners();
	}
	
	@Override 
	public void onDisable() {
		//TODO
	}
	
	private void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayerJoin(this), this);
		pm.registerEvents(new PlayerQuit(this), this);
	}
	
	/**
	 * Grabs the GameManager instance.
	 * @return Returns a GameManager instance.
	 */
	public GameManager getGameManager() {
		return gameManager;
	}

	/**
	 * Grabs the MinigamePluginManager instance.
	 * @return Returns a MinigamePluginManager instance.
	 */
	public MinigamePluginManager getMinigamePluginManager() {
		return minigamePluginManager;
	}
}
