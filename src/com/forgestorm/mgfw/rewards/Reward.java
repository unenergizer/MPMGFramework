package com.forgestorm.mgfw.rewards;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class Reward {

	protected final Player player;
	protected final int multiplier;
	protected final int amount;
	
	public abstract int giveReward();
}
