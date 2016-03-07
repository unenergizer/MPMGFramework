package com.minepile.mpmgfw.core.kits.spawner;

import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import com.minepile.mpmgfw.MPMGFramework;
import com.minepile.mpmgfw.core.kits.KitSelector;

public class EntityFreezer {
	
	private final MPMGFramework PLUGIN;
	private final KitSelector KIT_SELECTOR;
	
	private boolean tpEntity;
	
	public EntityFreezer(MPMGFramework plugin, KitSelector kitSelector) {
		PLUGIN = plugin;
		KIT_SELECTOR = kitSelector;
		
		tpEntity = true;
	}
	
	/**
	 * Teleport the mobs in the list back to their spawn locations.
	 */
	public void teleportEntity() {
		//Lets start a repeating task
		
		//TODO: FIX WITH LOBBY WORLD
		World world = Bukkit.getWorlds().get(0);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				//Loop through our entityKitLocation hashmap and teleport mobs back to their spawns.
				for (Entry<UUID, Location> entry : KIT_SELECTOR.getKitLocations().entrySet()) {
					UUID mobID = entry.getKey();
					Location spawnLocation = entry.getValue();

					//Loop through entity list and teleport the correct entity.
					for (Entity mob : world.getEntities()) {
						if (mob.getUniqueId().equals(mobID)) {
							mob.teleport(spawnLocation);
						}
					}
				}
				
				//Stop this thread if a game starts.
				if(!tpEntity) {
					cancel();
				}
				
			} //END Run method.
		}.runTaskTimer(PLUGIN, 0, 5);
	}
	
	/**
	 * Sets whether or not an entity will teleport.
	 * @param tpMobs Return true to teleport mobs. Return false to cancel the thread.
	 */
	public void setTpEntity(boolean tpMobs) {
		this.tpEntity = tpMobs;
	}
}
