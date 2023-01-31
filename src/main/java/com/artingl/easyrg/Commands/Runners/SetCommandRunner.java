package com.artingl.easyrg.Commands.Runners;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.Storage.FirstPositionItem;
import com.artingl.easyrg.Storage.SecondPositionItem;
import com.artingl.easyrg.misc.Utilities.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class SetCommandRunner implements CommandRunner {

    @Override
    public boolean run(Player player, World world, String[] args) {
        if (args.length < 2) {
            ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().get("not-enough-arguments-provided").toString());
            return false;
        }

        String[] nameKey = args[1].split(":");
        String key = nameKey.length == 1 ? nameKey[0] : nameKey[1];

        Material material = Material.getMaterial(key.toUpperCase());

        if (material == null) {
            ChatUtils.sendDecoratedMessage(player,
                    MessageFormat.format(
                            PluginMain.instance.getLanguage().get("sel-set-invalid-block").toString(),
                            key
                    )
            );

            return false;
        }

        FirstPositionItem pos1 = (FirstPositionItem) PluginMain.storage.getValue(player, FirstPositionItem.class);
        SecondPositionItem pos2 = (SecondPositionItem) PluginMain.storage.getValue(player, SecondPositionItem.class);

        if (pos1 == null || pos2 == null) {
            ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().get("sel-no-selection").toString());
            return false;
        }

        ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().get("sel-processing").toString());

        Bukkit.getScheduler().runTaskAsynchronously(PluginMain.instance, () -> {
            int minX = Math.min(pos1.getLocation().getBlockX(), pos2.getLocation().getBlockX());
            int minZ = Math.min(pos1.getLocation().getBlockZ(), pos2.getLocation().getBlockZ());
            int maxX = Math.max(pos1.getLocation().getBlockX(), pos2.getLocation().getBlockX());
            int maxZ = Math.max(pos1.getLocation().getBlockZ(), pos2.getLocation().getBlockZ());
            int minY = Math.min(pos1.getLocation().getBlockY(), pos2.getLocation().getBlockY());
            int maxY = Math.max(pos1.getLocation().getBlockY(), pos2.getLocation().getBlockY());

        });

        return true;
    }

}
