package com.forgestorm.mgfw.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

public class InventoryDrag implements Listener {

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		event.setCancelled(true);
	}
}
