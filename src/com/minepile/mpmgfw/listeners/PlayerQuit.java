package com.minepile.mpmgfw.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.minepile.mpmgfw.MPMGFramework;
import com.minepile.mpmgfw.core.GameManager;

public class PlayerQuit implements Listener {
	
	private final MPMGFramework PLUGIN;
	
	public PlayerQuit(MPMGFramework plugin) {
		PLUGIN = plugin;
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		
		GameManager gameManager = PLUGIN.getGameManager();
		
		if (gameManager.shouldEnd()) {
			//End the game!
			gameManager.endGame();
		}
	}
}
