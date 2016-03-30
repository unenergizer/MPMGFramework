package com.forgestorm.mgfw.core.display;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.forgestorm.mgfw.core.constants.Messages;

public class BossBarAnnouncer {
	
	private BossBar bar;
	private String message;
	
	public BossBarAnnouncer(String message) {
		this.message = message;
		setupBossBar();
	}
	
	/**
	 * This will setup a new bossbar.
	 */
	private void setupBossBar() {
		bar = Bukkit.createBossBar(message, BarColor.PURPLE, BarStyle.SOLID, new BarFlag[] { BarFlag.DARKEN_SKY });
		bar.setVisible(true);
	}
	
	/**
	 * Sends a player entity a boss bar message.
	 * @param player The player who will receive a boss bar message.
	 */
	public void showBossBar(Player player) {
		bar.addPlayer(player);
	}
	
	/**
	 * Removes a boss bar from a player.
	 * @param player Will remove a boss bar from this player.
	 */
	public void removeBossBar(Player player) {
		bar.removePlayer(player);
	}
}
