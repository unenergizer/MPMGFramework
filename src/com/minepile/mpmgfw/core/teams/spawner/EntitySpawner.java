package com.minepile.mpmgfw.core.teams.spawner;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.minepile.mpmgfw.core.teams.TeamSelector;

public class EntitySpawner extends Spawner{
	
	private final TeamSelector teamSelector;
	
	public EntitySpawner(TeamSelector teamSelector) {
		this.teamSelector = teamSelector;
	}

	@Override
	public void spawnEntity(String kitName, Location location, EntityType entityType) {
		//Spawn the entity.
		LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, entityType);
		UUID uuid = entity.getUniqueId();
		
		entity.setCustomName(kitName);
		entity.setCustomNameVisible(true);
		entity.setRemoveWhenFarAway(false);
		entity.setCanPickupItems(false);

		//Add potion effects to lobby entity.
		PotionEffect noWalk = new PotionEffect(PotionEffectType.SLOW, 60*60*20, 10);
		PotionEffect noJump = new PotionEffect(PotionEffectType.JUMP, 60*60*20, -10);

		entity.addPotionEffect(noWalk);
		entity.addPotionEffect(noJump);
		
		//Add the kit selection entities UUID's to an array list.
		teamSelector.getTeamEntityUUID().add(uuid);
		
		//Add the location of the entity to the main kit class.
		teamSelector.getTeamLocations().put(uuid, location);
	}
}
