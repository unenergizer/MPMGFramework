package com.minepile.mpmgfw.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.minepile.mpmgfw.MPMGFramework;
import com.minepile.mpmgfw.profiles.PlayerProfile;

public class EntityDamageByEntity implements Listener {

	private final MPMGFramework PLUGIN;

	public EntityDamageByEntity(MPMGFramework plugin) {
		PLUGIN = plugin;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {

		HashMap<Player, PlayerProfile> playerProfile = PLUGIN.getGameManager().getPlayerProfile();
		
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
	}
}
