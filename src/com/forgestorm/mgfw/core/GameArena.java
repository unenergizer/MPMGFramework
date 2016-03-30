package com.forgestorm.mgfw.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.constants.GameState;
import com.forgestorm.mgfw.core.constants.Messages;
import com.forgestorm.mgfw.core.display.FloatingMessage;
import com.forgestorm.mgfw.core.display.scoreboard.ArenaScoreboard;
import com.forgestorm.mgfw.core.teams.spawner.PlayerSpawner;
import com.forgestorm.mgfw.core.worlds.WorldDuplicator;
import com.forgestorm.mgfw.util.ScoresToPlaces;

import net.md_5.bungee.api.ChatColor;

public class GameArena {

	private final MGFramework PLUGIN;
	private final WorldDuplicator WORLD_DUPE;

	private HashMap<Player, Location> playerSpawns;
	private ArenaScoreboard scoreboard;

	public GameArena(MGFramework plugin) {
		PLUGIN = plugin;
		WORLD_DUPE = new WorldDuplicator();
		scoreboard = new ArenaScoreboard(PLUGIN);
	}

	/**
	 * Setups an arena player.
	 * @param player The player who will be setup for the game arena.
	 */
	private void setupArenaPlayer(Player player) {
		//Heal the player
		player.setHealth(20);
		player.setFoodLevel(20);

		//Clear a players inventory
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);

		//Give the player flying.
		player.setAllowFlight(false);
		player.setFlying(false);

		//Setup lobby scoreboard
		scoreboard.addPlayer(player);

