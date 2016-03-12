package com.forgestorm.mgfw.core;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.worlds.WorldDuplicator;

public class GameArena {

	private final MGFramework PLUGIN;
	private final WorldDuplicator worldDupe;

	public GameArena(MGFramework plugin) {
		PLUGIN = plugin;
		worldDupe = new WorldDuplicator();
	}
	
	/**
	 * This will setup a spectator for a player.
	 * @param player The player that will be setup as a spectator.
	 */
	public void setupSpectator(Player player) {
		//Heal the player
		player.setHealth(20);
		player.setFoodLevel(20);
		
		//Clear a players inventory
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		
		//Switch Gamemode
		player.setGameMode(GameMode.ADVENTURE);
		
		//Give the player flying.
		player.setAllowFlight(true);
		player.setFlying(true);
		
		//Set collide entities false.
		player.spigot().setCollidesWithEntities(false);
	}
	
	/**
	 * Loads the needed assets from the minigame plugin.
	 */
	public void loadGameWorld() {
		String name = PLUGIN.getMinigamePluginManager().getMinigameBase().getArenaWorldName();
		
		File wc = Bukkit.getServer().getWorldContainer();
		File destinationFolder = new File(wc + File.separator + name);
		File backupFolder = new File(wc + File.separator + "worlds" + File.separator + name.concat("_backup"));

		if (worldDupe.getWorld() == null) {
			//Copy the backup world folder.
			try {
				worldDupe.copyFolder(backupFolder, destinationFolder);
			} catch (IOException e) {
				e.printStackTrace();
			}

			//Show success message in console.
			Bukkit.getServer().getLogger().info("[MPMG-Framework] Copied world: " + name);

			//Load the map into memory
			worldDupe.loadWorld(name);

			//Cleanup any map entities.
			worldDupe.clearEntities();
		} else {
			Bukkit.getServer().getLogger().info("[MPMG-Framework] World var not null? We will not load the next world.");
		}
	}

	/**
	 * Disables the games loaded assets.
	 */
	public void unloadGameWorld() {
		String name = PLUGIN.getMinigamePluginManager().getMinigameBase().getArenaWorldName();
		File wc = Bukkit.getServer().getWorldContainer();
		File destinationFolder = new File(wc + File.separator + name);

		//Unload the current game map from memory.
		worldDupe.unloadWorld();

		//Delete the world folder.
		try {
			worldDupe.deleteFolder(destinationFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Teleport all players in the server to the game world.
	 */
	public void tpAllToGameWorld() {
		for(Player players: Bukkit.getOnlinePlayers()) {
			//TODO: Get spawn cords from minigame plugin.
			tpToGameWorld(players, 0, 90, 0);
		}
	}
	
	/**
	 * Teleports a player to the game arena.
	 * 
	 * @param player The player who will be teleported.
	 */
	public void tpToGameWorld(Player player, double x, double y, double z) {
		//Teleport to game world
		player.teleport(new Location(worldDupe.getWorld(), x, y, z));
	}

	/**
	 * Returns and instance of the world duplicator.
	 * 
	 * @return An instance of the world duplicator.
	 */
	public WorldDuplicator getWorldDupe() {
		return worldDupe;
	}
}
