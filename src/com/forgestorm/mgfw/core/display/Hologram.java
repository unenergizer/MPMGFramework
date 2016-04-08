package com.forgestorm.mgfw.core.display;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class Hologram {

	private ArrayList<ArmorStand> armorStands;

	public Hologram() {
		armorStands = new ArrayList<ArmorStand>();
	}

	/**
	 * Creates a hologram with the given text at a given location.
	 * @param name The text to display in the hologram.
	 * @param location The location to spawn the hologram.
	 */
	public void createHologram(String name, Location location) {
		//Create armor stand and save it.
		armorStands.add(spawnArmorStand(name, location));
	}

	/**
	 * Creates a hologram with the given text at a given location.
	 * @param name The text to display in the hologram.
	 * @param location The location to spawn the hologram.
	 */
	public void createHologram(ArrayList<String> name, Location location) {

		double spotsMovedDown = 0;

		//Create armor stand and save it.
		for (int i = 0; i < name.size(); i++) {
			Location adjustedLocation = location.subtract(0, spotsMovedDown, 0);
			armorStands.add(spawnArmorStand(name.get(i), adjustedLocation));

			spotsMovedDown += .3;
		}
	}

	/**
	 * This will spawn an armor stand with a hologram type setup.
	 * @param name The name to display over the hologram.
	 * @param location The location to spawn the hologram.
	 * @return Returns the generated entity.
	 */
	private ArmorStand spawnArmorStand(String name, Location location) {
		String worldName = location.getWorld().getName();
		ArmorStand stand = (ArmorStand) Bukkit.getWorld(worldName).spawnEntity(location, EntityType.ARMOR_STAND);

		//Setup armor stand.
		stand.setBasePlate(false);
		stand.setCanPickupItems(false);
		stand.setCollidable(false);
		stand.setGravity(false);
		stand.setInvulnerable(true);
		stand.setVisible(false);

		//Set the armor stands name.
		stand.setCustomName(name);
		stand.setCustomNameVisible(true);

		return stand;
	}

	/**
	 * This will completely remove and despawn a hologram.
	 */
	public void removeHolograms() {
		//Loop through and delete hologram entities.
		for (int i = 0; i < armorStands.size(); i++) {
			armorStands.get(i).remove();
		}

		//Clear array holograms.
		armorStands.clear();
	}

}
