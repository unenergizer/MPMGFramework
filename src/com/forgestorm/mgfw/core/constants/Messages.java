package com.forgestorm.mgfw.core.constants;

import net.md_5.bungee.api.ChatColor;

public enum Messages {
	
	//Admin only messages
	ADMIN_NEXT_MAP_ARENA("Next arena map: "),
	ADMIN_NEXT_MAP_LOBBY("Next lobby map: "),
	
	//Debug messages
	
	//Scoreboard
	SB_LOBBY_TITLE("&cmc&7.&cForgeStorm&7.&ccom"),
	
	SB_BLANK_LINE_1("&r"),
	SB_BLANK_LINE_2("&r&r"),
	SB_BLANK_LINE_3("&r&r&r"),
	SB_BLANK_LINE_4("&r&r&r&r"),
	SB_BLANK_LINE_5("&r&r&r&r&r"),
	SB_BLANK_LINE_6("&r&r&r&r&r&r"),
	SB_GAME_STATUS("&lGame Status:"),
	SB_GAME_STATUS_WAITING_1("&eNeed players"),
	SB_GAME_STATUS_WAITING_2("&eNeed players."),
	SB_GAME_STATUS_WAITING_3("&eNeed players.."),
	SB_GAME_STATUS_READY("&aGame ready!"),
	SB_PLAYERS("&lPlayers:"),
	SB_PLAYER_COUNT("&a%s / %f"),
	SB_KIT("&lKit:"),
	SB_TEAM("&lTeam:"),
	SB_NEXT_GAME("&lNext Game:"),
	
	//Game display messages
	GAME_TAB_HEADRER("&bThanks for playing on &amc&7.&aForgeStorm&7.&acom"),
	GAME_TAB_FOOTER("&bNews&7, &aForum&7, &eTeamSpeak&7, &dShop &6@ &cwww&7.&cForgeStorm&7.&ccom"),
	
	GAME_BAR_KIT("&8&l&m-----------------&r&8&l<[ &6&lKit Select &8&l]>&8&l&m---------------"),
	GAME_BAR_TEAM("&8&l&m----------------&r&8&l<[ &3&lTeam Select &8&l]>&8&l&m---------------"),
	GAME_BAR_BOTTOM("&8&l&m---------------------------------------------"),
	
	GAME_TIME_REMAINING_PLURAL("&eGame will start in &c%s &eseconds."),
	GAME_TIME_REMAINING_SINGULAR("&eGame will start in &c1 &esecond.");
	
	
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
