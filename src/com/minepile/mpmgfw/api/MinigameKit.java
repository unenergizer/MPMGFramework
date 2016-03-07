package com.minepile.mpmgfw.api;

import java.util.ArrayList;

import org.bukkit.Location;

public abstract class MinigameKit {
	
	public MinigameKit() {
		
	}
	
	/**
	 * int numOfKits, TreeMap<Integer, ArrayList<Location>> platformLocation)
		kit npc
		kit npc held item
		kit npc wearable items
	 */
	
	/**
	 * A list of minigame kit names.
	 * @return A list of minigame kit names.
	 */
	public abstract ArrayList<String> setKitName();
	
	/**
	 * A list of minigame kit descriptions.
	 * @return A list of minigame kit descriptions.
	 */
	public abstract ArrayList<String> setKitDescription();
	
	/**
	 * A list of minigame platform locations.
	 * <p>
	 * Each patform has two locations.
	 * 
	 * @return A list of minigame platform locations.
	 */
	public abstract ArrayList<ArrayList<Location>> setKitPlatformLocation();
	
}
