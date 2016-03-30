package com.forgestorm.mgfw.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.GameArena;
import com.forgestorm.mgfw.core.GameLobby;
import com.forgestorm.mgfw.profiles.PlayerProfile;

public class EntityDamage implements Listener {

	private final MGFramework PLUGIN;

	public EntityDamage(MGFramework plugin) {
		PLUGIN = plugin;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		boolean isRunning = PLUGIN.getGameManager().isMinigameRunning();

		if (!isRunning) {
			GameLobby lobby = PLUGIN.getGameLobby();
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
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 1F, .5F);
					}
				}
			}
		} else {
			//Check for spectator damage.
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				double playerHP = player.getHealth() - event.getFinalDamage();
				boolean isSpectator = PLUGIN.getGameLobby().getPlayerProfile().get(player).isSpectator();
				
				if (isSpectator) {
					event.setCancelled(true);
				} else {
					
					//The player is not a spectator.
					//However, if they loose enough HP, lets make them a spectator.
					if (playerHP <= 1) {
						event.setCancelled(true);
						GameArena gameArena = PLUGIN.getGameArena();
						GameLobby gameLobby = PLUGIN.getGameLobby();
						HashMap<Player, PlayerProfile> playerProfile = gameLobby.getPlayerProfile();
						
						//Get the players profile and set them to spectator.
						playerProfile.get(player).setSpectator(true);
						
						//Setup the player as a spectator.
						gameArena.setupSpectator(player);
						
						//Teleport the player to a spectator spawn in the game world.
						//TODO: Get a real spectator spawn point from the minigame plugin.
						gameArena.tpToGameWorld(player, 0, 100, 0);
					}
				}
			}
		}
	}
}
