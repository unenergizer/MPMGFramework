package com.forgestorm.mgfw.spawner;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.forgestorm.mgfw.selector.TeamSelector;

public class TeamSpawner extends Spawner {
	
	private final TeamSelector teamSelector;
	
	public TeamSpawner(TeamSelector teamSelector) {
		this.teamSelector = teamSelector;
	}
	
	/**
	 * Spawns a team entity mob at a specified location.
	 */
	@Override
	public void spawnEntity(int teamID, String teamName, Location location, EntityType entityType) {
		//Spawn the entity.
		LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, entityType);
		UUID uuid = entity.getUniqueId();
		
		entity.setCustomName(teamName);
		entity.setCustomNameVisible(true);
		entity.setRemoveWhenFarAway(false);
		entity.setCanPickupItems(false);
		entity.setCollidable(false);

		//Add potion effects to lobby entity.
		PotionEffect noWalk = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 10);
		PotionEffect noJump = new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -10);

		entity.addPotionEffect(noWalk);
		entity.addPotionEffect(noJump);
		
		//Add the team selection entities UUID's to an array list.
		teamSelector.getTeamEntityUUID().add(uuid);
		
		//Add the team id and location of the main team classes.
		//This is for holograms.
		teamSelector.getTeamHologramLocations().put(teamID, location);
	}
}
