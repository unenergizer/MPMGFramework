package com.minepile.mpmgfw.core.kits.spawner;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class PlatformBuilder {
	
	/**
	 * Places platforms in the given world.
	 */
	public void setPlatforms(ArrayList<ArrayList<Location>> platformLocation, Material material){
		
		//Set platform
		for (int i = 0; i < platformLocation.size(); i++) {
			Location loc1 = platformLocation.get(i).get(0);
			Location loc2 = platformLocation.get(i).get(1);

			setBlock(loc1, loc2, material);
		}
	}
	
	/**
	 * Places platforms in the given world of a given type.
	 * <p>
	 * You can set a platform as Material.Air to clear an existing platform.
	 */
	public void setPlatforms(ArrayList<ArrayList<Location>> platformLocation, ArrayList<Material> material){
		
		//Set platform
		for (int i = 0; i < platformLocation.size(); i++) {
			Location loc1 = platformLocation.get(i).get(0);
			Location loc2 = platformLocation.get(i).get(1);

			setBlock(loc1, loc2, material.get(i));
		}
	}

	/**
	 * Sets blocks between two locations.
	 * 
	 * @param loc1 The first location.
	 * @param loc2 The second location.
	 * @param material The material to set at the location.
	 */
	private void setBlock(Location loc1, Location loc2, Material material) {

		int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
		int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
		int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
		int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
		int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

		for(int x = minX; x <= maxX; x++){
			for(int y = minY; y <= maxY; y++){
				for(int z = minZ; z <= maxZ; z++){
					Block block = loc1.getWorld().getBlockAt(x, y, z);
					block.setType(material);
				}
			}
		}
	}
}