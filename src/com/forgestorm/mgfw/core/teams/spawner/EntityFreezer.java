package com.forgestorm.mgfw.core.teams.spawner;

import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.teams.TeamSelector;

public class EntityFreezer {
	
	private final MGFramework PLUGIN;
	private final TeamSelector TEAM_SELECTOR;
	
	private boolean tpEntity;
	
	public EntityFreezer(MGFramework plugin, TeamSelector teamSelector) {
		PLUGIN = plugin;
		TEAM_SELECTOR = teamSelector;
		
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
				for (Entry<UUID, Location> entry : TEAM_SELECTOR.getTeamLocations().entrySet()) {
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
