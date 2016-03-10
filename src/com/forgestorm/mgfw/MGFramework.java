package com.forgestorm.mgfw;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.forgestorm.mgfw.core.GameArena;
import com.forgestorm.mgfw.core.GameLobby;
import com.forgestorm.mgfw.core.GameManager;
import com.forgestorm.mgfw.core.MinigamePluginManager;
import com.forgestorm.mgfw.listeners.EntityCombust;
import com.forgestorm.mgfw.listeners.EntityDamage;
import com.forgestorm.mgfw.listeners.EntityDamageByEntity;
import com.forgestorm.mgfw.listeners.EntityTargetLivingEntity;
import com.forgestorm.mgfw.listeners.PlayerInteractEntity;
import com.forgestorm.mgfw.listeners.PlayerJoin;
import com.forgestorm.mgfw.listeners.PlayerQuit;
import com.forgestorm.mgfw.listeners.WeatherChange;

public class MGFramework extends JavaPlugin {

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
		gameManager.endGame(false);
	}

	/**
	 * Register Spigot/Bukkit event listeners.
	 */
	private void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();

		pm.registerEvents(new EntityCombust(), this);
		pm.registerEvents(new EntityDamage(this), this);
		pm.registerEvents(new EntityDamageByEntity(this), this);
		pm.registerEvents(new EntityTargetLivingEntity(this), this);
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