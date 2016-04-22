package com.forgestorm.mgfw.core.player;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class ArenaPlayer extends FrameworkPlayer {

	public ArenaPlayer(Player player, GameMode gameMode, boolean isCollidable, boolean canFly) {
		super(player, gameMode, isCollidable, canFly);
	}

	@Override
	protected void setupPlayer() {
		// TODO Auto-generated method stub
		
	}

}
