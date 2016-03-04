package com.minepile.mpmgfw.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.minepile.mpmgfw.MPMGFramework;

public class WeatherChange implements Listener {
	
	private final MPMGFramework PLUGIN;
	
	public WeatherChange(MPMGFramework plugin) {
		PLUGIN = plugin;
	}
	
	@EventHandler
	public void onChange(WeatherChangeEvent event) {
		
		String lobbyWorld = PLUGIN.getGameManager().getLobbyWorld();
		
		//Lets cancel weather change events in the minigame lobby.
		if (event.getWorld().equals(Bukkit.getWorld(lobbyWorld))) {
			event.setCancelled(true);
		}
	}
}
