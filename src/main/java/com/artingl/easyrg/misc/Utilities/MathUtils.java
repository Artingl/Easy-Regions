package com.artingl.easyrg.misc.Utilities;

import org.bukkit.Location;

public class MathUtils {

    public static double calculateLength(double x0, double y0, double z0, double x1, double y1, double z1) {
        double xd = x0 - x1;
        double yd = y0 - y1;
        double zd = z0 - z1;
        return Math.sqrt(xd * xd + yd * yd + zd * zd);
    }

    public static double calculateLength(double x0, double y0, double x1, double y1) {
        double xd = x0 - x1;
        double yd = y0 - y1;
        return Math.sqrt(xd * xd + yd * yd);
    }

    public static double calculateLength(Location loc0, Location loc1) {
        double xd = loc0.getX() - loc1.getX();
        double yd = loc0.getY() - loc1.getY();
        double zd = loc0.getZ() - loc1.getZ();
        return Math.sqrt(xd * xd + yd * yd + zd * zd);
    }

    public static double calculateLength(Location loc0, double x1, double y1, double z1) {
        double xd = loc0.getX() - x1;
        double yd = loc0.getY() - y1;
        double zd = loc0.getZ() - z1;
        return Math.sqrt(xd * xd + yd * yd + zd * zd);
    }

    public static double calculateLength(double x0, double y0, double z0, Location loc1) {
        double xd = x0 - loc1.getX();
        double yd = y0 - loc1.getY();
        double zd = z0 - loc1.getZ();
        return Math.sqrt(xd * xd + yd * yd + zd * zd);
    }

}
