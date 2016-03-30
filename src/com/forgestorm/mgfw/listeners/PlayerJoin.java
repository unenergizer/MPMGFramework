package com.forgestorm.mgfw.listeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.GameArena;
import com.forgestorm.mgfw.core.GameLobby;
import com.forgestorm.mgfw.core.GameManager;
import com.forgestorm.mgfw.core.constants.GameState;
import com.forgestorm.mgfw.profiles.PlayerProfile;

public class PlayerJoin implements Listener {

	private final MGFramework PLUGIN;

	public PlayerJoin(MGFramework plugin) {
		PLUGIN = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);

		Player player = event.getPlayer();
		GameManager gameManager = PLUGIN.getGameManager();
		GameArena gameArena = PLUGIN.getGameArena();
		GameLobby gameLobby = PLUGIN.getGameLobby();
		Location lobbySpawn = gameLobby.getLobbySpawn();
		HashMap<Player, PlayerProfile> playerProfile = gameLobby.getPlayerProfile();
		GameState gameState = gameManager.getGameState();
		
		//Setup lobby player profiles.
		if (!playerProfile.containsKey(player)) {
			playerProfile.put(player, new PlayerProfile(player));
		}
		
		//Test if the game is running.  If it is, teleport the player to the game world.
		if (gameManager.isMinigameRunning()) {
			
			playerProfile.get(player).setSpectator(true);
			
			//Setup the player as a spectator.
			gameArena.setupSpectator(player);
		} else {
			//Teleport the player to the lobby.
			teleport(player, lobbySpawn);
			
			//Setup a lobby player.
			gameLobby.setupLobbyPlayer(player);
			
			//Update scoreboard for all players
			gameLobby.getScoreboard().updateAllPlayerScoreboards();
			
			//If the game has enough players to start, lets do that now.
			if (gameManager.shouldMinigameStart() && !gameState.equals(GameState.LOBBY_COUNTDOWN)) {
				gameManager.startCountdown();
			}
		}
	}

	/**
	 * Waits X amount of ticks to teleport player to the designated spawn location.
	 * 
	 * @param player The player to teleport.
	 * @param location The location to teleport the player to.
	 */
	private void teleport(final Player player, final Location location) {
		new BukkitRunnable() {
			@Override
			public void run() {
				player.teleport(location);
			}
		}.runTaskLater(PLUGIN, 10);
	}
}
