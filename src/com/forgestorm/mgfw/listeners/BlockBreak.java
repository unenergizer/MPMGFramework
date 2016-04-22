package com.forgestorm.mgfw.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.forgestorm.mgfw.MGFramework;

public class BlockBreak implements Listener {

	private MGFramework PLUGIN;

	public BlockBreak(MGFramework plugin){
		PLUGIN = plugin;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
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
