package com.forgestorm.mgfw.core.teams.spawner;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

public abstract class Spawner {
	
	/**
	 * This will spawn an entity in the world.
	 *  
	 * @param string The entity name.
	 * @param centerLocation The centered location of the kit platform.
	 * @param entityType The type of entity to spawn in the world.
	 */
	public abstract void spawnEntity(String string, Location centerLocation, EntityType entityType);
	
	/**
	 * This will set the Entity on the kit platform.
	 * 
	 * @param platformLocation A list of locations to get the center from.
	 * @param names A list of names for the kit's.
	 * @param entities A list of entities to spawn.
	 */
	public void setEntities(ArrayList<ArrayList<Location>> platformLocation, ArrayList<String> names, ArrayList<EntityType> entities) {

		for (int i = 0; i < platformLocation.size(); i++) {
			Location loc1 = platformLocation.get(i).get(0);
			Location loc2 = platformLocation.get(i).get(1);

			//Spawn an entity at the center location.
			spawnEntity(names.get(i), getCenterLocation(loc1, loc2), entities.get(i));
		}
	}

	/**
	 * Spawns an entity between two locations.
	 * 
	 * @param loc1 The first location.
	 * @param loc2 The second location.
	 * @param kitName The name of the entity to spawn.
	 */
	private Location getCenterLocation(Location loc1, Location loc2) {
		
		//Gets the smallest and largest value in the X and Z plane and 
		//puts it in minimum and maximum variables.
		double minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
		double minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		double maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
		double maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

		//Gets the center of the two locations.
		double x = (minX + maxX) / 2;
		double z = (minZ + maxZ) / 2;
		
		World world = loc1.getWorld();
		Location spawnloc = new Location(world, x + .5, loc1.getY() + 1, z + .5);
		
		return spawnloc;
	}
}
