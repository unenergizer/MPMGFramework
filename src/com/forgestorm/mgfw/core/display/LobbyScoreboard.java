package com.forgestorm.mgfw.core.display;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.constants.Messages;

public class LobbyScoreboard {

	private final MGFramework PLUGIN;
	
	private ScoreboardManager manager;
	private HashMap<Player, Scoreboard> playerBoards;
	//private HashMap<Player, ArrayList<String>> objectives;
	
	
	public LobbyScoreboard(MGFramework plugin) {
		PLUGIN = plugin;
		manager = Bukkit.getScoreboardManager();
		//objectives = new HashMap<Player, ArrayList<String>>();
		playerBoards = new HashMap<Player, Scoreboard>();
	}
	
	private void setBoardToPlayer(Player player) {
		Scoreboard board = playerBoards.get(player);
		
		//If the player already 
		if (player.getScoreboard() != null) {
			playerBoards.remove(player);
			playerBoards.put(player, player.getScoreboard());
			board = player.getScoreboard();
		}
		
		int random = new Random().nextInt(5999);
		int random1 = new Random().nextInt(5999);
		
		//Set the board objective.
		Objective obj = board.registerNewObjective("sb" + Integer.toString(random), "ct" + Integer.toString(random1));
		String name = Messages.SB_LOBBY_TITLE.toString();
		obj.setDisplayName(name);
		
		//Makesure the display slot is the sidebar.
		if(obj.getDisplaySlot() != DisplaySlot.SIDEBAR) {
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		
		//Try adding a test score.
		Score score = obj.getScore(player.getName());
		score.setScore(15);
		
		//Sends the player the current scoreboard.
		player.setScoreboard(board);
	}
	
	public void addPlayer(Player player) {
		playerBoards.put(player, manager.getNewScoreboard());
		setBoardToPlayer(player);
	}
	
	public void removePlayer(Player player) {
		//Remove the player from the HashMap.
		playerBoards.remove(player);
		
		//Give the player a new "blank" scoreboard.
		player.setScoreboard(manager.getNewScoreboard());
	}
	
}
