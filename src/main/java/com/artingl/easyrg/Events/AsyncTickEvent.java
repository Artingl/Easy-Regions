package com.artingl.easyrg.Events;

import com.artingl.easyrg.misc.Utilities.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AsyncTickEvent extends BukkitRunnable {

    private long playersListUpdateTimer;

    @Override
    public void run() {
        if (playersListUpdateTimer + 500 < System.currentTimeMillis()) {
            playersListUpdateTimer = System.currentTimeMillis();

            ChatUtils.PLAYER_NAMES_LIST.clear();

            for (Player player : Bukkit.getOnlinePlayers()) {
                ChatUtils.PLAYER_NAMES_LIST.add(player.getName());
            }
        }
    }

}
