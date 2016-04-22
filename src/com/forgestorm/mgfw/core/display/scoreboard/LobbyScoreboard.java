package com.forgestorm.mgfw.core.display.scoreboard;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.api.MinigameKits;
import com.forgestorm.mgfw.api.MinigameTeams;
import com.forgestorm.mgfw.core.GameManager;
import com.forgestorm.mgfw.core.MinigamePluginManager;
import com.forgestorm.mgfw.core.constants.Messages;
import com.forgestorm.mgfw.selector.TeamSelector;


public class LobbyScoreboard {

	private final MGFramework PLUGIN;

	private ScoreboardManager manager;
	private Scoreboard lobbyScoreboard;
	private ArrayList<Team> teams;
	private int gameWaitingAnamate;


	public LobbyScoreboard(MGFramework plugin) {
		PLUGIN = plugin;
		manager = Bukkit.getScoreboardManager();
		teams = new ArrayList<Team>();
		gameWaitingAnamate = 1;
	}
	
	/**
	 * Creates a new scoreboard for the minigame lobby.
	 * <p>
	 * This should happen every time a new game is loaded into the framework.
	 */
	public void createScoreboard() {
		MinigameTeams minigameTeam = PLUGIN.getMinigamePluginManager().getMinigameTeams();
		
		lobbyScoreboard = manager.getNewScoreboard();
		
		//Create Teams
		for (int i = 0; i < minigameTeam.getTeamNames().size(); i++) {
			String teamName = trimString(minigameTeam.getTeamNames().get(i));
			ChatColor teamColor = minigameTeam.getTeamColors().get(i);
			Team sbTeam = lobbyScoreboard.registerNewTeam(teamName);
		
			//Set this scoreboard team prefix.
			sbTeam.setPrefix(teamColor + "");
			
			//Add this scoreboard team to the teams array.
			teams.add(sbTeam);
			
			//TODO: Rank refixes + colors
			//team.setPrefix(rankPrefix + teamColor + "");
		}
	}
	
	/**
	 * This will remove the current lobby scoreboard and components.
	 */
	public void destroyScoreboard() {
		lobbyScoreboard = null;
		teams.clear();
		gameWaitingAnamate = 0;
	}
	
