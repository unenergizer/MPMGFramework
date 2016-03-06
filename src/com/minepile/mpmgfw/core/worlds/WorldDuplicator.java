package com.minepile.mpmgfw.core.worlds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class WorldDuplicator {

	private World world;

	/**
	 * Load a world directory into memory for use.
	 * 
	 * @param worldName The name of the world you want to load into memory.
	 */
	public void loadWorld(String worldName) {
		//Load world into memory.
		WorldCreator wc = new WorldCreator(worldName);
		wc.createWorld();

		//World settings
		world = Bukkit.getWorld(worldName);

	}

	/**
	 * Remove the loaded game world from memory.
	 */
	public void unloadWorld() {
		//Unload the world and do not save it.
		Server bukkit = Bukkit.getServer();
		Logger logger = bukkit.getLogger();
		
		if(bukkit.unloadWorld(world, false)) {
			logger.info("[MPMG-Framework] World " + world.getName() + " was unloaded.");
		} else {
			logger.warning("[MPMG-Framework] World " + world.getName() + " was NOT unloaded.");
		}
	}

	/**
	 * Removes entities from the loaded game world.
	 */
	public void clearEntities(){
		// Despawn any animals or monsters.
		for (Entity entity : world.getEntities()) {
			if (!(entity instanceof Player)) {
				entity.remove();
			}
		}
	}

	/**
	 * Copies a world directory to another directory.
	 * 
	 * @param src The source destination of the folder to copy.
	 * @param dest The end destination to copy the folder to.
	 * @throws IOException
	 */
	public void copyFolder(File src, File dest) throws IOException {

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
	 * Delete's a file directory.
	 * 
	 * @param file The file or folder that will be deleted.
	 * @throws IOException
	 */
	public void deleteFolder(File file) throws IOException {

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
					deleteFolder(fileDelete);
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
	 *  Get world instance.
	 * @return Returns an instance of the game world.
	 */
	public World getWorld() {
		return world;
	}
}
