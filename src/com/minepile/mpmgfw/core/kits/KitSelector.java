package com.minepile.mpmgfw.core.kits;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.minepile.mpmgfw.MPMGFramework;
import com.minepile.mpmgfw.api.MinigameKits;
import com.minepile.mpmgfw.core.kits.spawner.EntitySpawner;
import com.minepile.mpmgfw.core.kits.spawner.PlatformSpawner;

import net.citizensnpcs.api.npc.NPC;

/**
 * Spawns kits in the lobby.
 * The maximum number of kits is 7.
 */
public class KitSelector {
	
	private final MPMGFramework PLUGIN;

	private PlatformSpawner platformSpawner;
	private EntitySpawner entitySpawner;
	private HashMap<UUID, Integer> playerKit;
	
	public KitSelector(MPMGFramework plugin) {
		PLUGIN = plugin;
		platformSpawner = new PlatformSpawner();
		entitySpawner = new EntitySpawner();
	}
	
	/**
	 * Called when a player interacts with a kit.
	 * @param name The NPC the player interacted with.
	 * @return Returns the kit reference number (0-6). 
	 */
	public int kitInteract(NPC name) {
		//TODO: Setup a real kitIntereaction method.
		return 0;
	}
	
	/**
	 * Spawns the kits in the game lobby.
	 */
	public void spawnKits() {
		MinigameKits minigameKit = PLUGIN.getMinigamePluginManager().getMinigameKit();
		
		//Spawn platform for NPC's to stand on.
		platformSpawner.SetPlatforms(minigameKit.getKitPlatformLocation());
		
		//TODO: Spawn NPC
		entitySpawner.setEntities(minigameKit.getKitPlatformLocation());
		
	}
	
	/**
	 * Removes the kits from the game lobby.
	 */
	public void removeKits() {
		MinigameKits minigameKit = PLUGIN.getMinigamePluginManager().getMinigameKit();
		
		//TODO: Remove NPC
		
		//Remove kit platform.
		platformSpawner.setPlatforms(minigameKit.getKitPlatformLocation(), Material.AIR);
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