	/**
	 * Gives a player a scoreboard objective.
	 * @param player The player who will receive a lobby scoreboard objective.
	 */
	private void setBoardObjective(Player player) {
		Scoreboard board = lobbyScoreboard;
		
		//Unregister all current objectives on the scoreboard.
		//unregisterObjectives(board);
		unregisterObjective(player);	
		
		//Set the board objective.
		Objective objective = lobbyScoreboard.registerNewObjective(player.getName(), "dummy");
		
		//Set the board title.
		String name = Messages.SB_LOBBY_TITLE.toString();
		objective.setDisplayName(name);

		//Make sure the display slot is the sidebar.
		if(objective.getDisplaySlot() != DisplaySlot.SIDEBAR) {
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		
		//Set the scores.
		setScores(player, objective);
		
		//Sends the player the current scoreboard.
		player.setScoreboard(board);
	}
	
	/**
	 * This will put a player on a team.
	 * @param player The player what will be placed on a team.
	 */
	private void setPlayerTeam(Player player) {
		TeamSelector teamSelector = PLUGIN.getGameManager().getTeamSelector();
		int playerTeam = teamSelector.getPlayerTeam(player);
		
		teams.get(playerTeam).addEntry(player.getName());		
	}

	/**
	 * Set the scores for the lobby scoreboard.
	 * @param player The player that we are setting scores for.
	 * @param objective The objective object we are working with.
	 */
	private void setScores(Player player, Objective objective) {
		GameManager gameManager = PLUGIN.getGameManager();
		MinigamePluginManager mpm = PLUGIN.getMinigamePluginManager();
		MinigameKits minigameKit = mpm.getMinigameKit();
		MinigameTeams minigameTeam = mpm.getMinigameTeams();

		int currentPlayers = Bukkit.getOnlinePlayers().size();
		int maxPlayers = gameManager.getMaxPlayers();

		String kit = minigameKit.getKitNames().get(gameManager.getKitSelector().getPlayerKit(player));
		String team = minigameTeam.getTeamNames().get(gameManager.getTeamSelector().getPlayerTeam(player));
		String gameName = mpm.getMinigameBase().getMinigameName();
		
		//Blank line 1
		objective.getScore(Messages.SB_BLANK_LINE_1.toString()).setScore(15);

		//Games Stats
		objective.getScore(Messages.SB_GAME_STATUS.toString()).setScore(14);

		//Game Status
		if(currentPlayers >= gameManager.getMinPlayers()) {
			objective.getScore(Messages.SB_GAME_STATUS_READY.toString()).setScore(13);
		} else {
			if(gameWaitingAnamate == 1) {
				objective.getScore(Messages.SB_GAME_STATUS_WAITING_1.toString()).setScore(13);
				gameWaitingAnamate++;
			} else if (gameWaitingAnamate == 2) {
				objective.getScore(Messages.SB_GAME_STATUS_WAITING_2.toString()).setScore(13);
				gameWaitingAnamate++;
			} else if (gameWaitingAnamate == 3) {
				objective.getScore(Messages.SB_GAME_STATUS_WAITING_3.toString()).setScore(13);
				gameWaitingAnamate = 1;
			}
		}

		//Blank line 2
		objective.getScore(Messages.SB_BLANK_LINE_2.toString()).setScore(12);

		//Players total
		objective.getScore(Messages.SB_PLAYERS.toString()).setScore(11);

		//Player count
		String pCount = Messages.SB_PLAYER_COUNT.toString().replace("%s", Integer.toString(currentPlayers)).replace("%f", Integer.toString(maxPlayers));
		objective.getScore(pCount).setScore(10);

		//Blank line 3
		objective.getScore(Messages.SB_BLANK_LINE_3.toString()).setScore(9);

		//Kit
		objective.getScore(Messages.SB_KIT.toString()).setScore(8);

		//Player Kit
		objective.getScore(trimString(kit)).setScore(7);

		//Blank line 4
		objective.getScore(Messages.SB_BLANK_LINE_4.toString()).setScore(6);

		//Team
		objective.getScore(Messages.SB_TEAM.toString()).setScore(5);

		//Player Team
		objective.getScore(trimString(team)).setScore(4);

		//Blank line 5
		objective.getScore(Messages.SB_BLANK_LINE_5.toString()).setScore(3);

		//Next game:
		objective.getScore(Messages.SB_NEXT_GAME.toString()).setScore(2);

		//Next game name.
		objective.getScore(ChatColor.AQUA + trimString(gameName)).setScore(1);
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
		player.setScoreboard(lobbyScoreboard);
		
		//Set the team.
		setPlayerTeam(player);
		
		//Setup player's scoreboard.
		setBoardObjective(player);
	}

	/**
	 * Removes a player scoreboard..
	 * 
	 * @param player The player that will have their scoreboard removed.
	 */
	public void removePlayer(Player player) {
		TeamSelector teamSelector = PLUGIN.getGameManager().getTeamSelector();
		int playerTeam = teamSelector.getPlayerTeam(player);
		
		//Give the player a new "blank" scoreboard.
		player.setScoreboard(manager.getNewScoreboard());
		
		//Remove the player from the 
		teams.get(playerTeam).removeEntry(player.getName());	
	}

	/**
	 * Update a players scoreboard.
	 * @param player The player scoreboard we will update.
	 */
	public void updatePlayerScoreboard(Player player) {
		removePlayer(player);
		addPlayer(player);
	}

	/**
	 * Update all players scoreboard.
	 */
	public void updateAllPlayerScoreboards() {
		for (Player players: Bukkit.getOnlinePlayers()) {
			updatePlayerScoreboard(players);
		}
	}

	/**
	 * Unregisters all the given objectives for the scoreboard.
	 * @param board The board that will have all objectives removed form it.
	 */
	private void unregisterObjective(Player player) {
		//Check to make sure the board objectives are not null.
		if (player.getScoreboard() != null) {
			
			//Unregister the players objective.
			if (player.getScoreboard().getObjective(player.getName()) != null) {
				player.getScoreboard().getObjective(player.getName()).unregister();
			}
			
		}
	}

	/**
	 * Unregisters all the given objectives for the scoreboard.
	 * @param board The board that will have all objectives removed form it.
	 */
	public void unregisterObjectives(Scoreboard board) {
		//Check to make sure the board objectives are not null.
		if (board != null) {
			//Loop through all objectives and unregister them.
			for(Objective objectives : board.getObjectives()) {
				objectives.unregister();
			}
		}
	}
}
