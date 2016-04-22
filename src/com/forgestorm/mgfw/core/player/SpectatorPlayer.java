package com.forgestorm.mgfw.core.player;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.forgestorm.mgfw.core.constants.Messages;
import com.forgestorm.servercore.core.display.FloatingMessage;

public class SpectatorPlayer extends FrameworkPlayer {

	public SpectatorPlayer(Player player, GameMode gameMode, boolean isCollidable, boolean canFly) {
		super(player, gameMode, isCollidable, canFly);
	}

	@Override
	public void setupPlayer() {
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
		sendSpectatorNotification();
	}
	
	private void sendSpectatorNotification() {
		String title = Messages.GAME_ARENA_SPECTATOR_TITLE.toString();
		String subtitle = Messages.GAME_ARENA_SPECTATOR_SUBTITLE.toString();
		new FloatingMessage().sendFloatingMessage(player, title, subtitle);
	}
}
