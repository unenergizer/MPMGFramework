package com.forgestorm.mgfw.profiles;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.forgestorm.servercore.api.ProfileAPI;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerProfile {
	
	private UUID uuid;
	private String name;
	private String prefix;
	private boolean isSpectator;
	
	public PlayerProfile(Player player) {
		uuid = player.getUniqueId();
		name = player.getDisplayName();
		isSpectator = false;
		prefix = new ProfileAPI(player).getProfile().getPrefix();
	}
}
