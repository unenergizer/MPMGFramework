package com.minepile.mpmgfw.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import com.minepile.mpmgfw.MPMGFramework;

public class EntityTargetLivingEntity implements Listener{

	private final MPMGFramework PLUGIN;
	
	public EntityTargetLivingEntity(MPMGFramework plugin) {
		PLUGIN = plugin;
	}

	@EventHandler
	public void onEntityTarget(EntityTargetLivingEntityEvent event){
		
		if(!PLUGIN.getGameManager().isMinigameRunning()){
			event.setCancelled(true);
		}
	}
}
