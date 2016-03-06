package com.minepile.mpmgfw.core;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.minepile.mpmgfw.MPMGFramework;
import com.minepile.mpmgfw.core.worlds.WorldDuplicator;

public class GameArena {

	private final MPMGFramework PLUGIN;
	private final WorldDuplicator worldDupe;

	public GameArena(MPMGFramework plugin) {
		PLUGIN = plugin;
		worldDupe = new WorldDuplicator();
	}

	/**
	 * Loads the needed assets from the minigame plugin.
	 */
	public void loadGameWorld() {
		String name = PLUGIN.getMinigamePluginManager().getMinigameBase().getWorldName();
		File wc = Bukkit.getServer().getWorldContainer();
		File destinationFolder = new File(wc + File.separator + name);
		File backupFolder = new File(wc + File.separator + "worlds" + File.separator + name.concat("_backup"));

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
	}

	/**
	 * Disables the games loaded assets.
	 */
	public void unloadGameWorld() {
		String name = PLUGIN.getMinigamePluginManager().getMinigameBase().getWorldName();
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
