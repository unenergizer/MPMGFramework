package com.minepile.mpmgfw.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.minepile.mpmgfw.MPMGFramework;
import com.minepile.mpmgfw.core.GameManager;

public class EntityDamage implements Listener {

	private final MPMGFramework PLUGIN;
	
	public EntityDamage(MPMGFramework plugin) {
		PLUGIN = plugin;
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		
		GameManager gameManager = PLUGIN.getGameManager();
		String lobbyWorld = gameManager.getLobbyWorld();
		
		//Check to see if the entity was a player entity.
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			
			//If the damage happened in the lobby world, prevent it.
			if (player.getWorld().equals(Bukkit.getWorld(lobbyWorld))) {
				
				//Cancel all damage to the player in the lobby.
				event.setCancelled(true);
				
				//If the player falls into the void, tp them to the lobby spawn.
				if (event.getCause().equals(DamageCause.VOID)) {
					gameManager.tpToLobbySpawn(player);
				}
			}
		}
	}
}
