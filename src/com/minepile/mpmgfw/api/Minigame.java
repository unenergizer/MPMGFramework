package com.minepile.mpmgfw.api;

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

    public String getWorldName() {
        return worldName;
    }
}
