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
import com.forgestorm.mgfw.core.teams.spawner.PlayerSpawner;
import com.forgestorm.mgfw.core.worlds.WorldDuplicator;

import net.md_5.bungee.api.ChatColor;

public class GameArena {

	private final MGFramework PLUGIN;
	private final WorldDuplicator WORLD_DUPE;
	
	private HashMap<Player, Location> playerSpawns;

	public GameArena(MGFramework plugin) {
		PLUGIN = plugin;
		WORLD_DUPE = new WorldDuplicator();
	}
	
	public void setupArenaPlayer(Player player) {
		//TODO: Give kit
		//TODO: Show Scoreboard
		//TODO: Show Bossbar Announcer
	}
	
	public void showGameRules() {
		//Set the current game state. Used in PlayerMoveEvent listener (PlayerMove) to stop players from moving.
		PLUGIN.getGameManager().setGameState(GameState.ARENA_SHOW_RULES);
		
		//Show the game rules.
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(Messages.GAME_BAR_BOTTOM.toString());
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(ChatColor.GREEN + "GAME RULES WILL GO HERE");
		Bukkit.broadcastMessage(ChatColor.GREEN + "GAME RULES WILL GO HERE");
		Bukkit.broadcastMessage(ChatColor.GREEN + "GAME RULES WILL GO HERE");
		Bukkit.broadcastMessage(ChatColor.GREEN + "GAME RULES WILL GO HERE");
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
		
		//TODO: Set the player to the spectator scoreboard team.
		//TODO: Show Bossbar Announcer
		
		//Send spectator notification message.
		String title = "Hello, Spectator!";
		String subtitle = ChatColor.GRAY + "Another game will start soon!";
		new FloatingMessage().sendFloatingMessage(player, title, subtitle);
	}
	
	/**
	 * Loads the needed assets from the minigame plugin.
	 */
	public void loadGameWorld() {
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

			//Cleanup any map entities.
			WORLD_DUPE.clearEntities();
		} else {
			Bukkit.getServer().getLogger().info("[MPMG-Framework] World var not null? We will not load the next world.");
		}
	}

	/**
	 * Disables the games loaded assets.
	 */
	public void unloadGameWorld() {
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
	 * Teleport all players in the server to the game world.
	 */
	public void setupAllArenaPlayers() {
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
	 * Teleports a player to the game arena.
	 * 
	 * @param player The player who will be teleported.
	 */
	public void tpToGameWorld(Player player, double x, double y, double z) {
		//Teleport to game world
		player.teleport(new Location(WORLD_DUPE.getWorld(), x, y, z));
	}

	/**
	 * Returns and instance of the world duplicator.
	 * 
	 * @return An instance of the world duplicator.
	 */
	public WorldDuplicator getWorldDupe() {
		return WORLD_DUPE;
	}

	public HashMap<Player, Location> getPlayerSpawns() {
		return playerSpawns;
	}

	public void setPlayerSpawns(HashMap<Player, Location> playerSpawns) {
		this.playerSpawns = playerSpawns;
	}
}
