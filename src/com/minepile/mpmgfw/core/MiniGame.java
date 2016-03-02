package com.minepile.mpmgfw.core;

import org.bukkit.World;

import com.minepile.mpmgfw.core.worlds.WorldDuplicator;

public abstract class MiniGame {

	private WorldDuplicator worldDupe = new WorldDuplicator();
	
	public abstract void loadKits();
	public abstract World loadWorld();
	public abstract String loadGameDescription();
	public abstract Integer loadGameTimeLength();
	
	public WorldDuplicator getWorldDupe() {
		return worldDupe;
	}
}
