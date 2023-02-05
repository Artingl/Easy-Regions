package com.artingl.easyrg.Commands.Runners;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.misc.Regions.RegionRemoveResult;
import com.artingl.easyrg.misc.Utilities.ChatUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.Objects;

public class RegionUnclaimCommandRunner implements CommandRunner {

    @Override
    public boolean run(Player player, World world, String[] args) {
        if (args.length < 2) {
            ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("not-enough-arguments-provided"));
            PluginMain.instance.playCommandErrorSound(player);
            return false;
        }
        String regionName = args[1];
        RegionRemoveResult result = PluginMain.instance.getRegionsRegistry().unregisterRegion(player, regionName);

        switch (result) {
            case NOT_EXISTS: {
                ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("region-not-exists"));
                PluginMain.instance.playCommandErrorSound(player);
                return false;
            }

            case NO_PERMISSIONS: {
                ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("region-no-permissions"));
                PluginMain.instance.playCommandErrorSound(player);
                return false;
            }

            case INTERNAL_ERROR: {
                ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("internal-error"));
                PluginMain.instance.playCommandErrorSound(player);
                return false;
            }
        }

        ChatUtils.sendDecoratedMessage(player,
                MessageFormat.format(
                        Objects.requireNonNull(PluginMain.instance.getLanguage().getString("region-removed")),
                        regionName
                )
        );

        return true;
    }

}
