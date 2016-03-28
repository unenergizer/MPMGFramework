package com.forgestorm.mgfw.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import com.forgestorm.mgfw.MGFramework;

public class EntityTargetLivingEntity implements Listener{

	private final MGFramework PLUGIN;
	
	public EntityTargetLivingEntity(MGFramework plugin) {
		PLUGIN = plugin;
	}

	@EventHandler
	public void onEntityTarget(EntityTargetLivingEntityEvent event){
		boolean isRunning = PLUGIN.getGameManager().isMinigameRunning();

		if (!isRunning) {
			event.setCancelled(true);
		}
	}
}
