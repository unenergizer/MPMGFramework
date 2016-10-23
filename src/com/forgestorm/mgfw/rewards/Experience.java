package com.forgestorm.mgfw.rewards;

import org.bukkit.entity.Player;

import com.forgestorm.servercore.api.ExperienceAPI;

public class Experience extends Reward {

	public Experience(Player player, int multiplier, int amount) {
		super(player, multiplier, amount);
	}

	@Override
	public int giveReward() {
		int total = multiplier * amount;
		new ExperienceAPI().addExp(player, total);
		return total;
	}
}
