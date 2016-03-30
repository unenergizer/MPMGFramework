package com.forgestorm.mgfw.listeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.GameArena;
import com.forgestorm.mgfw.core.constants.GameState;

public class PlayerMove implements Listener {

	private MGFramework PLUGIN;

	public PlayerMove(MGFramework plugin){
		PLUGIN = plugin;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		GameState gameState = PLUGIN.getGameManager().getGameState();
		GameArena gameArena = PLUGIN.getGameArena();

		//Stop the player from moving if the game is showing the rules.
		if (gameState.equals(GameState.ARENA_SHOW_RULES)) {
			Player player = event.getPlayer();
			boolean isSpectator = PLUGIN.getGameLobby().getPlayerProfile().get(player).isSpectator();
			
			//If the countdown has started, then let the player look around.
			if (!event.getFrom().toVector().equals(event.getTo().toVector()) && !isSpectator) {
				HashMap<Player, Location> playerSpawns = gameArena.getPlayerSpawns();
				
				//Teleport player back to their arena spawn location.
				player.teleport(playerSpawns.get(player));
			}
		}
	}
}
