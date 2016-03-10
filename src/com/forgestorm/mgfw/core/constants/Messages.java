package com.forgestorm.mgfw.core.constants;

import net.md_5.bungee.api.ChatColor;

public enum Messages {
	
	//Admin only messages
	ADMIN_NEXT_MAP("The next map to be played is: "),
	
	//Debug messages
	
	
	//Game display messages
	GAME_BAR_TOP("&8&l&m-----------------&r&8&l<[ &e&lKit Select &8&l]>&8&l&m----------------"),
	GAME_BAR_BOTTOM("&8&l&m----------------------------------------------"),
	GAME_TIME_REMAINING("&eGame will start in &c%s &eseconds.");

	private String message;

	Messages(String message) {
		this.message = color(message);
	}

	public String toString() {
		return message;
	}
	
	public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
