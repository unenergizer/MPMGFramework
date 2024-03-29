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
import com.forgestorm.mgfw.core.constants.Messages;
import com.forgestorm.mgfw.profiles.PlayerProfile;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlayerQuit implements Listener {

	private final MGFramework PLUGIN;
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		GameManager gameManager = PLUGIN.getGameManager();
		GameLobby gameLobby = gameManager.getGameLobby();
		boolean isRunning = gameManager.isMinigameRunning();
		Player player = event.getPlayer();
		PlayerProfile profile = PLUGIN.getProfile(player);
		String prefix = profile.getPrefix();
		
		String playerName = profile.getName();
		String quitMessage = "";
		
		//Add prefix if the player has one.
		if (prefix != null && !prefix.equals("")) {
			playerName = prefix + playerName.concat(playerName);
		}
		
		//Test if the game is running.  If it is, teleport the player to the game world.
		if (isRunning) {
			
			//Test to see if the minigame should end.
			shouldMinigameEnd(gameManager);
			
			if (profile.isSpectator()) {
				//Show spectator quit message.
				quitMessage = quitMessage.concat(Messages.SPECTATOR_QUIT.toString().replace("%s", playerName));
			} else {
				//Show player quit game message.
				quitMessage = quitMessage.concat(Messages.PLAYER_QUIT_GAME.toString().replace("%s", playerName));
			}
		} else {
			String onlinePlayers = Integer.toString(Bukkit.getOnlinePlayers().size() - 1);
			String maxPlayers = Integer.toString(gameManager.getMaxPlayers());
			
			//Show lobby quit message.
			quitMessage = quitMessage.concat(Messages.PLAYER_QUIT_LOBBY.toString().toString()
				.replace("%s", onlinePlayers) //Number of players.
				.replace("%f", maxPlayers) //Max Players Allowed
				.replace("%e", playerName)); //Player Name
			
			//Remove the player from the lobby.
			gameLobby.removeQuitPlayer(player);
			
			//Update scoreboards
			updateScoreboard(gameLobby);
		}
		
		//Remove the player from the players hashmap.
		PLUGIN.getRemovedProfile(player);
		
		//Tell minigames a player quit.
		PLUGIN.getMinigamePluginManager().getMinigameBase().onPlayerQuit(player);
		
		//Show the quit message.
		event.setQuitMessage(quitMessage);
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
