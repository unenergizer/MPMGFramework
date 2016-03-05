package com.minepile.mpmgfw.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.minepile.mpmgfw.MPMGFramework;
import com.minepile.mpmgfw.core.GameManager;
import com.minepile.mpmgfw.core.GameLobby;

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
		GameLobby lobby = PLUGIN.getGameLobby();
		Location lobbySpawn = lobby.getLobbySpawn();

		//Test if the game is running.  If it is, teleport the player to the game world.
		if (gameManager.isMinigameRunning()) {
			//TODO: teleport the new player to the game world as a spectator.

		} else {

			//If the game has enough players to start, lets do that now.
			if (gameManager.shouldMinigameStart()) {
				gameManager.startCountdown();
				teleport(player, lobbySpawn);

			} else {
				//The game does not have enough players.
				teleport(player, lobbySpawn);
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
