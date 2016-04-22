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

public class PlayerInteract implements Listener {

	private MGFramework PLUGIN;

	public PlayerInteract(MGFramework plugin){
		PLUGIN = plugin;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		GameManager gameManager = PLUGIN.getGameManager();
		GameArena gameArena = gameManager.getGameArena();
		boolean isRunning = gameManager.isMinigameRunning();

		if (isRunning) {
			Player player = (Player) event.getPlayer();
			boolean isSpectator = PLUGIN.getProfile(player).isSpectator();
			
			//If the player is a spectator, lets cancel the event.
			if(isSpectator){
				
				if ((event.getAction().equals(Action.RIGHT_CLICK_AIR)) || (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
					if (event.getItem() != null) {
						if (event.getItem().getType().equals(Material.REDSTONE)) {
							gameArena.getSpectatorMenu(player).openMenu(player);
						} else if (event.getItem().getType().equals(Material.COMPASS)) {
							gameArena.getSpectatorTrackerMenu(player).openMenu(player);
						}
					}
				}
				
				event.setCancelled(true);
			}
		}
	}
}