package com.artingl.easyrg.Commands.Runners;

import org.bukkit.World;
import org.bukkit.entity.Player;

public interface CommandRunner {

     boolean run(Player player, World world, String[] args);

}
