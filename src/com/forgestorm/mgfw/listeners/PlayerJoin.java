package com.forgestorm.mgfw.listeners;

import org.bukkit.Bukkit;
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
import com.forgestorm.mgfw.core.constants.Messages;
import com.forgestorm.mgfw.profiles.PlayerProfile;

public class PlayerJoin implements Listener {

	private final MGFramework PLUGIN;

	public PlayerJoin(MGFramework plugin) {
		PLUGIN = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		//Get the players profile!
		final PlayerProfile PROFILE = new PlayerProfile(player);
		PLUGIN.getProfiles().put(player.getUniqueId(), PROFILE);
		
		GameManager gameManager = PLUGIN.getGameManager();
		GameArena gameArena = gameManager.getGameArena();
		GameLobby gameLobby = gameManager.getGameLobby();
		Location lobbySpawn = gameLobby.getLobbySpawn();
		GameState gameState = gameManager.getGameState();
		String prefix = PROFILE.getPrefix();
		
		String playerName = PROFILE.getName();
		String joinMessage = "";
		
		//Add prefix if the player has one.
		if (prefix != null && !prefix.equals("")) {
			playerName = prefix + playerName.concat(playerName);
		}
		
		//Test if the game is running.  If it is, teleport the player to the game world.
		if (gameManager.isMinigameRunning()) {
			
			PLUGIN.getProfile(player).setSpectator(true);
			
			//Setup the player as a spectator.
			gameArena.setupSpectator(player);
			
			//Show spectator join message.
			joinMessage = joinMessage.concat(Messages.SPECTATOR_JOIN.toString().replace("%s", playerName));
			
		} else {
			String onlinePlayers = Integer.toString(Bukkit.getOnlinePlayers().size());
			String maxPlayers = Integer.toString(gameManager.getMaxPlayers());
			
			//Teleport the player to the lobby.
			teleport(player, lobbySpawn);
			
			//Setup a lobby player.
			gameLobby.setupLobbyPlayer(player);
			
			//Update scoreboard for all players
			gameLobby.getScoreboard().updateAllPlayerScoreboards();
			
			//Show join message.
			joinMessage = joinMessage.concat(Messages.PLAYER_JOIN_LOBBY.toString().toString()
				.replace("%s", onlinePlayers) //Number of players.
				.replace("%f", maxPlayers) //Max Players Allowed
				.replace("%e", playerName)); //Player Name
			
			//If the game has enough players to start, lets do that now.
			if (gameManager.shouldMinigameStart() && !gameState.equals(GameState.LOBBY_COUNTDOWN)) {
				gameManager.startCountdown();
			}
		}
		
		//Show the join message.
		event.setJoinMessage(joinMessage);
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
