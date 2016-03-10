package com.forgestorm.mgfw.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.kits.KitSelector;
import com.forgestorm.mgfw.core.teams.TeamSelector;
import com.forgestorm.mgfw.profiles.PlayerProfile;

public class EntityDamageByEntity implements Listener {

	private final MGFramework PLUGIN;

	public EntityDamageByEntity(MGFramework plugin) {
		PLUGIN = plugin;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {

		HashMap<Player, PlayerProfile> playerProfile = PLUGIN.getGameLobby().getPlayerProfile();

		//Lets check for spectator damage.
		//Check to see if the entity was a player entity.
		if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			Player damager = (Player) event.getDamager();
			Player defender = (Player) event.getEntity();

			//Make sure we have the player in the PlayerProfile HashMap.
			if (playerProfile.containsKey(damager) || playerProfile.containsKey(defender)) {

				//Check if the damager was a spectator.
				if(playerProfile.get(damager).isSpectator() || playerProfile.get(defender).isSpectator()) {

					//Cancel the damage if the player was a spectator.
					event.setCancelled(true);
				}
			}
		}
		
		//Test for kit and team interaction.
		if (event.getDamager() instanceof Player) {

			KitSelector kitSelector = PLUGIN.getGameManager().getKitSelector();
			TeamSelector teamSelector = PLUGIN.getGameManager().getTeamSelector();
			Player player = (Player) event.getDamager();
			UUID uuid = event.getEntity().getUniqueId();
			ArrayList<UUID> kitUUID = kitSelector.getKitEntityUUID();
			ArrayList<UUID> teamUUID = teamSelector.getTeamEntityUUID();
			
			//Test for kit interaction.
			for(int i = 0; i < kitUUID.size(); i++) {
				if (uuid.equals(kitUUID.get(i))) {
					//Cancel the event (do no damage).
					event.setCancelled(true);
					
					//Toggle interact
					kitSelector.kitInteract(player, i);
				}
			}
			
			//Test for team interaction.
			for(int i = 0; i < teamUUID.size(); i++) {
				if (uuid.equals(teamUUID.get(i))) {
					//Cancel the event (do no damage).
					event.setCancelled(true);
					
					//Toggle interact
					teamSelector.teamInteract(player, i);
				}
			}
		}
	}
}
