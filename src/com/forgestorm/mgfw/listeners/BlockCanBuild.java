package com.forgestorm.mgfw.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCanBuildEvent;

import com.forgestorm.mgfw.MGFramework;

public class BlockCanBuild implements Listener {

	private MGFramework PLUGIN;

	public BlockCanBuild(MGFramework plugin){
		PLUGIN = plugin;
	}

	@EventHandler
	public void onBlockCanBuild(BlockCanBuildEvent event){
		boolean isRunning = PLUGIN.getGameManager().isMinigameRunning();
		//Let the player place blocks.
		if(isRunning) {
			event.setBuildable(true);
		}
	}
}
