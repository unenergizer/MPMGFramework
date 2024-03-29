package com.forgestorm.mgfw.selector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.api.MinigameTeams;
import com.forgestorm.mgfw.core.GameManager;
import com.forgestorm.mgfw.core.constants.Messages;
import com.forgestorm.mgfw.spawner.TeamSpawner;
import com.forgestorm.mgfw.util.CenterChatText;
import com.forgestorm.mgfw.util.PlatformBuilder;
import com.forgestorm.servercore.core.display.Hologram;

import lombok.Getter;

/**
 * Spawns teams in the lobby.
 * The maximum number of teams is 7.
 */
@Getter
public class TeamSelector {

	private final MGFramework PLUGIN;
	private final GameManager GAME_MANAGER;

	private PlatformBuilder platformSpawner;
	private TeamSpawner entitySpawner;
	private CenterChatText cct = new CenterChatText();
	private ConcurrentMap<Integer, ArrayList<Player>> sortedTeams;
	private ConcurrentMap<Integer, Queue<Player>> queuedPlayers;
	private HashMap<Integer, Location> teamHologramLocations;
	private ArrayList<Hologram> holograms;
	private ArrayList<UUID> teamEntityUUID;
	
	public TeamSelector(MGFramework plugin, GameManager gameManager) {
		PLUGIN = plugin;
		GAME_MANAGER = gameManager;
		sortedTeams = new ConcurrentHashMap<Integer, ArrayList<Player>>(); //<Team, Player>
		queuedPlayers = new ConcurrentHashMap<Integer, Queue<Player>>(); //<Team, Queued Player>
		teamHologramLocations = new HashMap<Integer, Location>();
		holograms = new ArrayList<Hologram>();
		teamEntityUUID = new ArrayList<UUID>();
	}

	/**
	 * Called when a player interacts with a team.
	 * @param name The NPC the player interacted with. 
	 */
	public void teamInteract(int team, Player player) {
		final MinigameTeams minigameTeam = PLUGIN.getMinigamePluginManager().getMinigameTeams();
		final String teamName =  minigameTeam.getTeamNames().get(team);
		final ArrayList<String> desc = minigameTeam.getTeamDescriptions().get(team);
		boolean sameTeam = isJoiningSameTeam(team, player);
		String sameTeamMessage = "";
		
		//If the player has interacted with a team they are on, add a little message to the description.
		if (sameTeam) {
			sameTeamMessage = " " + Messages.TEAM_ALREADY_ON_TEAM.toString();
		}

		//Set player a confirmation message.
		player.sendMessage("");
		player.sendMessage(Messages.GAME_BAR_TEAM.toString());
		player.sendMessage("");
		player.sendMessage(cct.centerMessage(ChatColor.GRAY + "Team: " + teamName + sameTeamMessage));
		player.sendMessage("");

		for (int i = 0; i < desc.size(); i++) {
			String message = cct.centerMessage(ChatColor.YELLOW + desc.get(i));
			player.sendMessage(message);
		}
		
		player.sendMessage("");
		player.sendMessage(Messages.GAME_BAR_BOTTOM.toString());
		player.sendMessage("");
		
		//Play a confirmation sound.
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, .5f, .6f);

		//Set the the players team.
		changePlayerTeam(team, player);

		//Update queued team positions.
		updateQueuePositions();

		//Update holograms.
		createHolograms();

