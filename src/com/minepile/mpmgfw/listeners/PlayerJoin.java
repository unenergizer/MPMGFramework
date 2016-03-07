package com.minepile.mpmgfw.listeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.minepile.mpmgfw.MPMGFramework;
import com.minepile.mpmgfw.core.GameArena;
import com.minepile.mpmgfw.core.GameLobby;
import com.minepile.mpmgfw.core.GameManager;
import com.minepile.mpmgfw.core.constants.GameState;
import com.minepile.mpmgfw.profiles.PlayerProfile;

public class PlayerJoin implements Listener {

	private final MPMGFramework PLUGIN;

	public PlayerJoin(MPMGFramework plugin) {
		PLUGIN = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);

		Player player = event.getPlayer();
		GameManager gameManager = PLUGIN.getGameManager();
		GameArena gameArena = PLUGIN.getGameArena();
		GameLobby lobby = PLUGIN.getGameLobby();
		Location lobbySpawn = lobby.getLobbySpawn();
		HashMap<Player, PlayerProfile> playerProfile = gameManager.getPlayerProfile();
		GameState gameState = gameManager.getGameState();
		
		//Setup lobby player profiles.
		if (!playerProfile.containsKey(player)) {
			playerProfile.put(player, new PlayerProfile(player));
		}
		
		//Test if the game is running.  If it is, teleport the player to the game world.
		if (gameManager.isMinigameRunning()) {
			
			//Set the player as a spectator.
			playerProfile.get(player).setSpectator(true);
			
			//Teleport the player to a spectator spawn in the game world.
			//TODO: Get a real spectator spawn point from the minigame plugin.
			gameArena.tpToGameWorld(player, 0, 100, 0);
			
		} else {
			//Teleport the player to the lobby.
			teleport(player, lobbySpawn);
			
			//Show the player the bossbar.
			gameManager.getBar().showBossBar(player);
			
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
