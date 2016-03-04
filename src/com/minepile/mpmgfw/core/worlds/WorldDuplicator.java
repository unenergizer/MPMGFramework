package com.minepile.mpmgfw.core.worlds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class WorldDuplicator {

	private World minigameWorld;
	
	/**
	 * Load a world directory into memory for use.
	 * 
	 * @param worldName The name of the world you want to load into memory.
	 */
	public void loadWorld(String worldName) {

		if (worldName.equalsIgnoreCase("world")) {
			//"world" should be the lobby world with kit selection.
			// World settings
			minigameWorld = Bukkit.getWorld(worldName);
		} else {
			// Replace the game world to be loaded.
			replaceWorld(worldName, worldName.concat("_backup"));
			
			//Load world into memory.
			WorldCreator wc = new WorldCreator(worldName);
			wc.createWorld();
			
			//World settings
			minigameWorld = Bukkit.getWorld(worldName);
		}
	}

	public void setWorldProperties(boolean setPVP, boolean setStorm,
			int setMonsterSpawnLimit, int setAnimalSpawnLimit, int time) {
		// Set world properties.
		this.minigameWorld.setPVP(setPVP);
		this.minigameWorld.setSpawnFlags(false, false);
		this.minigameWorld.setStorm(setStorm);
		this.minigameWorld.setTime(time);

		// Despawn any animals or monsters.
		clearEntities();
	}
	
	/**
	 * Removes entities from the loaded game world.
	 */
	public void clearEntities(){
		// Despawn any animals or monsters.
		for (Entity entity : this.minigameWorld.getEntities()) {
			if (!(entity instanceof Player)) {
				entity.remove();
			}
		}
	}
	
	/**
	 * Remove the loaded game world from memory.
	 */
	public void unloadWorld() {
		Bukkit.getServer().unloadWorld(this.minigameWorld, true);
	}
	

	/**
	 * This will allow the safe replacement of worlds.
	 * 
	 * @param worldName
	 * @param backupWorldName
	 */
	public void replaceWorld(String worldName, String backupWorldName) {

		World world = Bukkit.getServer().getWorld(worldName);

		if (Bukkit.getServer().unloadWorld(world, false)
				|| !Bukkit.getServer().getWorlds().contains(worldName)) {

			File backup = new File(Bukkit.getServer().getWorldContainer()
					+ File.separator + "worlds" + File.separator
					+ backupWorldName);
			File folder = new File(Bukkit.getServer().getWorldContainer()
					+ File.separator + worldName);

			// Delete world from directory.
			try {
				deleteFile(folder);
				Bukkit.getServer()
						.getLogger()
						.info("[MPMG-Framework] World directory: " + worldName + " deleted.");
			} catch (IOException e1) {}

			// Copy world from backup.
			try {
				copyFolder(backup, folder);
				Bukkit.getServer()
						.getLogger()
						.info("[MPMG-Framework] World directory: " + worldName
								+ " copied from " + backupWorldName + ".");
			} catch (IOException e) {}

		} else {
			Bukkit.getServer()
				.getLogger()
				.info("[MPMG-Framework] Failed to replace " + worldName + "!");
		}

	}

	/**
	 * Delete's a file directory.
	 * 
	 * @param file The file or folder that will be deleted.
	 * @throws IOException
	 */
	private void deleteFile(File file) throws IOException {

		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {

				file.delete();

			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					deleteFile(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
				}
			}

		} else {
			// if file, then delete it
			file.delete();
		}
	}

	/**
	 * Copies a world directory to another directory.
	 * 
	 * @param src The source destination of the folder to copy.
	 * @param dest The end destination to copy the folder to.
	 * @throws IOException
	 */
	private void copyFolder(File src, File dest) throws IOException {

		if (src.isDirectory()) {

			// if directory not exists, create it
			if (!dest.exists()) {
				dest.mkdir();
			}

			// list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile);
			}

		} else {
			// if file, then copy it
			// Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
		}
	}

	/**
	 *  Get world instance.
	 * @return Returns an instance of the game world.
	 */
	public World getMiniGameWorld() {
		return minigameWorld;
	}
}
