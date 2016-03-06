package com.minepile.mpmgfw.api;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public abstract class MinigameKits {
	
	/**
	 * A list of entity spawn locations.
	 * @return Returns a lost of entity spawn locations.
	 */
	public abstract ArrayList<Location> entityLocations();

	/**
	 * A list of entity hand items.
	 * @return Returns a lost of entity hand items.
	 */
	public abstract ArrayList<ItemStack> entityHandItems();

	/**
	 * A list of entity spawn locations.
	 * @return Returns a lost of entity entity armor.
	 */
	public abstract ArrayList<ItemStack> entityArmorItems();
	
	/**
	 * A list of minigame kit names.
	 * @return Returns a list of minigame kit names.
	 */
	public abstract ArrayList<String> getKitName();
	
	/**
	 * A list of minigame kit descriptions.
	 * @return Returns a list of minigame kit descriptions.
	 */
	public abstract ArrayList<String> getKitDescription();
	
	/**
	 * A list of minigame platform locations.
	 * <p>
	 * Each patform has two locations.
	 * 
	 * @return Returns a list of minigame platform locations.
	 */
	public abstract ArrayList<ArrayList<Location>> getKitPlatformLocation();
}
