package com.forgestorm.mgfw.core.constants;

import org.bukkit.ChatColor;

public enum Messages {
	
	//Console Messages
	FSMG("[FSMG-Framework]"), //TODO: Implement this
	
	//Debug messages
	BOSS_BAR_LOBBY_MESSAGE("&bThanks for playing on &amc&7.&aForgeStorm&7.&acom&b!"),
	BOSS_BAR_SPECTATOR_MESSAGE("&bYou are a spectator! &amc&7.&aForgeStorm&7.&acom&b!"),
	
	//Commands
	COMMAND_ADMIN_NOT_OP("&cYou do not have permission to do this."),
	COMMAND_ADMIN_UNKNOWN("&cCommand unknown! Use &a/admin help &cfor more information!"),
	COMMAND_ADMIN_GAMES_PLAYED("&e&lTotal games played: &c%s"),
	COMMAND_ADMIN_END_GAME("&c&lThe game has just been shut down. Returning to game lobby."),
	COMMAND_ADMIN_END_ERROR("&c&lYou can not end a game if the game is not running."),
	COMMAND_ADMIN_FORCE_START("&c&lYou have just force started a game."),
	
	//Team Messages
	TEAM_QUEUE_PLACED("&eYou were placed in a queue to join this team."),
	TEAM_QUEUE_ALREADY_PLACED("&cYou are already queued for this team!"),
	TEAM_ALREADY_ON_TEAM("&7(you are on this team)"),
	TEAM_DROPPED_FROM_QUEUE("&cYou have been removed from the %s &cqueue."),
	
	//Kit Messages
	KIT_ALREADY_HAVE_KIT("&7(you are using this kit)"),
	
	//Game display messages
	GAME_COUNTDOWN_NOT_ENOUGH_PLAYERS("&cCountdown canceled! Not enough players!"),
	GAME_COUNTDOWN_ALL_TEAMS_NEED_PLAYERS("&cCountdown canceled! All teams need players!"),
	GAME_BAR_KIT("&8&l&m----------------&r&l &l &l &6&lKit Select&l &l &l &8&l&m----------------"),
	GAME_BAR_TEAM("&8&l&m---------------&r&l &l &l &3&lTeam Select&l &l &l &8&l&m----------------"),
	GAME_BAR_RULES("&8&l&m---------------&r&l &l &l &a&lHow to Play&l &l &l &8&l&m----------------"),
	GAME_BAR_SCORES("&8&l&m---------------&r&l &l &l &e&lFinal Scores&l &l &l &8&l&m---------------"),
	GAME_BAR_BOTTOM("&8&l&m---------------------------------------------"),
	GAME_TIME_REMAINING_PLURAL("&eGame will start in &c%s &eseconds."),
	GAME_TIME_REMAINING_SINGULAR("&eGame will start in &c1 &esecond."),
	GAME_ARENA_SPECTATOR_TITLE("&aHello, Spectator!"),
	GAME_ARENA_SPECTATOR_SUBTITLE("&7Relax, another game will start soon!"),
	
	//Join and Quit Messages
	PLAYER_JOIN_LOBBY("&a+ &8[&7%s&8/&7%f&8] %e"),
	PLAYER_QUIT_LOBBY("&c- &8[&7%s&8/&7%f&8] %e"),
	PLAYER_QUIT_GAME("&8[&cQuit&8] %s"),
	SPECTATOR_JOIN("&8[&7Spectator Join&8] %s"),
	SPECTATOR_QUIT("&8[&7Spectator Quit&8] %s"),
	
	//Scoreboard
	SB_LOBBY_TITLE("&cmc&7.&cForgeStorm&7.&ccom"),
	SB_BLANK_LINE_1("&r"),
	SB_BLANK_LINE_2("&r&r"),
	SB_BLANK_LINE_3("&r&r&r"),
	SB_BLANK_LINE_4("&r&r&r&r"),
	SB_BLANK_LINE_5("&r&r&r&r&r"),
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
	
	//Menu Items
	MENU_NAME_TRACKER("Player Tracker"),
	MENU_NAME_SPECTATOR("Spectator Menu"),
	MENU_ITEM_SPECTATOR_NO_SPEED("No Speed"),
	MENU_ITEM_SPECTATOR_SPEED_1("Speed 1"),
	MENU_ITEM_SPECTATOR_SPEED_2("Speed 2"),
	MENU_ITEM_SPECTATOR_SPEED_3("Speed 3"),
	MENU_ITEM_SPECTATOR_SPEED_4("Speed 4"),
	MENU_ITEM_SPECTATOR_TRACK_PLAYERS("Track Players");
	
	private String message;
	
	//Constructor
	Messages(String message) {
		this.message = color(message);
	}
	
	/**
	 * Sends a string representation of the enumerator item.
	 */
	public String toString() {
		return message;
	}
	
	/**
	 * Converts special characters in text into Minecraft client color codes.
	 * <p>
	 * This will give the messages color.
	 * @param msg The message that needs to have its color codes converted.
	 * @return Returns a colored message!
	 */
	public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
