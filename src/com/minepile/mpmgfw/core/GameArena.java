package com.minepile.mpmgfw.core;

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
		String worldName = PLUGIN.getMinigamePluginManager().getMinigameBase().getWorldName();

		//Load the map into memory
		worldDupe.loadWorld(worldName);

		//Cleanup any map entities.
		worldDupe.clearEntities();
	}

	/**
	 * Disables the games loaded assets.
	 */
	public void unloadGameWorld() {

		//Unload the current game map from memory.
		worldDupe.unloadWorld();
	}

	/**
	 * Teleports a player to the game arena.
	 * 
	 * @param player The player who will be teleported.
	 */
	public void tpToGameWorld(Player player, double x, double y, double z) {
		//Teleport to game world
		player.teleport(new Location(worldDupe.getMiniGameWorld(), x, y, z));
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
