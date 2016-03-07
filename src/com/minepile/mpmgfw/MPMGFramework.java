package com.minepile.mpmgfw;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.minepile.mpmgfw.core.GameArena;
import com.minepile.mpmgfw.core.GameLobby;
import com.minepile.mpmgfw.core.GameManager;
import com.minepile.mpmgfw.core.MinigamePluginManager;
import com.minepile.mpmgfw.listeners.EntityCombust;
import com.minepile.mpmgfw.listeners.EntityDamage;
import com.minepile.mpmgfw.listeners.EntityDamageByEntity;
import com.minepile.mpmgfw.listeners.PlayerInteractEntity;
import com.minepile.mpmgfw.listeners.PlayerJoin;
import com.minepile.mpmgfw.listeners.PlayerQuit;
import com.minepile.mpmgfw.listeners.WeatherChange;

public class MPMGFramework extends JavaPlugin {

	private GameManager gameManager;
	private MinigamePluginManager minigamePluginManager;
	private GameLobby lobby;
	private GameArena arena;

	@Override
	public void onEnable() {

		minigamePluginManager = new MinigamePluginManager();
		lobby = new GameLobby();
		arena = new GameArena(this);
		gameManager = new GameManager(this);

		//Register Event listeners
		registerListeners();
	}

	@Override 
	public void onDisable() {
	}

	/**
	 * Register Spigot/Bukkit event listeners.
	 */
	private void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();

		pm.registerEvents(new EntityCombust(), this);
		pm.registerEvents(new EntityDamage(this), this);
		pm.registerEvents(new EntityDamageByEntity(this), this);
		pm.registerEvents(new PlayerInteractEntity(this), this);
		pm.registerEvents(new PlayerJoin(this), this);
		pm.registerEvents(new PlayerQuit(this), this);
		pm.registerEvents(new WeatherChange(this), this);
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


	/**
	 * Grabs the LobbyManager instance.
	 * @return Returns a LobbyManager instance.
	 */
	public GameLobby getGameLobby() {
		return lobby;
	}


	/**
	 * Grabs the ArenaManager instance.
	 * @return Returns a ArenaManager instance.
	 */
	public GameArena getGameArena() {
		return arena;
	}
}
