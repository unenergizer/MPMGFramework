package com.minepile.mpmgfw;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.minepile.mpmgfw.core.GameManager;
import com.minepile.mpmgfw.listeners.PlayerJoin;
import com.minepile.mpmgfw.listeners.PlayerQuit;

import lombok.Getter;

@Getter
public class MPMGFramework extends JavaPlugin {
	
	private GameManager gameManager;
	
	@Override
	public void onEnable() {
		
		gameManager = new GameManager(this);
		
		//Edit world properties.
		Bukkit.setSpawnRadius(0);
        World world = Bukkit.getWorlds().get(0);
        world.setSpawnFlags(false, false);
        world.setGameRuleValue("doMobSpawning", "false");
        
        //Remove entities from the world.
        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof Player) && entity instanceof LivingEntity) {
                entity.remove();
            }
        }
		
        //Register Event listeners
        registerListeners();
	}
	
	@Override 
	public void onDisable() {
		//TODO
	}
	
	private void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayerJoin(this), this);
		pm.registerEvents(new PlayerQuit(this), this);
	}

	public GameManager getGameManager() {
		return gameManager;
	}
}
