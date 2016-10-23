package com.forgestorm.mgfw.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.forgestorm.mgfw.MGFramework;

public class Spectator {
	
	private final MGFramework PLUGIN = (MGFramework) Bukkit.getPluginManager().getPlugin("FSMG-Framework");
	
	public boolean isSpectator(Player player) {
		return PLUGIN.getProfile(player).isSpectator();
	}
	
	public void setSpectator(Player player) {
		PLUGIN.getGameManager().getGameArena().setupSpectator(player);
	}
}
