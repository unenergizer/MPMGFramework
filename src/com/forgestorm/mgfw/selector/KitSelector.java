package com.forgestorm.mgfw.selector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.api.MinigameKits;
import com.forgestorm.mgfw.core.GameManager;
import com.forgestorm.mgfw.core.constants.Messages;
import com.forgestorm.mgfw.spawner.KitSpawner;
import com.forgestorm.mgfw.util.PlatformBuilder;

import net.md_5.bungee.api.ChatColor;

/**
 * Spawns kits in the lobby.
 * The maximum number of kits is 7.
 */
public class KitSelector {

	private final MGFramework PLUGIN;
	private final GameManager GAME_MANAGER;

	private PlatformBuilder platformSpawner;
	private KitSpawner entitySpawner;
	private HashMap<UUID, Integer> playerKit;
	private ArrayList<UUID> kitEntityUUID;

	public KitSelector(MGFramework plugin, GameManager gameManager) {
		PLUGIN = plugin;
		GAME_MANAGER = gameManager;
		platformSpawner = new PlatformBuilder();
		playerKit = new HashMap<UUID, Integer>();
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
		
		//Update the lobby scoreboard.
		GAME_MANAGER.getGameLobby().getScoreboard().updatePlayerScoreboard(player);
	}
	
	/**
	 * This will remove a players kit if they leave the server.
	 * @param player The player who's kit we will remove.
	 */
	public void removePlayerKit(Player player) {
		UUID uuid = player.getUniqueId();
		
		if (playerKit.containsKey(uuid)) {
			playerKit.remove(uuid);
		}
	}

	/**
	 * Spawns the kits in the game lobby.
	 */
	public void spawnKitEntities() {
		MinigameKits kit = PLUGIN.getMinigamePluginManager().getMinigameKit();

		//Spawn platform for NPC's to stand on.
		platformSpawner.setPlatforms(kit.getKitPlatformLocations(), kit.getKitPlatformMaterials());

		//TODO: Spawn NPC

		//Spawn a bukkit/spigot entity.
		entitySpawner = new KitSpawner(this);
		entitySpawner.setEntities(kit.getKitPlatformLocations(), kit.getKitNames(), kit.getEntityTypes());	
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
	 * Returns an instance of the KitEntityUUID variable.
	 * @return Returns an instance of the KitEntityUUID variable.
	 */
	public ArrayList<UUID> getKitEntityUUID() {
		return kitEntityUUID;
	}
}
