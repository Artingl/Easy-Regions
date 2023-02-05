package com.artingl.easyrg.misc.Regions;

import org.bukkit.Location;

public class RegionPosition {

    private final int x;
    private final int y;
    private final int z;

    public RegionPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public RegionPosition(Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "RegionPosition{x=" + x + ", y=" + y + ", z=" + z + "}";
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RegionPosition))
            return false;

        return ((RegionPosition) obj).x == x
                && ((RegionPosition) obj).y == y
                && ((RegionPosition) obj).z == z;
    }
}
