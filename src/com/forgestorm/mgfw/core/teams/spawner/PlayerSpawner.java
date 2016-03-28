package com.forgestorm.mgfw.core.teams.spawner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;
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
	 * @param concurrentMap The sorted map of teams and players to spawn.
	 * @return Returns a HashMap that specifically ties a player to a spawn location. Used to preventing player movement during game rules countdown.
	 */
	public HashMap<Player, Location> spawnPlayers(ConcurrentMap<Integer, ArrayList<Player>> concurrentMap) {
		MinigameTeams instance = PLUGIN.getMinigamePluginManager().getMinigameTeams();
		ArrayList<ArrayList<Location>> playerTeamSpawnLocations = instance.getPlayerTeamSpawnLocations();
		HashMap<Player, Location> playerSpawns = new HashMap<Player, Location>();
		int playersTeleported = 0;
		
		//Loop through the multidimensional array.
		for (int team = 0; team < playerTeamSpawnLocations.size(); team++) {
			
			//Loop through the spawn locations.
			for (int location = 0; location < playerTeamSpawnLocations.get(team).size(); location++) {
				
				//Loop through the BiMap keys.
				for(Integer key: concurrentMap.keySet()) {
					
					//Spawn the player at a given location.
					if(key == team && playersTeleported < Bukkit.getOnlinePlayers().size()) {
						Location loc = playerTeamSpawnLocations.get(team).get(location);
						Player player = concurrentMap.get(team).get(playersTeleported);
						
						player.teleport(loc);
						
						playerSpawns.put(player, loc);
						
						playersTeleported++;
					}
				}
			}
		}
		return playerSpawns;
	}
}
