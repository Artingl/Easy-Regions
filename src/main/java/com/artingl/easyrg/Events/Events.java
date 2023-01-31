package com.artingl.easyrg.Events;

import com.artingl.easyrg.PluginMain;
import org.bukkit.Bukkit;

public class Events {

    private static PlayerEvents playerEvents = new PlayerEvents();
    private static RegionEvents regionEvents = new RegionEvents();
    private static WorldEvents worldEvents = new WorldEvents();

    public static void register() {
        Bukkit.getPluginManager().registerEvents(playerEvents, PluginMain.instance);
        Bukkit.getPluginManager().registerEvents(regionEvents, PluginMain.instance);
        Bukkit.getPluginManager().registerEvents(worldEvents, PluginMain.instance);
    }

}
