package com.artingl.easyrg.misc.Regions;

import org.bukkit.Location;

public class RegionPosition {

    private final double x;
    private final double y;
    private final double z;

    public RegionPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public RegionPosition(Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
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
