package com.forgestorm.mgfw.core.kits.spawner;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.forgestorm.mgfw.core.kits.KitSelector;

public class EntitySpawner extends Spawner{
	
	private final KitSelector kitSelector;
	
	public EntitySpawner(KitSelector kitSelector) {
		this.kitSelector = kitSelector;
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
		kitSelector.getKitEntityUUID().add(uuid);
		
		//Add the location of the entity to the main kit class.
		kitSelector.getKitLocations().put(uuid, location);
	}
}