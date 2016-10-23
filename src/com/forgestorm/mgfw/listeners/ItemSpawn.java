package com.forgestorm.mgfw.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

public class ItemSpawn implements Listener {

	@EventHandler
	public void onItemSpawn(ItemSpawnEvent event) {
		ItemStack item = event.getEntity().getItemStack();
		Material material = item.getType();
		
		//Prevent chickens from laying eggs.
		if (material.equals(Material.EGG)) {
			event.getEntity().remove();
			event.setCancelled(true);
		}
	}
}
