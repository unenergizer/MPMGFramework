package com.forgestorm.mgfw;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.forgestorm.mgfw.bungeecord.BungeeCord;
import com.forgestorm.mgfw.command.Admin;
import com.forgestorm.mgfw.core.GameManager;
import com.forgestorm.mgfw.core.MinigamePluginManager;
import com.forgestorm.mgfw.listeners.AsyncPlayerChat;
import com.forgestorm.mgfw.listeners.BlockBreak;
import com.forgestorm.mgfw.listeners.BlockCanBuild;
import com.forgestorm.mgfw.listeners.BlockPlace;
import com.forgestorm.mgfw.listeners.EntityCombust;
import com.forgestorm.mgfw.listeners.EntityDamage;
import com.forgestorm.mgfw.listeners.EntityDamageByEntity;
import com.forgestorm.mgfw.listeners.EntityTargetLivingEntity;
import com.forgestorm.mgfw.listeners.FoodLevelChange;
import com.forgestorm.mgfw.listeners.InventoryClick;
import com.forgestorm.mgfw.listeners.InventoryDrag;
import com.forgestorm.mgfw.listeners.ItemSpawn;
import com.forgestorm.mgfw.listeners.PlayerDropItem;
import com.forgestorm.mgfw.listeners.PlayerInteract;
import com.forgestorm.mgfw.listeners.PlayerInteractEntity;
import com.forgestorm.mgfw.listeners.PlayerJoin;
import com.forgestorm.mgfw.listeners.PlayerKick;
import com.forgestorm.mgfw.listeners.PlayerMove;
import com.forgestorm.mgfw.listeners.PlayerPickupItem;
import com.forgestorm.mgfw.listeners.PlayerQuit;
import com.forgestorm.mgfw.listeners.WeatherChange;
import com.forgestorm.mgfw.profiles.PlayerProfile;

import lombok.Getter;

@Getter
public class MGFramework extends JavaPlugin {

	private MinigamePluginManager minigamePluginManager;
	private GameManager gameManager;
	private BungeeCord bungeecord;
	private HashMap<UUID, PlayerProfile> profiles = new HashMap<>();
	
	@Override
	public void onEnable() {

		minigamePluginManager = new MinigamePluginManager();
		gameManager = new GameManager(this);
		bungeecord = new BungeeCord(this);
		
		//Register BungeeCord channels.
		getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", bungeecord);
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		//Register Event listeners
		registerListeners();
		
		//Register commands
		getCommand("admin").setExecutor(new Admin(this));
	}

	@Override
	public void onDisable() {
		gameManager.endGame(false);
	}

	/**
	 * Register Spigot/Bukkit event listeners.
	 */
	private void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();

		pm.registerEvents(new AsyncPlayerChat(this), this);
		pm.registerEvents(new BlockBreak(this), this);
		pm.registerEvents(new BlockCanBuild(this), this);
		pm.registerEvents(new BlockPlace(this), this);
		pm.registerEvents(new EntityCombust(this), this);
		pm.registerEvents(new EntityDamage(this), this);
		pm.registerEvents(new EntityDamageByEntity(this), this);
		pm.registerEvents(new PlayerInteract(this), this);
		pm.registerEvents(new EntityTargetLivingEntity(this), this);
		pm.registerEvents(new FoodLevelChange(this), this);
		pm.registerEvents(new InventoryClick(this), this);
		pm.registerEvents(new InventoryDrag(), this);
		pm.registerEvents(new ItemSpawn(), this);
		pm.registerEvents(new PlayerDropItem(this), this);
		pm.registerEvents(new PlayerInteractEntity(this), this);
		pm.registerEvents(new PlayerJoin(this), this);
		pm.registerEvents(new PlayerMove(this), this);
		pm.registerEvents(new PlayerPickupItem(this), this);
		pm.registerEvents(new PlayerQuit(this), this);
		pm.registerEvents(new WeatherChange(this), this);
		pm.registerEvents(new PlayerKick(this), this);
	}
	
	public PlayerProfile getProfile(Player player) {
		return profiles.get(player.getUniqueId());
	}

    public PlayerProfile getRemovedProfile(Player player) {
        return profiles.remove(player.getUniqueId());
    }
}
