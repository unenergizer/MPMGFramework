package com.forgestorm.mgfw.core.player;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class LobbyPlayer extends FrameworkPlayer {

	public LobbyPlayer(Player player, GameMode gameMode, boolean isCollidable, boolean canFly) {
		super(player, gameMode, isCollidable, canFly);
	}

	@Override
	public void setupPlayer() {

	}
}
