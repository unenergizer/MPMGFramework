package com.minepile.mpmgfw.api;

import java.util.ArrayList;

import org.bukkit.Location;

public abstract class MinigameKits {

	/**
	 * TODO: Kit Methods to Implement:
	 * 	NPC's
	 * 	NPC items in hand
	 * 	NPC wearable items
	 */
	
	/**
	 * A list of minigame kit names.
	 * @return Returns list of minigame kit names.
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
