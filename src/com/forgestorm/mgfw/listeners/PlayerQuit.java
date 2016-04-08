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
		GameLobby gameLobby = gameManager.getGAME_LOBBY();
		boolean isRunning = gameManager.isMinigameRunning();
		Player player = event.getPlayer();

		//Remove the player from the players hashmap.
		gameLobby.getPlayerProfile().remove(player);
		
		//Tell minigames a player quit.
		PLUGIN.getMinigamePluginManager().getMinigameBase().onPlayerQuit(player);
		
		//Test if the game is running.  If it is, teleport the player to the game world.
		if (isRunning) {
			
			//Test to see if the minigame should end.
			shouldMinigameEnd(gameManager);
		} else {
			//Remove the player from the lobby.
			gameLobby.removeQuitPlayer(player);
			
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
				gameLobby.getScoreboard().updateAllPlayerScoreboards();
			}
		}.runTaskLater(PLUGIN, 10);
	}


	private void shouldMinigameEnd(GameManager gameManager) {
		new BukkitRunnable() {
			@Override
			public void run() {
				//Check to see if the game should end.
				if (gameManager.shouldMinigameEnd()) {

					//Show warning in the logger.
					Bukkit.getLogger().warning("[MPMGFramework] Ending game early. No players online!");
					
					//End the game!
					gameManager.endGame(true);
				}
			}
		}.runTaskLater(PLUGIN, 10);
	}
}
