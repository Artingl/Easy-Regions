package com.artingl.easyrg.Storage;

import org.bukkit.Location;

public class RegionSelectionItem extends StorageItem {

    private final Types type;
    private final Location location;

    public RegionSelectionItem(Types type, Location location) {
        this.type = type;
        this.location = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
    }

    public Types getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "PlayerSelectionsItem{type=" + type + ", location=" + location + "}";
    }

    @Override
    public void destroy() {
    }

    public enum Types {
        POS1, POS2
    }

}
