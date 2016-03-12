package com.forgestorm.mgfw.core;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.constants.Messages;
import com.forgestorm.mgfw.core.display.BossBarAnnouncer;
import com.forgestorm.mgfw.core.display.LobbyScoreboard;
import com.forgestorm.mgfw.core.display.TabMenuText;
import com.forgestorm.mgfw.profiles.PlayerProfile;

public class GameLobby {
	
	private final MGFramework PLUGIN;
	private final String LOBBY_WORLD_NAME;
	private final Location LOBBY_SPAWN;

	private HashMap<Player, PlayerProfile> playerProfile;
	private HashMap<Player, BossBarAnnouncer> bar;
	private LobbyScoreboard scoreboard;

	public GameLobby(MGFramework plugin) {
		PLUGIN = plugin;
		LOBBY_WORLD_NAME = "mg-lobby";
		LOBBY_SPAWN = new Location(Bukkit.getWorld(LOBBY_WORLD_NAME), 0.5, 76, 0.5);
		
		playerProfile = new HashMap<Player, PlayerProfile>();
		bar = new HashMap<Player, BossBarAnnouncer>();
		scoreboard = new LobbyScoreboard(PLUGIN);
	}
	
	/**
	 * This will setup all the players in the server for the lobby.
	 */
	public void setupAllLobbyPlayers() {
		//Setup all the lobby players.
		for (Player players: Bukkit.getOnlinePlayers()) {
			setupLobbyPlayer(players);
		}
	}
	
	/**
	 * This will setup a player in the lobby.
	 * 
	 * @param player The player to setup in the lobby.
	 */
	public void setupLobbyPlayer(Player player) {
		//Get all players and teleport them to the lobby world.	
		tpToLobbySpawn(player);
		
		//Heal the player
		player.setHealth(20);
		player.setFoodLevel(20);
		
		//Clear a players inventory
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		
		//Set gamemode.
		player.setGameMode(GameMode.ADVENTURE);
		
		//Give the player flying.
		player.setAllowFlight(false);
		player.setFlying(false);
		
		//Set collide entities false.
		player.spigot().setCollidesWithEntities(true);

		//Setup lobby player profiles (for server reloads).
		if (!playerProfile.containsKey(player)) {
			playerProfile.put(player, new PlayerProfile(player));
		}

		//Set the player as a spectator.
		playerProfile.get(player).setSpectator(false);
		
		//Setup lobby scoreboard
		scoreboard.addPlayer(player);

		//Give the player a boss bar if they dont have one.
		if(!bar.containsKey(player)) {
			bar.put(player, new BossBarAnnouncer());
		}
		//Send the player the boss bar.
		bar.get(player).showBossBar(player);
		
		//Send Tab Menu text
		TabMenuText tmt = new TabMenuText();
		String header = Messages.GAME_TAB_HEADRER.toString();
		String footer = Messages.GAME_TAB_FOOTER.toString();
		tmt.sendHeaderAndFooter(player, header, footer);
	}
	
	public void removeAllLobbyPlayers() {
		for(Player players: Bukkit.getOnlinePlayers()) {
			removeLobbyPlayer(players);
		}
	}
	
	public void removeLobbyPlayer(Player player) {
		//Remove boss bars from the player.
		bar.get(player).removeBossBar(player);
		
		//Remove the lobby scoreboard.
		scoreboard.removePlayer(player);
	}

	/**
	 * Initialize (setup) the minigame lobby.
	 */
	public void loadLobbyWorld() {

		//Edit world properties.
		Bukkit.setSpawnRadius(0);
		World world = Bukkit.getWorlds().get(0);
		world.setSpawnFlags(false, false);
		world.setGameRuleValue("doMobSpawning", "false");

		//Remove non-player entities from the world.
		for (Entity entity : world.getEntities()) {
			if (!(entity instanceof Player) && entity instanceof LivingEntity) {
				entity.remove();
			}
		}
	}
	
	/**
	 * This will teleport all the players in the server to the lobby spawn.
	 */
	public void tpAllToLobbySpawn() {
		for(Player players: Bukkit.getOnlinePlayers()) {
			tpToLobbySpawn(players);
		}
	}
	
	/**
	 * Teleports a player to the lobby spawn pad.
	 * 
	 * @param player The player to teleport to lobby spawn.
	 */
	public void tpToLobbySpawn(Player player) {
		player.teleport(LOBBY_SPAWN);
	}

	/**
	 * Gets the main lobby's spawn point!
	 * @return A location with the lobby's spawn point.
	 */
	public Location getLobbySpawn() {
		return LOBBY_SPAWN;
	}

	/**
	 * Gets the lobby's world name.
	 * @return The name of the lobby world.
	 */
	public String getLobbyWorldName() {
		return LOBBY_WORLD_NAME;
	}

	public HashMap<Player, PlayerProfile> getPlayerProfile() {
		return playerProfile;
	}

	public LobbyScoreboard getScoreboard() {
		return scoreboard;
	}
}
