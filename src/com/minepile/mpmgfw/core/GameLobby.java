package com.minepile.mpmgfw.core;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class GameLobby {

	private final String lobbyWorldName;
	private final Location lobbySpawn;
	
	public GameLobby() {
		lobbyWorldName = "mg-lobby";
		lobbySpawn = new Location(Bukkit.getWorld(lobbyWorldName), 0.5, 76, 0.5);
	}
	
	/**
	 * Initialize (setup) the minigame lobby.
	 */
	public void loadLobbyWorld() {

		//Edit world properties.
		Bukkit.setSpawnRadius(0);
		World world = Bukkit.getWorlds().get(0);
		world.setSpawnFlags(false, false);
		world.setGameRuleValue("doMobSpawning", "false");

		//Remove non-player entities from the world.
		for (Entity entity : world.getEntities()) {
			if (!(entity instanceof Player) && entity instanceof LivingEntity) {
				entity.remove();
			}
		}
	}

	/**
	 * Teleports a player to the lobby spawn pad.
	 * 
	 * @param player The player to teleport to lobby spawn.
	 */
	public void tpToLobbySpawn(Player player) {
		player.teleport(lobbySpawn);
	}

	/**
	 * Gets the main lobby's spawn point!
	 * @return A location with the lobby's spawn point.
	 */
	public Location getLobbySpawn() {
		return lobbySpawn;
	}

	/**
	 * Gets the lobby's world name.
	 * @return The name of the lobby world.
	 */
	public String getLobbyWorldName() {
		return lobbyWorldName;
	}
}
