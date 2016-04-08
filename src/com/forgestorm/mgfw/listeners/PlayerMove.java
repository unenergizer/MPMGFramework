package com.forgestorm.mgfw.listeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.GameArena;
import com.forgestorm.mgfw.core.GameManager;
import com.forgestorm.mgfw.core.constants.GameState;

public class PlayerMove implements Listener {

	private MGFramework PLUGIN;

	public PlayerMove(MGFramework plugin){
		PLUGIN = plugin;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		GameManager gameManager = PLUGIN.getGameManager();
		GameState gameState = gameManager.getGameState();
		GameArena gameArena = gameManager.getGAME_ARENA();

		//Stop the player from moving if the game is showing the rules.
		if (gameState.equals(GameState.ARENA_SHOW_RULES)) {
			Player player = event.getPlayer();
			boolean isSpectator = gameManager.getGAME_LOBBY().getPlayerProfile().get(player).isSpectator();
			
			double moveX = event.getFrom().getX();
			double moveZ = event.getFrom().getZ();
			
			double moveToX = event.getTo().getX();
			double moveToZ = event.getTo().getZ();
			
			//If the countdown has started, then let the player look around.
			if ((moveX != moveToX || moveZ != moveToZ) && !isSpectator) {
	
				HashMap<Player, Location> playerSpawns = gameArena.getPlayerSpawns();
				
				//Teleport player back to their arena spawn location.
				player.teleport(playerSpawns.get(player));
			}
		}
	}
}
