package com.forgestorm.mgfw.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.GameArena;
import com.forgestorm.mgfw.core.GameManager;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlayerInteract implements Listener {

	private MGFramework PLUGIN;

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		Player player = (Player) event.getPlayer();
		GameManager gameManager = PLUGIN.getGameManager();
		GameArena gameArena = gameManager.getGameArena();
		boolean isRunning = gameManager.isMinigameRunning();

		if (isRunning) {
			boolean isSpectator = PLUGIN.getProfile(player).isSpectator();
			
			//If the player is a spectator, lets cancel the event.
			if(isSpectator) {
				
				if ((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
					if (event.getItem() != null) {
						
						Material material = event.getItem().getType();
						
						if (material.equals(Material.REDSTONE)) {
							gameArena.getSpectatorMenu(player).openMenu(player);
						} else if (material.equals(Material.COMPASS)) {
							gameArena.getSpectatorTrackerMenu(player).openMenu(player);
						} else if (material.equals(Material.WATCH)) {
							PLUGIN.getBungeecord().connectToBungeeServer(player, "hub-01");
						}
					}
				}
				
				event.setCancelled(true);
			}
		} else {
			
			//Player must be in the lobby.
			if ((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
				if (event.getItem() != null) {
					
					Material material = event.getItem().getType();
					
					//Send player back to the main hub.
					if (material.equals(Material.WATCH)) {
						//Send player to another server instance
						PLUGIN.getBungeecord().connectToBungeeServer(player, "hub-01");
					}
				}
			}
		}
	}
}