		//TODO: Give kit
		//TODO: Show Bossbar Announcer
	}

	/**
	 * Teleport all players in the server to the game world.
	 */
	void setupAllArenaPlayers() {
		//Spawn the players in the world.
		PlayerSpawner spawner = new PlayerSpawner(PLUGIN);
		ConcurrentMap<Integer, ArrayList<Player>> sortedTeam = PLUGIN.getGameManager().getTeamSelector().getSortedTeams();

		//Clear the location of the playerSpawns HashMap
		if (playerSpawns != null && !playerSpawns.isEmpty()) {
			playerSpawns.clear();
		}

		//Spawn players and record their spawn locations.
		playerSpawns = spawner.spawnPlayers(sortedTeam);

		//Do some additional player setup.
		for (Player players: Bukkit.getOnlinePlayers()) {
			setupArenaPlayer(players);
		}

		//TODO: Now freeze the players so they can not move.
	}

	/**
	 * This will setup a spectator for a player.
	 * @param player The player that will be setup as a spectator.
	 */
	public void setupSpectator(Player player) {
		//Heal the player
		player.setHealth(20);
		player.setFoodLevel(20);

		//Clear a players inventory
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);

		//Switch Gamemode
		player.setGameMode(GameMode.ADVENTURE);

		//Give the player flying.
		player.setAllowFlight(true);
		player.setFlying(true);

		//Set collide entities false.
		player.spigot().setCollidesWithEntities(false);

		//Setup lobby scoreboard
		scoreboard.addPlayer(player);

		//TODO: Set the player to the spectator scoreboard team.
		//TODO: Show Bossbar Announcer

		//Send spectator notification message.
		String title = ChatColor.GREEN + "Hello, Spectator!";
		String subtitle = ChatColor.GRAY + "Relax, another game will start soon!";
		new FloatingMessage().sendFloatingMessage(player, title, subtitle);
	}

	/**
	 * This will show the games rules before the players are able to move around in the arena world.
	 */
	void showGameRules() {
		//Set the current game state. Used in PlayerMoveEvent listener (PlayerMove) to stop players from moving.
		PLUGIN.getGameManager().setGameState(GameState.ARENA_SHOW_RULES);
		ArrayList<String> gameRules = PLUGIN.getMinigamePluginManager().getMinigameBase().getGameRules();
		
		//Show the game rules.
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(Messages.GAME_BAR_BOTTOM.toString());
		Bukkit.broadcastMessage("");
		
		//Loop through and show the game rules.
		for (int i = 0; i < gameRules.size(); i++) {
			Bukkit.broadcastMessage(ChatColor.GREEN + gameRules.get(i));
		}
		
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(Messages.GAME_BAR_BOTTOM.toString());

		new BukkitRunnable() {
			int countdown = 10;

			@Override
			public void run() {

				if (countdown <= 5 && countdown > 1) {
					String title = ChatColor.YELLOW + Integer.toString(countdown);

					//Show players a countdown message.
					for (Player players: Bukkit.getOnlinePlayers()) {
						new FloatingMessage().sendFloatingMessage(players, title, "");
						players.playSound(players.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
					}
				} else if (countdown == 1) {
					String title = ChatColor.RED + Integer.toString(countdown);

					//Show players a countdown message.
					for (Player players: Bukkit.getOnlinePlayers()) {
						new FloatingMessage().sendFloatingMessage(players, title, "");
						players.playSound(players.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
					}
				} else if (countdown == 0) {
					cancel();
					String subTitle = ChatColor.GREEN + "Go!";

					//Show players a countdown message.
					for (Player players: Bukkit.getOnlinePlayers()) {
						new FloatingMessage().sendFloatingMessage(players, "", subTitle);
						players.playSound(players.getLocation(), Sound.BLOCK_NOTE_HARP, 1f, 1f);
					}

					//Change the game state.
					PLUGIN.getGameManager().setGameState(GameState.GAME_RUNNING);
				}

				countdown--;
			}
		}.runTaskTimer(PLUGIN, 0, 20);
	}

	/**
	 * This will display the scores at the end of the game to all the players and spectators.
	 */
	void showGameScores(HashMap<Player, Integer> scoreMap) {
		//Set the current game state. Used in PlayerMoveEvent listener (PlayerMove) to stop players from moving.
		PLUGIN.getGameManager().setGameState(GameState.ARENA_SHOW_SCORES);

		ArrayList<String> playerPlaces = new ScoresToPlaces().scoresToPlaces(scoreMap);

		//Show the game rules.
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(Messages.GAME_BAR_BOTTOM.toString());
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(ChatColor.RED + "1st " + playerPlaces.get(0));
		
		if (playerPlaces.size() >= 2) {
			Bukkit.broadcastMessage(ChatColor.YELLOW + "2nd " + playerPlaces.get(1));
		}
		
		if (playerPlaces.size() >= 3) {
			Bukkit.broadcastMessage(ChatColor.GREEN + "3rd " + playerPlaces.get(2));
		}
		
		Bukkit.broadcastMessage("");

		//Show players how they scored.
		for (int i = 0; i < playerPlaces.size(); i++) {
			for (Player players: Bukkit.getOnlinePlayers()) {
				if (playerPlaces.get(i).equals(players.getName()) && i > 2) {
					int place = i + 1;
					players.sendMessage(ChatColor.LIGHT_PURPLE + "You placed " + place + "th place.");	
				}
			}
		}
		
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(Messages.GAME_BAR_BOTTOM.toString());

		new BukkitRunnable() {
			int countdown = 8;

			@Override
			public void run() {

				//Show returning to lobby message.
				if (countdown == 2) {
					Bukkit.broadcastMessage(ChatColor.GOLD + "Returning to game lobby...");
				}

				//Cancel this thread and end the game.
				if (countdown == 0) {
					cancel();

					//End the game, and prepare to start another one.
					PLUGIN.getGameManager().endGame(true);
				}

				countdown--;
			}
		}.runTaskTimer(PLUGIN, 0, 20);
	}

	/**
	 * Loads the needed assets from the minigame plugin.
	 */
	void loadGameWorld() {
		String name = PLUGIN.getMinigamePluginManager().getMinigameBase().getArenaWorldName();

		File wc = Bukkit.getServer().getWorldContainer();
		File destinationFolder = new File(wc + File.separator + name);
		File backupFolder = new File(wc + File.separator + "worlds" + File.separator + name.concat("_backup"));

		if (WORLD_DUPE.getWorld() == null) {
			//Copy the backup world folder.
			try {
				WORLD_DUPE.copyFolder(backupFolder, destinationFolder);
			} catch (IOException e) {
				e.printStackTrace();
			}

			//Show success message in console.
			Bukkit.getServer().getLogger().info("[MPMG-Framework] Copied world: " + name);

			//Load the map into memory
			WORLD_DUPE.loadWorld(name);

			//Stop natural spawning of entities.
			WORLD_DUPE.stopEntitySpawns();
			
			//Cleanup any map entities.
			WORLD_DUPE.clearEntities();
			
		} else {
			Bukkit.getServer().getLogger().info("[MPMG-Framework] World var not null? We will not load the next world.");
		}
	}

	/**
	 * Disables the games loaded assets.
	 */
	void unloadGameWorld() {
		String name = PLUGIN.getMinigamePluginManager().getMinigameBase().getArenaWorldName();
		File wc = Bukkit.getServer().getWorldContainer();
		File destinationFolder = new File(wc + File.separator + name);

		//Unload the current game map from memory.
		WORLD_DUPE.unloadWorld();

		//Delete the world folder.
		try {
			WORLD_DUPE.deleteFolder(destinationFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Teleports a player to the game arena.
	 * @param player The player who will be teleported.
	 */
	public void tpToGameWorld(Player player, double x, double y, double z) {
		//Teleport to game world
		player.teleport(new Location(WORLD_DUPE.getWorld(), x, y, z));
	}

	/**
	 * Returns and instance of the world duplicator.
	 * @return An instance of the world duplicator.
	 */
	public WorldDuplicator getWorldDupe() {
		return WORLD_DUPE;
	}

	/**
	 * This HashMap holds the data for a Player and their in-game spawn position.
	 * <p>
	 * This is used to teleport players back to their spawn position if the game
	 * rules are still being shown.
	 * @return Returns an instance of the PlayerSpawns HashMap.
	 */
	public HashMap<Player, Location> getPlayerSpawns() {
		return playerSpawns;
	}

	/**
	 * Gets the arena scoreboard component.
	 * @return Returns an instance of the lobby scoreboard component.
	 */
	public ArenaScoreboard getScoreboard() {
		return scoreboard;
	}
}
