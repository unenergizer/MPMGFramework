package com.forgestorm.mgfw.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;

public class EntityCombust implements Listener {

	@EventHandler
	public void onEntityCombust(EntityCombustEvent event) {
		event.setCancelled(true);
	}
}
