package com.forgestorm.mgfw.core;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.constants.Messages;
import com.forgestorm.mgfw.core.display.scoreboard.LobbyScoreboard;
import com.forgestorm.mgfw.core.player.LobbyPlayer;
import com.forgestorm.mgfw.profiles.PlayerProfile;
import com.forgestorm.mgfw.selector.KitSelector;
import com.forgestorm.mgfw.selector.TeamSelector;
import com.forgestorm.mgfw.util.ItemBuilder;
import com.forgestorm.servercore.core.display.BossBarAnnouncer;

import net.md_5.bungee.api.ChatColor;

public class GameLobby {

	private final MGFramework PLUGIN;
	private final String LOBBY_WORLD_NAME;
	private final Location LOBBY_SPAWN;

	private BossBarAnnouncer bar;
	private LobbyScoreboard scoreboard;

	public GameLobby(MGFramework plugin) {
		PLUGIN = plugin;
		LOBBY_WORLD_NAME = "mg-lobby";
		LOBBY_SPAWN = new Location(Bukkit.getWorld(LOBBY_WORLD_NAME), 0.5, 76, 0.5);

		bar = new BossBarAnnouncer(Messages.BOSS_BAR_LOBBY_MESSAGE.toString());
		scoreboard = new LobbyScoreboard(PLUGIN);
	}

	/**
	 * This will setup all the players in the server for the lobby.
	 */
	void setupAllLobbyPlayers() {
		//Setup all the lobby players.
		for (Player players: Bukkit.getOnlinePlayers()) {
			setupLobbyPlayer(players);
		}
	}

	/**
	 * This will setup a player in the lobby.
	 * @param player The player to setup in the lobby.
	 */
	public void setupLobbyPlayer(Player player) {
		PlayerProfile playerProfile = PLUGIN.getProfile(player);
		TeamSelector teamSelector = PLUGIN.getGameManager().getTeamSelector();

		//Get all players and teleport them to the lobby world.	
		tpToLobbySpawn(player);

		//Assigning the player to a team.
		if (teamSelector.getPlayerTeam(player) == null) {
			teamSelector.addPlayer(player);
		}
		
		new LobbyPlayer(player, GameMode.ADVENTURE, false, false);
		
		//Set the player as a spectator.
		playerProfile.setSpectator(false);

		//Setup lobby scoreboard
		scoreboard.addPlayer(player);

		//Send the player the boss bar.
		bar.showBossBar(player);
		
		//Give Spectator tracker menu item.
		ItemStack spectatorServerExit = new ItemBuilder(Material.WATCH).setTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "Back To Lobby").build();
		player.getInventory().setItem(8, spectatorServerExit);
	}

	void showHiddenPlayers() {
		//Hide the spectator from other players.
		for (Player spectators: Bukkit.getOnlinePlayers()) {

			//Now loop through all players and hide them from spectators.
			for(Player players: Bukkit.getOnlinePlayers()) {
				
				players.showPlayer(spectators);
			}
		}
	}
	
	/**
	 * Removes all the players from the lobby player setup.
	 */
	void removeAllLobbyPlayers() {
		for(Player players: Bukkit.getOnlinePlayers()) {
			removeLobbyPlayer(players);
		}
	}

	/**
	 * Removes a players lobby components.
	 * @param player The player who will have lobby components removed from them.
	 */
	private void removeLobbyPlayer(Player player) {
		//Remove boss bars from the player.
		bar.removeBossBar(player);

		//Remove the lobby scoreboard.
		scoreboard.removePlayer(player);
	}

	/**
	 * When a player quits the game, lets remove lobby components from them.
	 * @param player The player who has quit the game.
	 */
	public void removeQuitPlayer(Player player) {
		KitSelector kitSelector = PLUGIN.getGameManager().getKitSelector();
		TeamSelector teamSelector = PLUGIN.getGameManager().getTeamSelector();

		//Remove lobby components from player.
		removeLobbyPlayer(player);

		//Remove player from kit selecton.
		kitSelector.removePlayerKit(player);

		//Remove player form teaming hashmaps.
		teamSelector.removeQueuedPlayer(player);
		teamSelector.removeSortedPlayer(player);
		
		//Update player queued positions.
		teamSelector.updateQueuePositions();
	}

	/**
	 * Initialize (setup) the minigame lobby.
	 */
	void loadLobbyWorld() {

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
	void tpAllToLobbySpawn() {
		for(Player players: Bukkit.getOnlinePlayers()) {
			tpToLobbySpawn(players);
		}
	}

	/**
	 * Teleports a player to the lobby spawn pad.
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

	/**
	 * Gets the lobby scoreboard component.
	 * @return Returns an instance of the lobby scoreboard component.
	 */
	public LobbyScoreboard getScoreboard() {
		return scoreboard;
	}
}
