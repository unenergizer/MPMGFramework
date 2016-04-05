package com.forgestorm.mgfw.core.teams.spawner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.api.MinigameTeams;

public class PlayerSpawner {

	private final MGFramework PLUGIN;

	public PlayerSpawner(MGFramework plugin){
		PLUGIN = plugin;
	}
	
	/**
	 * Spawns players in the arena world map at specified spawning locations.
	 * @param sortedTeams The sorted map of teams and players to spawn.
	 * @return Returns a HashMap that specifically ties a player to a spawn location. Used to preventing player movement during game rules countdown.
	 */
	public HashMap<Player, Location> spawnPlayers(ConcurrentMap<Integer, ArrayList<Player>> sortedTeams) {
		MinigameTeams minigameTeams = PLUGIN.getMinigamePluginManager().getMinigameTeams();
		ArrayList<ArrayList<Location>> playerTeamSpawnLocations = minigameTeams.getPlayerTeamSpawnLocations();
		HashMap<Player, Location> playerSpawns = new HashMap<Player, Location>();
		
		//Loop through the multidimensional array.
		for (int i = 0; i < sortedTeams.size(); i++) {
			
			//Loop through and spawn players.
			for (int j = 0; j < sortedTeams.get(i).size(); j++) {
				Player player = sortedTeams.get(i).get(j);
				Location spawn = playerTeamSpawnLocations.get(i).get(j);
				
				//Teleport player.
				player.teleport(spawn);
				
				//Save player spawn positions for later use.
				playerSpawns.put(player, spawn);
			}
		}
		return playerSpawns;
	}
}
