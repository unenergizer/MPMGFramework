package com.forgestorm.mgfw.rewards;

import org.bukkit.entity.Player;

import com.forgestorm.servercore.api.CurrencyAPI;

public class Currency extends Reward {

	public Currency(Player player, int multiplier, int amount) {
		super(player, multiplier, amount);
	}

	@Override
	public int giveReward() {
		int total = multiplier * amount;
		new CurrencyAPI().addCurrency(player, total);
		return total;
	}
}
