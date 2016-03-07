package com.minepile.mpmgfw.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
		Player player = event.getPlayer();
		
		//Remove the player from the players hashmap.
		gameManager.getPlayerProfile().remove(player);
		
		gameManager.getBar().removeBossBar(player);
		
		//Check to see if the game should end.
		if (gameManager.shouldMinigameEnd()) {

			//End the game!
			gameManager.endGame(true);
			
			Bukkit.getLogger().warning("[MPMGFramework] Ending game early. No players online!");
		}
	}
}
