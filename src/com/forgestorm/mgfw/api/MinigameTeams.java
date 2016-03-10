package com.forgestorm.mgfw.api;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public abstract class MinigameTeams {
	
	/**
	 * A list of entity types to spawn.
	 * @return Returns a list of entity types to spawn.
	 */
	public abstract ArrayList<EntityType> getEntityTypes();

	/**
	 * A list of entity hand items.
	 * @return Returns a list of entity hand items.
	 */
	public abstract ArrayList<ItemStack> getEntityHandItems();

	/**
	 * A list of entity spawn locations.
	 * @return Returns a list of entity entity armor.
	 */
	public abstract ArrayList<ItemStack> getEntityArmorItems();
	
	/**
	 * A list of minigame team names.
	 * @return Returns a list of minigame team names.
	 */
	public abstract ArrayList<String> getTeamNames();
	
	/**
	 * A list of minigame team descriptions.
	 * @return Returns a list of minigame team descriptions.
	 */
	public abstract ArrayList<String> getTeamDescriptions();
	
	/**
	 * A list of minigame platform locations.
	 * <p>
	 * Each patform has two locations.
	 * 
	 * @return Returns a list of minigame platform locations.
	 */
	public abstract ArrayList<ArrayList<Location>> getTeamPlatformLocations();
	
	/**
	 * A list of minigame platform materials.
	 * @return Returns a list of minigame platform materials.
	 */
	public abstract ArrayList<Material> getTeamPlatformMaterials();
}