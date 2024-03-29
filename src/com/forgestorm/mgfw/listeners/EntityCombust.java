package com.forgestorm.mgfw.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;

import com.forgestorm.mgfw.MGFramework;

public class EntityCombust implements Listener {
	
	private MGFramework PLUGIN;
	
	public EntityCombust(MGFramework plugin) {
		PLUGIN = plugin;
	}
	
	@EventHandler
	public void onEntityCombust(EntityCombustEvent event) {
		boolean isRunning = PLUGIN.getGameManager().isMinigameRunning();

		if (!isRunning) {
			event.setCancelled(true);
		}
	}
}
