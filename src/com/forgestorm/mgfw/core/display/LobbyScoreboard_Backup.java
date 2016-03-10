package com.forgestorm.mgfw.core.display;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.api.MinigameKits;
import com.forgestorm.mgfw.api.MinigameTeams;
import com.forgestorm.mgfw.core.MinigamePluginManager;

import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("deprecation")
public class LobbyScoreboard_Backup {
	
	private final MGFramework PLUGIN;
	
	private Scoreboard board;
	private Objective objective;
	private HashMap<String, Team> teams;
	private String tempObjectiveName, tempDisplayName;
	private boolean updateText;
	
	public LobbyScoreboard_Backup(MGFramework plugin) {
		PLUGIN = plugin;
		teams = new HashMap<String, Team>();
		updateText = true;
	}
	
	/**
	 * This will setup a new scoreboard.
	 * 
	 * @param objectiveName The name of the current objective.
	 * @param displayName The name to display at the top of the scoreboard.
	 */
	public void setup(String objectiveName, String displayName) {
		setTempObjectiveName(objectiveName);
		setTempDisplayName(displayName);
		
		board = Bukkit.getScoreboardManager().getNewScoreboard();
		
		objective = board.registerNewObjective(objectiveName, "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + displayName);
	}
	
	/**
	 * Updates the text of the scoreboard for all players in the lobby.
	 */
	public void updateLobbyText() {
		
		new BukkitRunnable() {

			@Override
			public void run() {
				//Update all players lobby text.
				for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
					setLobbyText(onlinePlayers);
				}
				
				if (!updateText) {
					cancel();
				}
			}
		}.runTaskTimer(PLUGIN, 0, 20);
	}
	
