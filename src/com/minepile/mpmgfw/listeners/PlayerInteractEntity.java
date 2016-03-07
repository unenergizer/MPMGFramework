package com.minepile.mpmgfw.listeners;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.minepile.mpmgfw.MPMGFramework;
import com.minepile.mpmgfw.core.kits.KitSelector;
import com.minepile.mpmgfw.core.teams.TeamSelector;

public class PlayerInteractEntity implements Listener {
	
	private final MPMGFramework PLUGIN;
	
	public PlayerInteractEntity(MPMGFramework plugin) {
		PLUGIN = plugin;
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

		KitSelector kitSelector = PLUGIN.getGameManager().getKitSelector();
		TeamSelector teamSelector = PLUGIN.getGameManager().getTeamSelector();
		Player player = event.getPlayer();
		UUID uuid = event.getRightClicked().getUniqueId();
		ArrayList<UUID> kitUUID = kitSelector.getKitEntityUUID();
		ArrayList<UUID> teamUUID = kitSelector.getKitEntityUUID();
		
		for(int i = 0; i < kitUUID.size(); i++) {
			if (uuid.equals(kitUUID.get(i))) {
				//Toggle interact
				kitSelector.kitInteract(player, i);
			}
		}
		
		for(int i = 0; i < teamUUID.size(); i++) {
			if (uuid.equals(teamUUID.get(i))) {
				//Toggle interact
				teamSelector.teamInteract(player, i);
			}
		}
	}
}
