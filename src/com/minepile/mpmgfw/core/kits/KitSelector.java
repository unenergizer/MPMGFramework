package com.minepile.mpmgfw.core.kits;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.minepile.mpmgfw.MPMGFramework;

import net.citizensnpcs.api.npc.NPC;

/**
 * Spawns kits in the lobby.
 * The maximum number of kits is 7.
 */
public class KitSelector {
	
	private final MPMGFramework PLUGIN;
	
	private PlatformSpawner platformSpawner;
	private Integer numOfKits;
	private HashMap<UUID, Integer> playerKit;
	
	public KitSelector(MPMGFramework plugin) {
		PLUGIN = plugin;
		platformSpawner = new PlatformSpawner();
	}
	
	/**
	 * Called when a player interacts with a kit.
	 * @param name The NPC the player interacted with.
	 * @return Returns the kit reference number (0-6). 
	 */
	public int kitInteract(NPC name) {
		return 0;
	}
	
	/**
	 * Spawns the kits in the game lobby.
	 */
	public void spawnKits() {
		//TODO: Spawn platform
		platformSpawner.SetPlatforms(PLUGIN.getMinigamePluginManager().getMinigameKit().setKitPlatformLocation());
		
		
		//TODO: Spawn NPC
	}
	
	/**
	 * Removes the kits from the game lobby.
	 */
	public void removeKits() {
		//TODO: Remove NPC
		
		//TODO: Remove platform
	}
	
	public Integer getNumOfKits() {
		return numOfKits;
	}

	public void setNumOfKits(Integer numOfKits) {
		this.numOfKits = numOfKits;
	}

	/**
	 * Gets the kit the player has.
	 * @param player The player with a kit.
	 * @return A players kit. If null, return the first kit.
	 */
	public int getPlayerKit(Player player) {
		UUID uuid = player.getUniqueId();
		
		if (playerKit.containsKey(uuid)) {
			return playerKit.get(uuid);
		} else {
			//Return first kit.
			return 0;
		}
	}
	
	/**
	 * Sets the players kit.
	 * @param player The player who needs a kit.
	 * @param kit The kit the player has selected.
	 */
	public void setPlayerKit(Player player, int kit) {
		playerKit.put(player.getUniqueId(), kit);
	}
}
