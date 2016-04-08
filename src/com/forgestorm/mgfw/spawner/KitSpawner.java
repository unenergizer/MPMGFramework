package com.forgestorm.mgfw.spawner;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.forgestorm.mgfw.selector.KitSelector;

public class KitSpawner extends Spawner {
	
	private final KitSelector kitSelector;
	
	public KitSpawner(KitSelector kitSelector) {
		this.kitSelector = kitSelector;
	}
	
	/**
	 * Spawns a kit entity at a specified location.
	 */
	@Override
	public void spawnEntity(int kitID, String kitName, Location location, EntityType entityType) {
		//Spawn the entity.
		LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, entityType);
		UUID uuid = entity.getUniqueId();
		
		entity.setCustomName(kitName);
		entity.setCustomNameVisible(true);
		entity.setRemoveWhenFarAway(false);
		entity.setCanPickupItems(false);
		entity.setCollidable(false);

		//Add potion effects to lobby entity.
		PotionEffect noWalk = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 10);
		PotionEffect noJump = new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -10);

		entity.addPotionEffect(noWalk);
		entity.addPotionEffect(noJump);
		
		//Add the kit selection entities UUID's to an array list.
		kitSelector.getKitEntityUUID().add(uuid);
	}
}
