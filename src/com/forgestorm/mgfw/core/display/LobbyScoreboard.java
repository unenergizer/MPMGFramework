package com.forgestorm.mgfw.core.display;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.api.MinigameKits;
import com.forgestorm.mgfw.api.MinigameTeams;
import com.forgestorm.mgfw.core.GameManager;
import com.forgestorm.mgfw.core.MinigamePluginManager;
import com.forgestorm.mgfw.core.constants.Messages;

import net.md_5.bungee.api.ChatColor;

public class LobbyScoreboard {

	private final MGFramework PLUGIN;

	private ScoreboardManager manager;
	private HashMap<Player, Scoreboard> playerBoards;
	private int gameWaitingAnamate;


	public LobbyScoreboard(MGFramework plugin) {
		PLUGIN = plugin;
		manager = Bukkit.getScoreboardManager();
		playerBoards = new HashMap<Player, Scoreboard>();
		gameWaitingAnamate = 1;
	}

	/**
	 * Gives a player a scoreboard that has lobby text.
	 * @param player The player who will receive a lobby scoreboard.
	 */
	private void setBoardToPlayer(Player player) {
		Scoreboard board = playerBoards.get(player);

		//If the player already 
		if (player.getScoreboard() != null) {
			playerBoards.remove(player);
			playerBoards.put(player, player.getScoreboard());
			board = player.getScoreboard();
		}

		//Unregister all current objectives on the scoreboard.
		unregisterObjectives(board);

		//Set the board objective.
		Objective objective = board.registerNewObjective("test", "dummy");

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
		int maxPlayers = gameManager.getMAX_PLAYERS();
		
		String kit = minigameKit.getKitNames().get(gameManager.getKitSelector().getPlayerKit(player));
		String team = minigameTeam.getTeamNames().get(gameManager.getTeamSelector().getPlayerTeam(player));
		String gameName = mpm.getMinigameBase().getMinigameName();

		//Blank line 1
		objective.getScore(Messages.SB_BLANK_LINE_1.toString()).setScore(15);

		//Games Stats
		objective.getScore(Messages.SB_GAME_STATUS.toString()).setScore(14);
		
		//Game Status
		if(gameManager.shouldMinigameStart()) {
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
	public String trimString(String input) {
		int ammountOver = input.length() - 14;
		String newString = input.substring(0, input.length() - ammountOver - 2) + "..";
		return newString;
	}

	/**
	 * Adds a player and gives them a scoreboard.
	 * 
	 * @param player The player that will receive a scoreboard.
	 */
	public void addPlayer(Player player) {
		//Give the player a new "blank" scoreboard.
		player.setScoreboard(manager.getNewScoreboard());

		Scoreboard board = manager.getNewScoreboard();
		playerBoards.put(player, board);
		setBoardToPlayer(player);
	}

	/**
	 * Removes a player scoreboard..
	 * 
	 * @param player The player that will have their scoreboard removed.
	 */
	public void removePlayer(Player player) {

		//Remove all of the boards current objectives.
		unregisterObjectives(playerBoards.get(player));

		//Remove the player from the Scoreboard HashMap.
		playerBoards.remove(player);

		//Give the player a new "blank" scoreboard.
		player.setScoreboard(manager.getNewScoreboard());
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
