package com.forgestorm.mgfw.core.teams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.api.MinigameTeams;
import com.forgestorm.mgfw.core.constants.Messages;
import com.forgestorm.mgfw.core.teams.spawner.EntityFreezer;
import com.forgestorm.mgfw.core.teams.spawner.EntitySpawner;
import com.forgestorm.mgfw.core.teams.spawner.Spawner;
import com.forgestorm.mgfw.util.PlatformBuilder;

import net.md_5.bungee.api.ChatColor;

/**
 * Spawns teams in the lobby.
 * The maximum number of teams is 7.
 */
public class TeamSelector {

	private final MGFramework PLUGIN;

	private PlatformBuilder platformSpawner;
	private Spawner entitySpawner;
	private EntityFreezer entityFreezer;
	private HashMap<UUID, Integer> playerTeam;
	private HashMap<UUID, Location> teamLocations;
	private ArrayList<UUID> teamEntityUUID;

	public TeamSelector(MGFramework plugin) {
		PLUGIN = plugin;
		platformSpawner = new PlatformBuilder();
		playerTeam = new HashMap<UUID, Integer>();
		teamLocations = new HashMap<UUID, Location>();
		teamEntityUUID = new ArrayList<UUID>();
	}

	/**
	 * Called when a player interacts with a team.
	 * @param name The NPC the player interacted with. 
	 */
	public void teamInteract(Player player, int team) {
		MinigameTeams minigameTeam = PLUGIN.getMinigamePluginManager().getMinigameTeams();
		UUID uuid = player.getUniqueId();

		//Set the the players team.
		playerTeam.put(uuid, team);

		//Set player a confirmation message.
		player.sendMessage("");
		player.sendMessage(Messages.GAME_BAR_TEAM.toString());
		player.sendMessage("");
		player.sendMessage(minigameTeam.getTeamNames().get(team) + ChatColor.DARK_GRAY + ":");
		player.sendMessage("");
		player.sendMessage(minigameTeam.getTeamDescriptions().get(team));
		player.sendMessage("");
		player.sendMessage(Messages.GAME_BAR_BOTTOM.toString());
		player.sendMessage("");

		//Play a confirmation sound.
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, .5f, .6f);
		
		//Update the lobby scoreboard.
		PLUGIN.getGameLobby().getScoreboard().updatePlayerScoreboard(player);
	}

	/**
	 * Spawns the teams in the game lobby.
	 */
	public void spawnTeams() {
		MinigameTeams team = PLUGIN.getMinigamePluginManager().getMinigameTeams();

		//Spawn platform for NPC's to stand on.
		platformSpawner.setPlatforms(team.getTeamPlatformLocations(), team.getTeamPlatformMaterials());

		//TODO: Spawn NPC

		//Spawn a bukteam/spigot entity.
		entitySpawner = new EntitySpawner(this);
		entitySpawner.setEntities(team.getTeamPlatformLocations(), team.getTeamNames(), team.getEntityTypes());

		//Start entity freezing.
		setEntityFreezer(new EntityFreezer(PLUGIN, this));
		entityFreezer.teleportEntity();

	}

	/**
	 * Removes the teams from the game lobby.
	 */
	public void removeTeams() {
		MinigameTeams minigameTeam = PLUGIN.getMinigamePluginManager().getMinigameTeams();

		//TODO: Remove NPC

		//Remove team platform.
		platformSpawner.setPlatforms(minigameTeam.getTeamPlatformLocations(), Material.AIR);

		//Empty the team entity
		teamEntityUUID.clear();

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
	 * Gets the team the player has.
	 * @param player The player with a team.
	 * @return A players team. If null, return the first team.
	 */
	public int getPlayerTeam(Player player) {
		UUID uuid = player.getUniqueId();

		if (playerTeam.containsKey(uuid)) {
			return playerTeam.get(uuid);
		} else {
			//Return first team.
			return 0;
		}
	}

	/**
	 * Sets the players team.
	 * @param player The player who needs a team.
	 * @param team The team the player has selected.
	 */
	public void setPlayerTeam(Player player, int team) {
		playerTeam.put(player.getUniqueId(), team);
	}

	public HashMap<UUID, Location> getTeamLocations() {
		return teamLocations;
	}

	/**
	 * Returns an instance of the TeamEntityUUID variable.
	 * @return Returns an instance of the TeamEntityUUID variable.
	 */
	public ArrayList<UUID> getTeamEntityUUID() {
		return teamEntityUUID;
	}
}
