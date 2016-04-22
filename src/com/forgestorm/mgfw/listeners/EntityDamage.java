package com.forgestorm.mgfw.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.GameArena;
import com.forgestorm.mgfw.core.GameLobby;
import com.forgestorm.mgfw.core.GameManager;
import com.forgestorm.mgfw.core.constants.GameState;
import com.forgestorm.mgfw.profiles.PlayerProfile;

public class EntityDamage implements Listener {

	private final MGFramework PLUGIN;

	public EntityDamage(MGFramework plugin) {
		PLUGIN = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageEvent event) {
		boolean isRunning = PLUGIN.getGameManager().isMinigameRunning();

		if (!isRunning) {
			GameLobby lobby = PLUGIN.getGameManager().getGameLobby();
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
				GameManager gameManager = PLUGIN.getGameManager();
				GameState gameState = gameManager.getGameState();
				Player player = (Player) event.getEntity();
				PlayerProfile profile = PLUGIN.getProfile(player);
				
				double playerHP = player.getHealth() - event.getFinalDamage();
				
				boolean isOverrideSpectator = PLUGIN.getMinigamePluginManager().getMinigameBase().isOverrideSpectator();
				boolean isSpectator = profile.isSpectator();
				boolean isGameStarting = gameState.equals(GameState.GAME_STARTING);
				boolean isShowingRules = gameState.equals(GameState.ARENA_SHOW_RULES);
				boolean isGameRunning = gameState.equals(GameState.GAME_RUNNING);
				
				if (isSpectator) {
					event.setCancelled(true);
				} else {
					
					//Cancel any damage to the player if the rules are still being shown.
					if (isGameStarting || isShowingRules) {
						event.setCancelled(true);
						
					} else if (playerHP <= 1 && isGameRunning && !isOverrideSpectator) {
						//The player is not a spectator.
						//However, if they loose enough HP, lets make them a spectator.
						
						event.setCancelled(true);
						GameArena gameArena = gameManager.getGameArena();

						//Get the players profile and set them to spectator.
						profile.setSpectator(true);
						
						//Setup the player as a spectator.
						gameArena.setupSpectator(player);
					}
				}
			}
		}
	}
}
