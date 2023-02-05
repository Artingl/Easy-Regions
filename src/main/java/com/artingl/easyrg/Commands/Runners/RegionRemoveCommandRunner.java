package com.artingl.easyrg.Commands.Runners;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.misc.Regions.Region;
import com.artingl.easyrg.misc.Utilities.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.Objects;

public class RegionRemoveCommandRunner implements CommandRunner {

    @Override
    public boolean run(Player player, World world, String[] args) {
        if (args.length < 3) {
            ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("not-enough-arguments-provided"));
            PluginMain.instance.playCommandErrorSound(player);
            return false;
        }

        Region region = PluginMain.instance.getRegionsRegistry().getRegion(args[1]);

        if (region == null) {
            ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("region-not-exists"));
            PluginMain.instance.playCommandErrorSound(player);
            return false;
        }
        else if (!region.hasAccess(player)) {
            ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("region-no-permissions"));
            PluginMain.instance.playCommandErrorSound(player);
            return false;
        }


        int result = region.removeMember(Bukkit.getOfflinePlayer(args[2]).getUniqueId());

        if (result == 1) {
            ChatUtils.sendDecoratedMessage(player,
                    MessageFormat.format(
                            Objects.requireNonNull(PluginMain.instance.getLanguage().getString("region-not-a-member")),
                            args[2]
                    )
            );
            PluginMain.instance.playCommandErrorSound(player);
            return false;
        }
        else if (result == 2) {
            ChatUtils.sendDecoratedMessage(player, Objects.requireNonNull(PluginMain.instance.getLanguage().getString("region-one-member")));
            PluginMain.instance.playCommandErrorSound(player);
            return false;
        }

        if (region.update()) {
            ChatUtils.sendDecoratedMessage(player,
                    MessageFormat.format(
                            Objects.requireNonNull(PluginMain.instance.getLanguage().getString("region-member-removed")),
                            args[2]
                    )
            );
        }
        else {
            ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("unable-to-save-region"));
            PluginMain.instance.playCommandErrorSound(player);
            return false;
        }

        return true;
    }

}