		//Check if game needs to start.
		if (GAME_MANAGER.shouldMinigameStart()) {
			GAME_MANAGER.startCountdown();
		}
	}

	/**
	 * This will create holograms that go over team entities.
	 * These holograms display the player counts for each team.
	 */
	public void createHolograms() {

		//Remove any existing holograms.
		if (!holograms.isEmpty()) {
			for (int i = 0; i < holograms.size(); i++) {
				holograms.get(i).removeHolograms();
			}

			//Clear current holograms array.
			holograms.clear();
		}

		//Generate new holograms.
		for (Entry<Integer, Location> entry : teamHologramLocations.entrySet()) {
			ArrayList<String> hologramText = new ArrayList<String>();
			int team = entry.getKey();
			int currentTeamSize = 0;
			int queuedCount = 0;
			int maxTeamSize = getMaxTeamSize(team);
			final Location location = entry.getValue();
			final Location hologramLocation = new Location(location.getWorld(), location.getX(), location.getY() + .7, location.getZ());

			//Get the number of players on a team.
			if (sortedTeams.get(team) != null) {
				currentTeamSize = sortedTeams.get(team).size();
			}
			
			//Get number of queued players.
			if (queuedPlayers.get(team) != null) {
				queuedCount = queuedPlayers.get(team).size();
			}
			
			hologramText.add(ChatColor.BOLD + "Players: " + Integer.toString(currentTeamSize) + " / " + maxTeamSize);
			hologramText.add(ChatColor.BOLD + "Queued Players: " + queuedCount);
			
			//Create the holograms.
			Hologram hologram = new Hologram();
			hologram.createHologram(hologramText, hologramLocation);
			holograms.add(hologram);
		}
	}

	/**
	 * This is a conditional test to make sure all teams have players.
	 * @return Returns true if all teams have players.
	 */
	public boolean allTeamsHavePlayers() {
		boolean allTeamsHavePlayers = true;

		for (Entry<Integer, ArrayList<Player>> entry : sortedTeams.entrySet()) {
			ArrayList<Player> value = entry.getValue();

			if (value.isEmpty() && value.size() < 1) {
				allTeamsHavePlayers = false;
				return false;
			}
		}

		if (allTeamsHavePlayers) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This will assing all the players on the server a team.
	 */
	public void assignAllPlayerTeams() {
		for (Player players: Bukkit.getOnlinePlayers()) {
			addPlayer(players);
		}
	}

	/**
	 * Adds a late player to a game team.
	 * 
	 * @param player The player who will be added to a team.
	 */
	public void addPlayer(Player player) {
		final ArrayList<Integer> teamSizes = PLUGIN.getMinigamePluginManager().getMinigameTeams().getTeamSizes();

		//Assign player to a fixed size team.
		for (int i = 0; i < teamSizes.size(); i++) {
			int playersNeeded = 0;

			//If no teams have a fixed size, we will sort the player into a team that needs players.
			if (teamSizes.get(i) != -1) {

				if (sortedTeams.get(i) == null) {
					playersNeeded = getMaxTeamSize(i);
				} else {
					playersNeeded = getMaxTeamSize(i) - sortedTeams.get(i).size();
				}

				if(playersNeeded > 0) {
					//If the team needs players, see if a player can be added.
					setPlayerTeam(i, player);

					//Spawn entity holograms.
					createHolograms();

					return;
				}
			}
		}

		//Assign player to any team.
		for (int i = 0; i < teamSizes.size(); i++) {
			int playersNeeded = 0;

			if (sortedTeams.get(i) == null) {
				playersNeeded = getMaxTeamSize(i);
			} else {
				playersNeeded = getMaxTeamSize(i) - sortedTeams.get(i).size();
			}

			if(playersNeeded > 0) {
				//If the team needs players, see if a player can be added.
				setPlayerTeam(i, player);

				//Spawn entity holograms.
				createHolograms();

				return;
			}
		}
	}

	/**
	 * Gets the number of teams for a particular minigame.
	 * @return
	 */
	private int getNumberOfTeams() {
		return PLUGIN.getMinigamePluginManager().getMinigameTeams().getTeamSizes().size();
	}

	/**
	 * Gets the maximum size a of players a team can hold.
	 * @return Return the size a team can hold.
	 */
	private int getMaxTeamSize(final int team) {
		ArrayList<Integer> teams = PLUGIN.getMinigamePluginManager().getMinigameTeams().getTeamSizes();
		int maxPlayers = GAME_MANAGER.getMaxPlayers();

		if (teams.get(team) != -1) {
			//These teams have a predefined size limit.
			return teams.get(team);

		} else {
			//For these unlimited sized teams, we need to figure out how many teams we have and 
			// how many players need to go into them. We will try to make even teams.

			int playersLeftOver = maxPlayers;
			int teamsWithUnlimitedSlots = 0;

			//Lets get the number of players needed to fill a "predefined size limit team."
			for (int i = 0; i < teams.size(); i++) {

				//Only return the predefined team sizes with player slots.
				if (teams.get(i) != -1) {
					playersLeftOver -= teams.get(i);
				} else {
					teamsWithUnlimitedSlots++;
				}
			}

			//Lets make the unlimited team sizes even.
			int teamSize = (int) Math.ceil(playersLeftOver / (double)teamsWithUnlimitedSlots);
			return teamSize;
		}
	}	

	/**
	 * This will try and swap queued players teams if swapping is possible.
	 */
	private void swapTeamMembers() {

		//Lets loop through all the teams.
		for (int i = 0; i < getNumberOfTeams(); i++) {
			int currentTeam = i;
			final ArrayList<Player> currentTeamPlayers = sortedTeams.get(currentTeam);

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
		final ArrayList<String> teamNames = PLUGIN.getMinigamePluginManager().getMinigameTeams().getTeamNames();

		//Loop through every team and update it.
		for (int i = 0; i < teamNames.size(); i++) {

			int playersNeeded = 0;

			if (sortedTeams.get(i) == null) {
				playersNeeded = getMaxTeamSize(i);
			} else {
				playersNeeded = getMaxTeamSize(i) - sortedTeams.get(i).size();
			}

			//If the team needs players, see if a player can be added from queue.
			if (playersNeeded > 0) {

				//This team could use a player. Lets see if a player exist in the queue.
				if (queuedPlayers.get(i) != null && !queuedPlayers.get(i).isEmpty()) {

					//Queued players exists, now lets loop through them and add them to the sorted team.
					for (int j = 0; j < playersNeeded; j++) {
						Player player = queuedPlayers.get(i).poll();

						//Remove player from current team.
						removeSortedPlayer(player);

						//Add player to the new team.
						setPlayerTeam(i, player);
					}
				}
			}
		}

		//Test to see if players can swap teams.
		swapTeamMembers();
	}
	
	/**
	 * This will test if the player is trying to join a team they are already on.
	 * @param futureTeam The team the player wants to join.
	 * @param player The player who is joining the team.
	 * @return Returns a boolean true, if the team is the same.
	 */
	private boolean isJoiningSameTeam(final int futureTeam, final Player player) {
		if (getPlayerTeam(player) == futureTeam) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Changes a players team from the current one to another.
	 * @param futureTeam The team the player wants to switch to.
	 * @param player The player who is trying to switch teams.
	 */
	private void changePlayerTeam(final int futureTeam, Player player) {
		//If the player is already on the "futureTeam" then don't let them change their team.
		if (!isJoiningSameTeam(futureTeam, player)) {
			int maxTeamSize = getMaxTeamSize(futureTeam);
			int currentTeamSize = 0;

			if (sortedTeams.get(futureTeam) != null) {
				currentTeamSize = sortedTeams.get(futureTeam).size();
			}

			//If the team has room, place the player in that team.
			if (currentTeamSize < maxTeamSize) {
				//Remove them from current team.
				removeSortedPlayer(player);

				//Put them in the future team.
				setPlayerTeam(futureTeam, player);

			} else {
				if (queuedPlayers.get(futureTeam) != null && !queuedPlayers.get(futureTeam).contains(player)) {

					//Remove from any existing queue if the player choose a different team.
					if(isOnAnotherQueue(player)) {
						removeQueuedPlayer(player);
					}

					//Their was no room. Put the player in a queue.
					addQueuedPlayer(futureTeam, player);

					player.sendMessage(Messages.TEAM_QUEUE_PLACED.toString());

				} else if (queuedPlayers.get(futureTeam) == null) {

					//Remove from any existing queue if the player choose a different team.
					if(isOnAnotherQueue(player)) {
						removeQueuedPlayer(player);
					}

					//Their was no room. Put the player in a queue.
					addQueuedPlayer(futureTeam, player);

					player.sendMessage(Messages.TEAM_QUEUE_PLACED.toString());

				}
			}
		}
	}

	/**
	 * Spawns the teams in the game lobby.
	 */
	public void spawnTeamsEntities() {
		MinigameTeams team = PLUGIN.getMinigamePluginManager().getMinigameTeams();

		//Spawn platform for NPC's to stand on.
		platformSpawner = new PlatformBuilder();
		platformSpawner.setPlatforms(team.getTeamPlatformLocations(), team.getTeamPlatformMaterials());

		//Spawn a bukkit/spigot entity.
		entitySpawner = new TeamSpawner(this);
		entitySpawner.setEntities(team.getTeamPlatformLocations(), team.getTeamNames(), team.getEntityTypes());
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
		teamHologramLocations.clear();

		//Remove HashMap players.
		sortedTeams.clear();
		queuedPlayers.clear();
	}

	/**
	 * Adds a player to a team queue.
	 * @param futureTeam The team the player wants to join.
	 * @param player The player who is trying to change teams.
	 */
	private void addQueuedPlayer(int futureTeam, Player player) {
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
	private boolean isOnAnotherQueue(Player player) {
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
	private void setPlayerTeam(int team, Player player) {
		ArrayList<String> teamNames = PLUGIN.getMinigamePluginManager().getMinigameTeams().getTeamNames();
		ArrayList<Player> currentValue = sortedTeams.get(team);

		if (currentValue == null) {
			currentValue = new ArrayList<Player>();
			sortedTeams.put(team, currentValue);
		}
		currentValue.add(player);

		//Send player a confirmation message.
		player.sendMessage(ChatColor.GREEN + "You joined the \"" + teamNames.get(team) + ChatColor.GREEN + "\" team.");

		//Update the lobby scoreboard.
		GAME_MANAGER.getGameLobby().getScoreboard().updatePlayerScoreboard(player);
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
}
