package com.minepile.mpmgfw.profiles;

import java.util.UUID;

import org.bukkit.entity.Player;

public class PlayerProfile {
	
	private UUID uuid;
	private String name;
	
	private boolean isSpectator;
	
	public PlayerProfile(Player player) {
		uuid = player.getUniqueId();
		name = player.getDisplayName();
		isSpectator = false;
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public boolean isSpectator() {
		return isSpectator;
	}

	public void setSpectator(boolean isSpectator) {
		this.isSpectator = isSpectator;
	}
}
