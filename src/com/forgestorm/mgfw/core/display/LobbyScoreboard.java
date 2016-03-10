package com.forgestorm.mgfw.core.display;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class LobbyScoreboard {
	
	public void createPlayerScoreboard(Player player) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Team team = board.registerNewTeam(player.getName());
		//team.getScoreboard().
	}
}
