package com.forgestorm.mgfw.core.player;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class FrameworkPlayer {

	protected Player player;
	private GameMode gameMode;
	private boolean isCollidable;
	
	public FrameworkPlayer(Player player, GameMode gameMode, boolean isCollidable, boolean canFly) {
		this.player = player;
		this.gameMode = gameMode;
		this.isCollidable = isCollidable;
		
		setGameMode();
		setCollidable();
		healPlayer();
		clearInventory();
		removePotionEffects();
		removeFireTicks();
		isFlying(canFly);
		showHiddenPlayers();

		setupPlayer();
	}
	
	protected abstract void setupPlayer();
	
	private void setCollidable(){
		player.setCollidable(isCollidable);
	}
	
	private void setGameMode() {
		player.setGameMode(gameMode);
	}
	
	private void healPlayer() {
		player.setMaxHealth(20);
		player.setHealth(20);
		player.setFoodLevel(20);
	}
	
	private void clearInventory() {
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
	}

	private void removePotionEffects() {
		for(PotionEffect p : player.getActivePotionEffects()) {  
			PotionEffectType pt = p.getType();

			player.removePotionEffect(pt);
		}
	}
	
	private void removeFireTicks() {
		player.setFireTicks(0);
	}
	
	private void isFlying(boolean flying) {
		if (flying) {
			player.setAllowFlight(true);
			player.setFlying(true);
		} else {
			player.setFlySpeed(.1f);
			player.setAllowFlight(false);
			player.setFlying(false);
		}
	}
	
	private void showHiddenPlayers() {
		for (Player hiddenPlayer: Bukkit.getOnlinePlayers()) {
			for(Player players: Bukkit.getOnlinePlayers()) {
				players.showPlayer(hiddenPlayer);
			}
		}
	}
}
