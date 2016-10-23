package com.forgestorm.mgfw.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.GameManager;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlayerDropItem implements Listener {

	private MGFramework PLUGIN;	

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		GameManager gameManager = PLUGIN.getGameManager();
		boolean isRunning = gameManager.isMinigameRunning();
		
		//Stop the player from dropping Lobby items.
		if (!isRunning) {
			event.setCancelled(true);
		}
	}
}
