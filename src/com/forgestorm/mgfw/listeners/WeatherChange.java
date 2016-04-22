package com.forgestorm.mgfw.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.forgestorm.mgfw.MGFramework;

public class WeatherChange implements Listener {

	private final MGFramework PLUGIN;

	public WeatherChange(MGFramework plugin) {
		PLUGIN = plugin;
	}

	@EventHandler
	public void onChange(WeatherChangeEvent event) {

		String lobbyWorld = PLUGIN.getGameManager().getGameLobby().getLobbyWorldName();

		//Lets cancel weather change events in the minigame lobby.
		if (event.getWorld().equals(Bukkit.getWorld(lobbyWorld))) {
			event.setCancelled(true);
		}
	}
}
