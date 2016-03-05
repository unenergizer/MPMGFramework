package com.minepile.mpmgfw.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.minepile.mpmgfw.MPMGFramework;
import com.minepile.mpmgfw.core.GameLobby;

public class EntityDamage implements Listener {

	private final MPMGFramework PLUGIN;
	
	public EntityDamage(MPMGFramework plugin) {
		PLUGIN = plugin;
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		
		GameLobby lobby = PLUGIN.getLobbyManager();
		String lobbyWorld = lobby.getLobbyWorldName();
		
		//Check to see if the entity was a player entity.
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			
			//If the damage happened in the lobby world, prevent it.
			if (player.getWorld().equals(Bukkit.getWorld(lobbyWorld))) {
				
				//Cancel all damage to the player in the lobby.
				event.setCancelled(true);
				
				//If the player falls into the void, tp them to the lobby spawn.
				if (event.getCause().equals(DamageCause.VOID)) {
					lobby.tpToLobbySpawn(player);
				}
			}
		}
	}
}
