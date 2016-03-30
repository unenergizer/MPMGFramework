package com.forgestorm.mgfw.core.display.scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.api.MinigameTeams;
import com.forgestorm.mgfw.core.constants.Messages;
import com.forgestorm.mgfw.core.teams.TeamSelector;

import net.md_5.bungee.api.ChatColor;

public class ArenaScoreboard {

	private final MGFramework PLUGIN;

	private ScoreboardManager manager;
	private Scoreboard arenaScoreboard;
	private ArrayList<Team> teams;
	private Team spectator;
	private HashMap<Player, Integer> lastScoreMapSent;

	public ArenaScoreboard(MGFramework plugin) {
		PLUGIN = plugin;
		manager = Bukkit.getScoreboardManager();
		teams = new ArrayList<Team>();
	}

	/**
	 * Creates a new scoreboard for the minigame.
	 * <p>
	 * This should happen every time a new game is loaded into the framework.
	 */
	public void createScoreboard() {
		MinigameTeams minigameTeam = PLUGIN.getMinigamePluginManager().getMinigameTeams();

		arenaScoreboard = manager.getNewScoreboard();

		//Create Teams
		for (int i = 0; i < minigameTeam.getTeamNames().size(); i++) {
			String teamName = trimString(minigameTeam.getTeamNames().get(i));
			ChatColor teamColor = minigameTeam.getTeamColors().get(i);
			Team sbTeam = arenaScoreboard.registerNewTeam(teamName);

			//Set this scoreboard team prefix.
			sbTeam.setPrefix(teamColor + "");

			//Add this scoreboard team to the teams array.
			teams.add(sbTeam);

			//TODO: Rank prefixes + colors
			//team.setPrefix(rankPrefix + teamColor + "");
		}

		//Setup spectator team.
		spectator = arenaScoreboard.registerNewTeam("spectator");
		spectator.setPrefix(ChatColor.GRAY + "" + ChatColor.ITALIC);
		spectator.setCanSeeFriendlyInvisibles(true);
		spectator.setAllowFriendlyFire(false);
	}

	/**
	 * This will remove the current lobby scoreboard and components.
	 */
	public void destroyScoreboard() {
		arenaScoreboard = null;
		teams.clear();
		lastScoreMapSent.clear();
	}

	/**
	 * Gives a player a scoreboard objective.
	 * @param player The player who will receive a lobby scoreboard objective.
	 */
	private Objective setBoardObjective(Player player) {
		Scoreboard board = arenaScoreboard;

		//Unregister all current objectives on the scoreboard.
		unregisterObjectives(board);

		//Set the board objective.
		Objective objective = arenaScoreboard.registerNewObjective(player.getName(), "dummy");

		//Set the board title.
		String name = Messages.SB_LOBBY_TITLE.toString();
		objective.setDisplayName(name);

		//Make sure the display slot is the sidebar.
		if(objective.getDisplaySlot() != DisplaySlot.SIDEBAR) {
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		}

		return objective;
	}

	/**
	 * This will put a player on a team.
	 * @param player The player what will be placed on a team.
	 */
	private void setPlayerTeam(Player player) {
		boolean isSpectator = PLUGIN.getGameLobby().getPlayerProfile().get(player).isSpectator();

		if (!isSpectator) {
			TeamSelector teamSelector = PLUGIN.getGameManager().getTeamSelector();
			int playerTeam = teamSelector.getPlayerTeam(player);

			teams.get(playerTeam).addEntry(player.getName());
		} else {

			//Add spectator player.
			spectator.addEntry(player.getName());
		}
	}

	/**
	 * Set the scores for the lobby scoreboard.
	 * @param player The player that we are setting scores for.
	 * @param objective The objective object we are working with.
	 */
	public void setScores(HashMap<Player, Integer> scoreMap) {
		
		lastScoreMapSent = scoreMap;
		
		//Sends scoreboard objectives to a certain player.
		for (Player players: Bukkit.getOnlinePlayers()) {

			//Send the player a custom scoreboard objective.
			Objective objective = setBoardObjective(players);

			//Puts the values on the scoreboard.
			for (Entry<Player, Integer> entry : scoreMap.entrySet()) {
				String sbPlayer = entry.getKey().getName();
				int sbScore = entry.getValue();

				objective.getScore(sbPlayer).setScore(sbScore);
			}

			//Sends the player the current scoreboard.
			players.setScoreboard(arenaScoreboard);
		}
	}

	/**
	 * This will make sure a string is not longer than 14 characters.
	 * If it is, we will shorten the string.
	 * @param input The string we want to trim.
	 * @return The trimmed string.
	 */
	private String trimString(String input) {

		//Check to see if the input lengity is greater than 14 characters.
		if (input.length() > 14) {
			int ammountOver = input.length() - 14;
			String newString = input.substring(0, input.length() - ammountOver - 2) + "..";
			return newString;
		} else {
			//The input is less than 15 characters so it does not need to be trimmed.
			return input;
		}
	}

	/**
	 * Adds a player and gives them a scoreboard.
	 * 
	 * @param player The player that will receive a scoreboard.
	 */
	public void addPlayer(Player player) {
		//Give the player the main scoreboard.
		player.setScoreboard(arenaScoreboard);

		//Set the team.
		setPlayerTeam(player);

		//Setup player's scoreboard.
		//TODO: Clean this up.
		if (PLUGIN.getGameLobby().getPlayerProfile().get(player).isSpectator()) {
			setScores(lastScoreMapSent);
		}
	}

	/**
	 * Unregisters all the given objectives for the scoreboard.
	 * @param board The board that will have all objectives removed form it.
	 */
	private void unregisterObjectives(Scoreboard board) {
		//Check to make sure the board objectives are not null.
		if (board != null) {
			//Loop through all objectives and unregister them.
			for(Objective objectives : board.getObjectives()) {
				objectives.unregister();
			}
		}
	}
}