	/**
	 * Sets the text for the lobby scorebaord.
	 * @param player The player who will get the custom scoreboard text.
	 */
	public void setLobbyText(Player player) {
		//Reset the previous scores.
		board.resetScores(player);
		
		//BLANK LINE
		setPoints(player, Bukkit.getOfflinePlayer(ChatColor.RESET.toString()), 15);
		
		//SHOW GAME STATUS
		setPoints(player, Bukkit.getOfflinePlayer(ChatColor.BOLD + "Status: "), 14);
		
		//If the game needs more players, display that to the user. Otherwise game is ready.
		if (PLUGIN.getGameManager().shouldMinigameStart()) {
			setPoints(player, Bukkit.getOfflinePlayer(ChatColor.GREEN + "Game ready!"), 13);
		} else {
			setPoints(player, Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Need players.."), 13);
		}
		
		//BLANK LINE
		setPoints(player, Bukkit.getOfflinePlayer(ChatColor.RESET.toString() + ChatColor.RESET.toString()), 12);
		
		//SHOW TOTAL PLAYERS
		setPoints(player, Bukkit.getOfflinePlayer(ChatColor.BOLD + "Players: "), 11);
		setPoints(player, Bukkit.getOfflinePlayer(ChatColor.GREEN + Integer.toString(Bukkit.getOnlinePlayers().size()) + " / " + Integer.toString(PLUGIN.getGameManager().getMAX_PLAYERS())), 10);
		
		//BLANK LINE
		setPoints(player, Bukkit.getOfflinePlayer(ChatColor.RESET.toString() + ChatColor.RESET.toString() + ChatColor.RESET.toString()), 9);
		
		//SHOW PLAYER SELECTED KIT
		MinigameKits minigameKit = PLUGIN.getMinigamePluginManager().getMinigameKit();
		int playerKit = PLUGIN.getGameManager().getKitSelector().getPlayerKit(player);
		String kitName = minigameKit.getKitNames().get(playerKit);
		
		setPoints(player, Bukkit.getOfflinePlayer(ChatColor.BOLD + "Kit: "), 8);
		if(kitName.length() > 14) {
			setPoints(player, Bukkit.getOfflinePlayer(trimString(kitName)), 7);
		} else {
			setPoints(player, Bukkit.getOfflinePlayer(ChatColor.GOLD + kitName), 7);
		}
		
		//BLANK LINE
		setPoints(player, Bukkit.getOfflinePlayer(ChatColor.RESET.toString() + ChatColor.RESET.toString() + ChatColor.RESET.toString() + ChatColor.RESET.toString()), 6);
		
		//SHOW PLAYER SELECTED TEAM
		MinigameTeams minigameTeams = PLUGIN.getMinigamePluginManager().getMinigameTeams();
		int playerTeam = PLUGIN.getGameManager().getTeamSelector().getPlayerTeam(player);
		String teamName = minigameTeams.getTeamNames().get(playerTeam);
		
		setPoints(player, Bukkit.getOfflinePlayer(ChatColor.BOLD + "Team: "), 5);
		if(teamName.length() >= 14) {
			setPoints(player, Bukkit.getOfflinePlayer(trimString(teamName)), 4);
		} else {
			setPoints(player, Bukkit.getOfflinePlayer(teamName), 4);
		}
		
		//BLANK LINE
		setPoints(player, Bukkit.getOfflinePlayer(ChatColor.RESET.toString() + ChatColor.RESET.toString() + ChatColor.RESET.toString() + ChatColor.RESET.toString() + ChatColor.RESET.toString()), 3);
		
		//SHOW GAME NAME
		MinigamePluginManager mpm = PLUGIN.getMinigamePluginManager();
		String gameName = mpm.getMinigameBase().getMinigameName();

		setPoints(player, Bukkit.getOfflinePlayer(ChatColor.BOLD + "Next Game: "), 2);
		if(gameName.length() > 14) {
			setPoints(player, Bukkit.getOfflinePlayer(ChatColor.AQUA + trimString(gameName)), 1);
		} else {
			setPoints(player, Bukkit.getOfflinePlayer(ChatColor.AQUA + gameName), 1);
		}
	}
	
	/**
	 * Trims a string to make sure it will not crash the client.
	 * @param input The string that will get formatting.
	 * @return A formatted string that is less than 14 characters long.
	 */
	public String trimString(String input) {
		int ammountOver = input.length() - 14;
		String newString = input.substring(0, input.length() - ammountOver - 2) + "..";
		return newString;
	}
	
	/**
	 * Sets up a new team.
	 * 
	 * @param name The name of the team.
	 * @param canSeeFriendlyInvisibles
	 * @param allowFriendlyFire
	 * @param prefix
	 */
	public void setupTeam(String name, boolean canSeeFriendlyInvisibles, boolean allowFriendlyFire, String prefix) {
		Team tempTeam = board.registerNewTeam(name);
		tempTeam.setCanSeeFriendlyInvisibles(canSeeFriendlyInvisibles);
		tempTeam.setAllowFriendlyFire(allowFriendlyFire);
		tempTeam.setPrefix(prefix);
		
		teams.put(name, tempTeam);
	}
	
	/**
	 * Adds a player to a team.
	 * @param player The player who will be added to a team.
	 * @param team The team the player will be added to.
	 */
	public void addPlayer(Player player, String team) {
		teams.get(team).addPlayer(player);
	}
	
	/**
	 * Removes a player from a scoreboard.
	 * @param player The player who will be removed from the scoreboard.
	 */
	public void removePlayer(Player player) {
		board.resetScores(player);
		updateAllScoreboards();
	}
	
	/**
	 * Removes every players scoreboard.
	 */
	public void removeAllScoreboards() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			try {
				player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME).unregister();
			} catch (NullPointerException exception) {}
			try {
				player.getScoreboard().getObjective(DisplaySlot.PLAYER_LIST).unregister();
			} catch (NullPointerException exception) {}
			try {
				player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
			} catch (NullPointerException exception) {}
		}
	}

	public void addPoint(Player player, int points) {
		Score score = objective.getScore(player);
		score.setScore(score.getScore() + points);
		updateScoreboard(player);
	}
	
	public void addPoint(Player player, OfflinePlayer offlinePlayer, int points) {
		Score score = objective.getScore(offlinePlayer);
		score.setScore(score.getScore() + points);
		updateScoreboard(player);
	}
	
	public void addPointUpdateAll(Player player, OfflinePlayer offlinePlayer, int points) {
		for(Player onlinePlayers : Bukkit.getOnlinePlayers()) {
			Score score = objective.getScore(offlinePlayer);
			score.setScore(score.getScore() + points);
			updateScoreboard(onlinePlayers);
		}
	}
	
	public void setPoints(Player player, OfflinePlayer offlinePlayer, int points) {
		Score score = objective.getScore(offlinePlayer);
		score.setScore(points);
		updateScoreboard(player);
	}
	
	public int getPoints(Player player) {
		int points = objective.getScore(player).getScore();
		return points;
	}
	
	public int getOfflinePlayerPoints(OfflinePlayer offlinePlayer) {
		int points = objective.getScore(offlinePlayer).getScore();
		return points;
	}
	
	public void removePoints(Player player, OfflinePlayer offlinePlayer) {
		Score score = objective.getScore(offlinePlayer);
		score.setScore(0);
		updateScoreboard(player);
	}
	
	public void updateScoreboard(Player player) {
		player.setScoreboard(board);
	}
	
	public void updateAllScoreboards() {
		for (Player players : Bukkit.getOnlinePlayers()) {
			players.setScoreboard(board);
		}
	}

	public Scoreboard getBoard() {
		return board;
	}

	public void setBoard(Scoreboard board) {
		this.board = board;
	}

	public String getTempObjectiveName() {
		return tempObjectiveName;
	}

	public void setTempObjectiveName(String tempObjectiveName) {
		this.tempObjectiveName = tempObjectiveName;
	}

	public String getTempDisplayName() {
		return tempDisplayName;
	}

	public void setTempDisplayName(String tempDisplayName) {
		this.tempDisplayName = tempDisplayName;
	}

	public HashMap<String, Team> getTeams() {
		return teams;
	}

	public void setTeams(HashMap<String, Team> teams) {
		this.teams = teams;
	}

	public boolean isUpdateText() {
		return updateText;
	}

	public void setUpdateText(boolean updateText) {
		this.updateText = updateText;
	}
}
