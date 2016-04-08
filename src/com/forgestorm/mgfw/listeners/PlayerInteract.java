package com.forgestorm.mgfw.listeners;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.GameArena;
import com.forgestorm.mgfw.core.GameManager;
import com.forgestorm.mgfw.profiles.PlayerProfile;

public class PlayerInteract implements Listener {

	private MGFramework PLUGIN;

	public PlayerInteract(MGFramework plugin){
		PLUGIN = plugin;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		GameManager gameManager = PLUGIN.getGameManager();
		GameArena gameArena = gameManager.getGAME_ARENA();
		boolean isRunning = gameManager.isMinigameRunning();

		if (isRunning) {
			HashMap<Player, PlayerProfile> playerProfile = gameManager.getGAME_LOBBY().getPlayerProfile();
			Player player = (Player) event.getPlayer();
			
			//If the player is a spectator, lets cancel the event.
			if(playerProfile.get(player).isSpectator()){
				
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