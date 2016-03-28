package com.forgestorm.mgfw.core.teams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;
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
	private ConcurrentMap<Integer, ArrayList<Player>> sortedTeams;
	private ConcurrentMap<Integer, Queue<Player>> queuedPlayers;
	private HashMap<UUID, Location> teamLocations;
	private ArrayList<UUID> teamEntityUUID;

	public TeamSelector(MGFramework plugin) {
		PLUGIN = plugin;
		platformSpawner = new PlatformBuilder();
		sortedTeams = new ConcurrentHashMap<Integer, ArrayList<Player>>(); //<Team, Player>
		queuedPlayers = new ConcurrentHashMap<Integer, Queue<Player>>(); //<Team, Queued Player>
		teamLocations = new HashMap<UUID, Location>();
		teamEntityUUID = new ArrayList<UUID>();
	}

	/**
	 * This will assing all the players on the server a team.
	 */
	public void assignAllPlayerTeams() {
		if (Bukkit.getOnlinePlayers().size() > 0) {
			int maxTeamSize = getMaxTeamSize();
			int peopleSorted = 0;
			ArrayList<Player> bukkitPlayers = new ArrayList<Player>();

			//TODO: Remove this hack.
			for (Player players: Bukkit.getOnlinePlayers()) {
				bukkitPlayers.add(players);
			}

			//Loop through the teams.
			for (int i = 0; i < getNumberOfTeams(); i++) {

				//Add players to a team.
				for(int j = 0; j < maxTeamSize; j++) {

					int currentTeam = i;

					if (peopleSorted < Bukkit.getOnlinePlayers().size()) {
						Player currentPerson = bukkitPlayers.get(peopleSorted);

						setPlayerTeam(currentTeam, currentPerson);
						peopleSorted++;
					}
				}
			}
		}
	}
	
	/**
	 * Gets the number of teams for a paticular minigame.
	 * @return
	 */
	private int getNumberOfTeams() {
		return PLUGIN.getMinigamePluginManager().getMinigameTeams().getTeamNames().size();
	}

	/**
	 * Gets the maximum size a of players a team can hold.
	 * @return Return the size a team can hold.
	 */
	private int getMaxTeamSize() { return PLUGIN.getGameManager().getMaxPlayers() / getNumberOfTeams(); }	

	/**
	 * This will try and swap queued players teams if swapping is possible.
	 */
	public void swapTeamMembers() {

		//Lets loop through all the teams.
		for (int i = 0; i < getNumberOfTeams(); i++) {
			int currentTeam = i;
			ArrayList<Player> currentTeamPlayers = sortedTeams.get(currentTeam);

			//See if any players are queued for this team.
			//If none want to join this team, then their is no need to continue.
			if (queuedPlayers.get(currentTeam) != null && !queuedPlayers.get(currentTeam).isEmpty()) {
				Player firstQueuedPlayerForCurrentTeam = queuedPlayers.get(currentTeam).peek();

				//Loop through the players
				for (int j = 0; j < currentTeamPlayers.size(); j++) {

					Player player = currentTeamPlayers.get(j);

					//Loop through the HashMap of queues and lets
					for (Entry<Integer, Queue<Player>> entry : queuedPlayers.entrySet()) {
						int newTeam = entry.getKey();

						//Make sure the team we are looking at is not the same as the current team we are working with.
						if (newTeam != currentTeam) {

							//If the player is the first queued on another team, then we can make a swap.
							if (queuedPlayers.get(newTeam).peek() != null && queuedPlayers.get(newTeam).peek().equals(player)) {

								//Lets begin making the swap.
								//Remove players from current teams.
								removeSortedPlayer(firstQueuedPlayerForCurrentTeam);
								removeSortedPlayer(player);

								//Remove players from queue.
								queuedPlayers.get(currentTeam).poll();
								queuedPlayers.get(newTeam).poll();

								//Add player to the other team.
								setPlayerTeam(currentTeam, firstQueuedPlayerForCurrentTeam);
								setPlayerTeam(newTeam, player);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Update queued status on certain events (player join, player quit, player change team.
	 */
	public void updateQueuePositions() {

		//Loop through every team and update it.
		for (int i = 0; i < getNumberOfTeams(); i++) {
			int currentTeam = i;
			int playersNeeded = getMaxTeamSize() - sortedTeams.get(currentTeam).size();

			//If the team needs players, see if a player can be added from queue.
			if (playersNeeded > 0) {

				//This team could use a player. Lets see if a player exist in the queue.
				if (queuedPlayers.get(currentTeam) != null && !queuedPlayers.get(currentTeam).isEmpty()) {

					//Queued players exists, now lets loop through them and add them to the sorted team.
					for (int j = 0; j < playersNeeded; j++) {
						Player player = queuedPlayers.get(currentTeam).poll();


						//TODO: WARNING: here do isPlayerOnline check to make sure we are 
						//not adding a player to a team if they quit the game!

						//Remove player from current team.
						removeSortedPlayer(player);

						//Add player to the new team.
						setPlayerTeam(currentTeam, player);
					}
				}
			}
		}

		//Test to see if players can swap teams.
		swapTeamMembers();
	}

	/**
	 * Adds a late player to a game team.
	 * 
	 * @param player The player who will be added to a team.
	 */
	public void addLatePlayer(Player player) {
		for (int i = 0; i < getNumberOfTeams(); i++) {

			int currentTeam = i;
			
			if(sortedTeams.get(currentTeam) != null) {
				int playersNeeded = getMaxTeamSize() - sortedTeams.get(currentTeam).size();

				//If the team needs players, see if a player can be added.
				if (playersNeeded > 0) {

					setPlayerTeam(currentTeam, player);
				}
			} else {
				setPlayerTeam(currentTeam, player);
			}
		}
	}

	/**
	 * Changes a players team from the current one to another.
	 * @param futureTeam The team the player wants to switch to.
	 * @param player The player who is trying to switch teams.
	 */
	public void changePlayerTeam(int futureTeam, Player player) {

		//If the player is already on the "futureTeam" then don't let them change their team.
		if (getPlayerTeam(player) != futureTeam) {
			int maxTeamSize = getMaxTeamSize();
			int currentTeamSize = sortedTeams.get(futureTeam).size();

			//If the team has room, place the player in that team.
			if (currentTeamSize < maxTeamSize) {
				//Remove them from current team.
				removeSortedPlayer(player);

				//Put them in the future team.
				setPlayerTeam(futureTeam, player);

			} else {
				if (queuedPlayers.get(futureTeam) != null && queuedPlayers.get(futureTeam).contains(player)) {

					//Remove from any existing queue if the player choose a different team.
					if(isOnAnotherQueue(player)) {
						removeQueuedPlayer(player);
					}

					//Their was no room. Put the player in a queue.
					addQueuedPlayer(futureTeam, player);

					player.sendMessage(Messages.TEAM_QUEUE_PLACED.toString());

				} else {
					player.sendMessage(Messages.TEAM_QUEUE_ALREADY_PLACED.toString());
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 1F, .5F);
				}
			}
		} else {
			player.sendMessage(Messages.TEAM_ALREADY_ON_TEAM.toString());
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 1F, .5F);
		}
	}

	/**
	 * Called when a player interacts with a team.
	 * @param name The NPC the player interacted with. 
	 */
	public void teamInteract(int team, Player player) {
		MinigameTeams minigameTeam = PLUGIN.getMinigamePluginManager().getMinigameTeams();

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

		//Set the the players team.
		changePlayerTeam(team, player);

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

		//Spawn a bukkit/spigot entity.
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

		//Remove HashMap players.
		sortedTeams.clear();
		queuedPlayers.clear();
	}

	/**
	 * Gets an instance of the EntityFreezer class.
	 * @return Returns an instance of the EntityFreezer class.
	 */
	public EntityFreezer getEntityFreezer() {
		return entityFreezer;
	}

	/**
	 * Sets the entity freezer instance.  Typically set to null to "reset" it for the next game.
	 * @param entityFreezer The entity freezer object we want to set.
	 */
	public void setEntityFreezer(EntityFreezer entityFreezer) {
		this.entityFreezer = entityFreezer;
	}

	/**
	 * Adds a player to a team queue.
	 * @param futureTeam The team the player wants to join.
	 * @param player The player who is trying to change teams.
	 */
	public void addQueuedPlayer(int futureTeam, Player player) {
		Queue<Player> currentQueue = queuedPlayers.get(futureTeam);
		if (currentQueue == null)  {
			currentQueue = new LinkedList<Player>();
			queuedPlayers.put(futureTeam, currentQueue);
		}
		currentQueue.add(player);
	}

	/**
	 * Remove the player from any queue they might be on.
	 * @param player The player who will be removed from an existing queue.
	 */
	public void removeQueuedPlayer(Player player) {

		for (int i = 0; i < getNumberOfTeams(); i++) {
			int currentTeam = i;
			if (queuedPlayers.get(currentTeam) != null && queuedPlayers.get(currentTeam).contains(player)) {

				MinigameTeams minigameTeam = PLUGIN.getMinigamePluginManager().getMinigameTeams();

				queuedPlayers.get(currentTeam).remove(player);

				//Send message letting player know they were dropped from a queue.
				player.sendMessage(Messages.TEAM_DROPPED_FROM_QUEUE.toString().replace("&s", minigameTeam.getTeamNames().get(currentTeam)));
			}
		}
	}

	/**
	 * This is a conditional test to see if a player is placed on a queue.
	 * @param player The player who we are going to run the test for.
	 * @return Returns true if a player is on a queue for any team.
	 */
	public boolean isOnAnotherQueue(Player player) {
		for (int i = 0; i < getNumberOfTeams(); i++) {
			int currentTeam = i;
			if (queuedPlayers.get(currentTeam) != null && queuedPlayers.get(currentTeam).contains(player)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the team the player is currently on.
	 * @param player The player who we will find a team for.
	 * @return The name of the team the player is on.
	 */
	public Integer getPlayerTeam(Player player) {
		for (Entry<Integer, ArrayList<Player>> entry : sortedTeams.entrySet()) {
			int team = entry.getKey();

			//System.out.println("Finding team: " + team);

			ArrayList<Player> currentPlayers = sortedTeams.get(team);

			for (int i = 0; i < currentPlayers.size(); i++) {

				//System.out.println("Finding player: " + currentPlayers.get(i));

				if (currentPlayers.get(i).equals(player)) {
					return team;
				}
			}
		}
		//No team was found?
		return null;
	}

	/**
	 * Sets the players team.
	 * @param player The player who needs a team.
	 * @param team The team the player has selected.
	 */
	public void setPlayerTeam(int team, Player player) {
		ArrayList<Player> currentValue = sortedTeams.get(team);
		if (currentValue == null) {
			currentValue = new ArrayList<Player>();
			sortedTeams.put(team, currentValue);
		}
		currentValue.add(player);
	}

	/**
	 * Removes a player from the sortedTeams HashMap array.
	 * @param player The player that will be removed.
	 */
	public void removeSortedPlayer(Player player) {
		ArrayList<Player> currentPlayers = sortedTeams.get(getPlayerTeam(player));

		for (int i = 0; i < currentPlayers.size(); i++) {
			if (currentPlayers.get(i).equals(player)) {
				currentPlayers.remove(i);
				break;
			}
		}
	}

	
	/**
	 * Gets the spawn locations of a team mob.
	 * @return Returns a HashMap of a <Entity UUID, and a World Location>.
	 */
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

	/**
	 * Gets an instance of the sortedTeams HashMap.
	 * @return Returns a <Team Integer, and a ArrayList<of Players in a team>.
	 */
	public ConcurrentMap<Integer, ArrayList<Player>> getSortedTeams() {
		return sortedTeams;
	}
}
