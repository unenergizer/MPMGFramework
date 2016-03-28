package com.forgestorm.mgfw.listeners;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.kits.KitSelector;
import com.forgestorm.mgfw.core.teams.TeamSelector;

public class PlayerInteractEntity implements Listener {

	private final MGFramework PLUGIN;

	public PlayerInteractEntity(MGFramework plugin) {
		PLUGIN = plugin;
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		boolean isRunning = PLUGIN.getGameManager().isMinigameRunning();

		if (!isRunning) {
			KitSelector kitSelector = PLUGIN.getGameManager().getKitSelector();
			TeamSelector teamSelector = PLUGIN.getGameManager().getTeamSelector();
			Player player = event.getPlayer();
			UUID uuid = event.getRightClicked().getUniqueId();
			ArrayList<UUID> kitUUID = kitSelector.getKitEntityUUID();
			ArrayList<UUID> teamUUID = teamSelector.getTeamEntityUUID();

			for(int i = 0; i < kitUUID.size(); i++) {
				if (uuid.equals(kitUUID.get(i))) {
					//Toggle interact
					kitSelector.kitInteract(player, i);
					event.setCancelled(true);
				}
			}

			for(int i = 0; i < teamUUID.size(); i++) {
				if (uuid.equals(teamUUID.get(i))) {
					//Toggle interact
					teamSelector.teamInteract(i, player);
					event.setCancelled(true);
				}
			}
		}
	}
}
