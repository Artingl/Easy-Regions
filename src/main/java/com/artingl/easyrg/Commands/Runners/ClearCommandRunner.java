package com.artingl.easyrg.Commands.Runners;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.misc.Utilities.ChatUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ClearCommandRunner implements CommandRunner {

    @Override
    public boolean run(Player player, World world, String[] args) {
        PluginMain.storage.remove(player.getUniqueId());
        ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().get("sel-clear").toString());

        return true;
    }

}
