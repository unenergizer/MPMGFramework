package com.minepile.mpmgfw.api;

import org.bukkit.Bukkit;
import org.bukkit.World;

public abstract class Minigame {
    protected final String name;
    protected final String worldName;

    public Minigame(final String name, final String worldName) {
        this.name = name;
        this.worldName = worldName;
    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }
}
