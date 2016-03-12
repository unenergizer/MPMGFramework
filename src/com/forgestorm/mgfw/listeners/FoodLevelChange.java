package com.forgestorm.mgfw.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import com.forgestorm.mgfw.MGFramework;

public class FoodLevelChange implements Listener {

	private final MGFramework PLUGIN;
	
	public FoodLevelChange(MGFramework plugin) {
		PLUGIN = plugin;
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		boolean isRunning = PLUGIN.getGameManager().isMinigameRunning();
		
		//Cancel food level change in the minigame lobby.
		if(!isRunning) {
			event.setCancelled(true);
		}
	}
}
