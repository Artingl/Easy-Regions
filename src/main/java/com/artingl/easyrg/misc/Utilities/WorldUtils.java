package com.artingl.easyrg.misc.Utilities;

import org.bukkit.Material;

public class WorldUtils {

    public static boolean isMaterialTransmitter(Material material) {
        return material == Material.REDSTONE_WIRE
                || material.getKey().getKey().contains("Button")
                || material.getKey().getKey().contains("Redstone")
                || material.getKey().getKey().contains("Plate")
                || material == Material.REDSTONE_TORCH
                || material == Material.TRIPWIRE_HOOK
                || material == Material.TARGET
                || material == Material.TNT
                || material == Material.OBSERVER
                || material == Material.REPEATER
                || material == Material.COMPARATOR
                || material == Material.TRAPPED_CHEST
                || material == Material.LECTERN
                || material == Material.REDSTONE_LAMP
                || material == Material.LEVER
                || material == Material.REDSTONE_BLOCK
                || material == Material.DETECTOR_RAIL
                || material == Material.DAYLIGHT_DETECTOR
                || material.getKey().getKey().contains("plate")
                || material.getKey().getKey().contains("button")
                || material.getKey().getKey().contains("redstone");
    }

}
