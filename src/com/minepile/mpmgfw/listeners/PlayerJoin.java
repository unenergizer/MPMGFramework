package com.minepile.mpmgfw.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.minepile.mpmgfw.MPMGFramework;
import com.minepile.mpmgfw.core.GameManager;

public class PlayerJoin implements Listener {
	
	private final MPMGFramework PLUGIN;
	
	public PlayerJoin(MPMGFramework plugin) {
		PLUGIN = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		
		Player player = event.getPlayer();
		GameManager gameManager = PLUGIN.getGameManager();
		
		if (gameManager.isMinigameRunning()) {
			//TODO: teleport the new player to the game world as a spectator.
			
		} else {
			if (gameManager.shouldMinigameStart()) {
				gameManager.startCountdown();
				teleport(player, PLUGIN.getGameManager().getLobbySpawn());
				
			} else {
				teleport(player, PLUGIN.getGameManager().getLobbySpawn());
			}
		}
	}
	
	private void teleport(final Player player, final Location location) {
		new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(location);
            }
        }.runTaskLater(PLUGIN, 10);
	}
}
