package com.forgestorm.mgfw.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.forgestorm.mgfw.MGFramework;

public class BlockPlace implements Listener {

	private MGFramework PLUGIN;

	public BlockPlace(MGFramework plugin){
		PLUGIN = plugin;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		boolean isRunning = PLUGIN.getGameManager().isMinigameRunning();

		if (isRunning) {
			Player player = (Player) event.getPlayer();
			boolean isSpectator = PLUGIN.getProfile(player).isSpectator();

			//If the player is a spectator, lets cancel the event.
			if(isSpectator){
				event.setCancelled(true);
			}
		}
	}
}
