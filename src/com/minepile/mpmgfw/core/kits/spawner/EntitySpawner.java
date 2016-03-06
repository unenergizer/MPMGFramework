package com.minepile.mpmgfw.core.kits.spawner;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;

public class EntitySpawner {


	public void setEntities(ArrayList<ArrayList<Location>> platformLocation) {

		for (int i = 0; i < platformLocation.size(); i++) {
			Location loc1 = platformLocation.get(i).get(0);
			Location loc2 = platformLocation.get(i).get(1);

			spawnEntity(loc1, loc2, i);
		}
	}

	private void spawnEntity(Location loc1, Location loc2, double i) {

		double minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
		double minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		double maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
		double maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

		double totalX = minX + maxX;
		double totalZ = minZ + maxZ;

		double displacementX = totalX/2;
		double displacementZ = totalZ/2;
		
		double test = -0;
		
		World world = loc1.getWorld();
		Location spawnloc = new Location(world, displacementX + .5, loc1.getY() + 1, displacementZ + .5);
		
		LivingEntity entity = (LivingEntity) world.spawnEntity(spawnloc, EntityType.COW);
		entity.setCustomName(ChatColor.GREEN + "KIT ENTITY " + i);
		entity.setCustomNameVisible(true);
		entity.setRemoveWhenFarAway(false);
		entity.setCanPickupItems(false);

		//Add potion effects to lobby entity.

		PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOW, 60*60*20, 10);
		PotionEffect potionEffect2 = new PotionEffect(PotionEffectType.JUMP, 60*60*20, -10);

		entity.addPotionEffect(potionEffect);
		entity.addPotionEffect(potionEffect2);


	}
}
