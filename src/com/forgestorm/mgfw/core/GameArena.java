package com.forgestorm.mgfw.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.forgestorm.mgfw.MGFramework;
import com.forgestorm.mgfw.core.constants.GameState;
import com.forgestorm.mgfw.core.constants.Messages;
import com.forgestorm.mgfw.core.display.scoreboard.ArenaScoreboard;
import com.forgestorm.mgfw.core.gui.PlayerTracker;
import com.forgestorm.mgfw.core.gui.SpectatorMenu;
import com.forgestorm.mgfw.core.player.ArenaPlayer;
import com.forgestorm.mgfw.core.player.SpectatorPlayer;
import com.forgestorm.mgfw.core.worlds.WorldDuplicator;
import com.forgestorm.mgfw.profiles.PlayerProfile;
import com.forgestorm.mgfw.spawner.PlayerSpawner;
import com.forgestorm.mgfw.util.CenterChatText;
import com.forgestorm.mgfw.util.ItemBuilder;
import com.forgestorm.mgfw.util.ScoresToPlaces;
import com.forgestorm.servercore.core.display.BossBarAnnouncer;
import com.forgestorm.servercore.core.display.FloatingMessage;

import net.md_5.bungee.api.ChatColor;

public class GameArena {

	private final MGFramework PLUGIN;
	private final WorldDuplicator WORLD_DUPE;

	private HashMap<Player, Location> playerSpawns;
	private HashMap<Player, SpectatorMenu> spectatorOptionsMenu;
	private HashMap<Player, PlayerTracker> spectatorTrackerMenu;
	private BossBarAnnouncer spectatorBar;
	private ArenaScoreboard scoreboard;

	public GameArena(MGFramework plugin) {
		PLUGIN = plugin;
		WORLD_DUPE = new WorldDuplicator();
		spectatorOptionsMenu = new HashMap<Player, SpectatorMenu>();
		spectatorTrackerMenu = new HashMap<Player, PlayerTracker>();
		spectatorBar = new BossBarAnnouncer(Messages.BOSS_BAR_SPECTATOR_MESSAGE.toString());
		scoreboard = new ArenaScoreboard(PLUGIN);
	}

	/**
	 * Setups an arena player.
	 * @param player The player who will be setup for the game arena.
	 */
	private void setupArenaPlayer(Player player) {
		new ArenaPlayer(player, GameMode.ADVENTURE, true, false);

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
		ConcurrentMap<Integer, ArrayList<Player>> sortedTeams = PLUGIN.getGameManager().getTeamSelector().getSortedTeams();

		//Clear the location of the playerSpawns HashMap
		if (playerSpawns != null && !playerSpawns.isEmpty()) {
			playerSpawns.clear();
		}

		//Spawn players and record their spawn locations.
		playerSpawns = spawner.spawnPlayers(sortedTeams);

		//Do some additional player setup.
		for (Player players: Bukkit.getOnlinePlayers()) {
			setupArenaPlayer(players);
		}
	}

	/**
	 * This will setup a spectator for a player.
	 * @param spectator The player that will be setup as a spectator.
	 */
	public void setupSpectator(Player spectator) {
		PlayerProfile profile = PLUGIN.getProfile(spectator);
		GameManager gameManager = PLUGIN.getGameManager();
		MinigamePluginManager mpm = PLUGIN.getMinigamePluginManager();

		//Check if the minigame should end.
		if (gameManager.shouldMinigameEnd()) {
			gameManager.endGame(true);
		}
		
		//Set to spectator on profile.
		profile.setSpectator(true);
		
		//Do spectator setup.
		new SpectatorPlayer(spectator, GameMode.ADVENTURE, false, true);

		hideSpectators();

		//Teleport player to spectator spawn.
		spectator.teleport(mpm.getMinigameTeams().getSpectatorSpawnLocation());

		//Setup arena scoreboard
		scoreboard.addPlayer(spectator);

		//Show Bossbar Announcer
		spectatorBar.showBossBar(spectator);

		//Assign the spectator a new spectator menu.
		spectatorOptionsMenu.put(spectator, new SpectatorMenu(PLUGIN, spectator));

		//Give Spectator tracker menu item.
		ItemStack spectatorTrackerItem = new ItemBuilder(Material.COMPASS).setTitle(ChatColor.YELLOW + "" + ChatColor.BOLD + "Player Tracker").build();
		spectator.getInventory().setItem(4, spectatorTrackerItem);

		//Give Spectator options menu item.
		ItemStack spectatorMenuItem = new ItemBuilder(Material.REDSTONE).setTitle(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Spectator Menu").build();
		spectator.getInventory().setItem(5, spectatorMenuItem);

		//Assign the spectator a new spectator menu.
		spectatorTrackerMenu.put(spectator, new PlayerTracker(PLUGIN, spectator));
		
		//Give Spectator tracker menu item.
		ItemStack spectatorServerExit = new ItemBuilder(Material.WATCH).setTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "Back To Lobby").build();
		spectator.getInventory().setItem(8, spectatorServerExit);
	}

