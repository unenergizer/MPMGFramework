package com.forgestorm.mgfw.core.kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.api.MinigameKits;
import com.forgestorm.mgfw.core.constants.Messages;
import com.forgestorm.mgfw.core.kits.spawner.EntityFreezer;
import com.forgestorm.mgfw.core.kits.spawner.EntitySpawner;
import com.forgestorm.mgfw.core.kits.spawner.Spawner;
import com.forgestorm.mgfw.util.PlatformBuilder;

import net.md_5.bungee.api.ChatColor;

/**
 * Spawns kits in the lobby.
 * The maximum number of kits is 7.
 */
public class KitSelector {

	private final MGFramework PLUGIN;

	private PlatformBuilder platformSpawner;
	private Spawner entitySpawner;
	private EntityFreezer entityFreezer;
	private HashMap<UUID, Integer> playerKit;
	private HashMap<UUID, Location> kitLocations;
	private ArrayList<UUID> kitEntityUUID;

	public KitSelector(MGFramework plugin) {
		PLUGIN = plugin;
		platformSpawner = new PlatformBuilder();
		playerKit = new HashMap<UUID, Integer>();
		kitLocations = new HashMap<UUID, Location>();
		kitEntityUUID = new ArrayList<UUID>();
	}

	/**
	 * Called when a player interacts with a kit.
	 * @param name The NPC the player interacted with. 
	 */
	public void kitInteract(Player player, int kit) {
		MinigameKits minigameKit = PLUGIN.getMinigamePluginManager().getMinigameKit();
		UUID uuid = player.getUniqueId();
		
		//Set the the players kit.
		playerKit.put(uuid, kit);
		
		//Set player a confirmation message.
		player.sendMessage("");
		player.sendMessage(Messages.GAME_BAR_KIT.toString());
		player.sendMessage("");
		player.sendMessage(minigameKit.getKitNames().get(kit) + ChatColor.DARK_GRAY + ":");
		player.sendMessage("");
		player.sendMessage(ChatColor.BLUE + minigameKit.getKitDescriptions().get(kit));
		player.sendMessage("");
		player.sendMessage(Messages.GAME_BAR_BOTTOM.toString());
		player.sendMessage("");
		
		//Play a confirmation sound.
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, .5f, .6f);
	}

	/**
	 * Spawns the kits in the game lobby.
	 */
	public void spawnKits() {
		MinigameKits kit = PLUGIN.getMinigamePluginManager().getMinigameKit();

		//Spawn platform for NPC's to stand on.
		platformSpawner.setPlatforms(kit.getKitPlatformLocations(), kit.getKitPlatformMaterials());

		//TODO: Spawn NPC

		//Spawn a bukkit/spigot entity.
		entitySpawner = new EntitySpawner(this);
		entitySpawner.setEntities(kit.getKitPlatformLocations(), kit.getKitNames(), kit.getEntityTypes());
		
		//Start entity freezing.
		setEntityFreezer(new EntityFreezer(PLUGIN, this));
		entityFreezer.teleportEntity();
		
	}

	/**
	 * Removes the kits from the game lobby.
	 */
	public void removeKits() {
		MinigameKits minigameKit = PLUGIN.getMinigamePluginManager().getMinigameKit();

		//TODO: Remove NPC

		//Remove kit platform.
		platformSpawner.setPlatforms(minigameKit.getKitPlatformLocations(), Material.AIR);
		
		//Empty the kit entity
		kitEntityUUID.clear();
		
		//Stop entity freezing.
		entityFreezer.setTpEntity(false);
		setEntityFreezer(null);
	}

	public EntityFreezer getEntityFreezer() {
		return entityFreezer;
	}

	public void setEntityFreezer(EntityFreezer entityFreezer) {
		this.entityFreezer = entityFreezer;
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
	
	public HashMap<UUID, Location> getKitLocations() {
		return kitLocations;
	}

	/**
	 * Returns an instance of the KitEntityUUID variable.
	 * @return Returns an instance of the KitEntityUUID variable.
	 */
	public ArrayList<UUID> getKitEntityUUID() {
		return kitEntityUUID;
	}
}
