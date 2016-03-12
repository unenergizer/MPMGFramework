package com.forgestorm.mgfw.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.GameLobby;
import com.forgestorm.mgfw.core.GameManager;

public class PlayerQuit implements Listener {

	private final MGFramework PLUGIN;

	public PlayerQuit(MGFramework plugin) {
		PLUGIN = plugin;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);

		GameManager gameManager = PLUGIN.getGameManager();
		GameLobby gameLobby = PLUGIN.getGameLobby();
		Player player = event.getPlayer();

		//Remove the player from the players hashmap.
		gameLobby.getPlayerProfile().remove(player);

		//Test if the game is running.  If it is, teleport the player to the game world.
		if (gameManager.isMinigameRunning()) {

			//Check to see if the game should end.
			if (gameManager.shouldMinigameEnd()) {

				//End the game!
				gameManager.endGame(true);

				Bukkit.getLogger().warning("[MPMGFramework] Ending game early. No players online!");
			}

		} else {
			//Remove the player from the lobby.
			gameLobby.removeLobbyPlayer(player);
			
			//Update scoreboards
			updateScoreboard(gameLobby);
		}
	}

	/**
	 * Waits X amount of ticks to teleport player to the designated spawn location.
	 * 
	 * @param player The player to teleport.
	 * @param location The location to teleport the player to.
	 */
	private void updateScoreboard(GameLobby gameLobby) {
		new BukkitRunnable() {
			@Override
			public void run() {
				//Update scoreboard for all players
				for(Player players: Bukkit.getOnlinePlayers()) {
					gameLobby.getScoreboard().updatePlayerScoreboard(players);
				}
			}
		}.runTaskLater(PLUGIN, 10);
	}
}
