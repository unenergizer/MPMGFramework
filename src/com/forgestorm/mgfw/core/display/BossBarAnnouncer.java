package com.forgestorm.mgfw.core.display;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarAnnouncer {
	
	private BossBar bar;
	
	/**
	 * Sends a player entity a boss bar message.
	 * @param player The player who will receive a boss bar message.
	 */
	public void showBossBar(Player player) {
		bar = Bukkit.createBossBar("THIS IS A TEST MESSAGE", BarColor.RED, BarStyle.SOLID, new BarFlag[] { BarFlag.DARKEN_SKY });
		bar.addPlayer(player);
		bar.show();
	}
	
	/**
	 * Removes a boss bar from a player.
	 * @param player Will remove a boss bar from this player.
	 */
	public void removeBossBar(Player player) {
		bar.removePlayer(player);
	}
	
	/**
	 * This will remove the boss bar from all players.
	 */
	public void removeAllBossBars() {
		bar.removeAll();
	}
}
