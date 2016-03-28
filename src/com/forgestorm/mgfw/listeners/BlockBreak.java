package com.forgestorm.mgfw.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.profiles.PlayerProfile;

public class BlockBreak implements Listener {

	private MGFramework PLUGIN;

	public BlockBreak(MGFramework plugin){
		PLUGIN = plugin;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		boolean isRunning = PLUGIN.getGameManager().isMinigameRunning();

		if (isRunning) {
			HashMap<Player, PlayerProfile> playerProfile = PLUGIN.getGameLobby().getPlayerProfile();
			Player player = (Player) event.getPlayer();

			//If the player is a spectator, lets cancel the event.
			if(playerProfile.get(player).isSpectator()){
				event.setCancelled(true);
			}
		}

	}
}