	private void hideSpectators() {

		for (Player spectators: Bukkit.getOnlinePlayers()) {
			boolean isSpectator = PLUGIN.getProfile(spectators).isSpectator();

			//If this player is a spectator lets hide them from the other players.
			if (isSpectator) {

				//Now loop through all players and hide them from spectators.
				for(Player players: Bukkit.getOnlinePlayers()) {
					boolean isSpectatorToo = PLUGIN.getProfile(players).isSpectator();

					if (!isSpectatorToo) {
						players.hidePlayer(spectators);
					}
				}
			}
		}
	}

	/**
	 * Removes a spectator.
	 * @param spectator The spectator we will remove.
	 */
	private void removeSpectator(Player spectator) {
		//Remove the spectator bar.
		spectatorBar.removeBossBar(spectator);

		//Remove the spectators "spectator menu."
		spectatorOptionsMenu.remove(spectator);

		//Remove the spectators "player tracker menu."
		spectatorTrackerMenu.remove(spectator);
	}

	/**
	 * Removes all the spectators.
	 */
	void removeAllSpectators() {
		for (Player spectators: Bukkit.getOnlinePlayers()) {
			removeSpectator(spectators);
		}
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
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(Messages.GAME_BAR_RULES.toString());
		Bukkit.broadcastMessage("");

		//Loop through and show the game rules.
		for (int i = 0; i < gameRules.size(); i++) {
			Bukkit.broadcastMessage(new CenterChatText().centerMessage(ChatColor.YELLOW + gameRules.get(i)));
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
						players.playSound(players.getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 1f);
					}
				} else if (countdown == 1) {
					String title = ChatColor.RED + Integer.toString(countdown);

					//Show players a countdown message.
					for (Player players: Bukkit.getOnlinePlayers()) {
						new FloatingMessage().sendFloatingMessage(players, title, "");
						players.playSound(players.getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 1f);
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
					
					//Let the plugin know to start the game.
					PLUGIN.getMinigamePluginManager().getMinigameBase().startGame();
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
		
		CenterChatText cct = new CenterChatText();
		ArrayList<String> playerPlaces = new ScoresToPlaces().scoresToPlaces(scoreMap);
		
		//Show the game rules.
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(Messages.GAME_BAR_SCORES.toString());
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(cct.centerMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "1st " + ChatColor.GREEN + playerPlaces.get(0)));

		if (playerPlaces.size() >= 2) {
			Bukkit.broadcastMessage(cct.centerMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "2nd " + ChatColor.AQUA + playerPlaces.get(1)));
		}

		if (playerPlaces.size() >= 3) {
			Bukkit.broadcastMessage(cct.centerMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "3rd " + ChatColor.LIGHT_PURPLE + playerPlaces.get(2)));
		}

		//Show players how they scored.
		for (int i = 0; i < playerPlaces.size(); i++) {
			for (Player players: Bukkit.getOnlinePlayers()) {
				if (playerPlaces.get(i).equals(players.getName()) && i > 2) {
					int place = i + 1;
					Bukkit.broadcastMessage("");
					players.sendMessage(new CenterChatText().centerMessage(ChatColor.RED + "You placed " + place + "th place."));	
				}
			}
		}

		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(Messages.GAME_BAR_BOTTOM.toString());
		Bukkit.broadcastMessage("");
		
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

	public SpectatorMenu getSpectatorMenu(Player player) {
		return spectatorOptionsMenu.get(player);
	}

	public PlayerTracker getSpectatorTrackerMenu(Player player) {
		return spectatorTrackerMenu.get(player);
	}

	/**
	 * Gets the arena scoreboard component.
	 * @return Returns an instance of the lobby scoreboard component.
	 */
	public ArenaScoreboard getScoreboard() {
		return scoreboard;
	}
}
