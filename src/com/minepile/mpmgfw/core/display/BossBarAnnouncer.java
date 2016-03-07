package com.minepile.mpmgfw.core.display;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarAnnouncer {
	
	private BossBar bar;
	
	public void showBossBar(Player player) {
		bar = Bukkit.createBossBar("THIS IS A TEST MESSAGE", BarColor.RED, BarStyle.SEGMENTED_6, new BarFlag[] { BarFlag.DARKEN_SKY });
		bar.addPlayer(player);
		bar.show();
	}
	
	public void removeBossBar(Player player) {
		bar.removePlayer(player);
	}
	
	public void removeAllBossBars() {
		bar.removeAll();
	}
}
