package com.minepile.mpmgfw.core.plugins;

import java.io.File;
import java.util.ArrayList;

/**
 * This class is designed to find plugins in a given directory.
 * 
 * @author Andrew
 * 
 */
public class PluginFinder {
	
	private final String FILE_PATH;
	private ArrayList<File> plugins = new ArrayList<>();
	
	public PluginFinder(String filePath) {
		this.FILE_PATH = filePath;
		findPlugins();
	}

	
	/**
	 * This will find plugins in the games directory.
	 */
	private void findPlugins() {
		File dir = new File(FILE_PATH);
		File[] files = dir.listFiles();
		
		//Loop through files in directory and add them to plugins array.
		for (File gameFile : files) {
			if(!plugins.contains(gameFile)) {
				plugins.add(gameFile);
			}
		}
	}

	/**
	 * Gets a list of Files found using the findPlugins() Method.
	 * Warning, this will return a list of all files.
	 * 
	 * @return A list of file names.
	 */
	public ArrayList<File> getPlugins() {
		return plugins;
	}
}
