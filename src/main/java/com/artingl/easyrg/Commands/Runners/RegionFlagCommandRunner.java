package com.artingl.easyrg.Commands.Runners;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.misc.Regions.Region;
import com.artingl.easyrg.misc.Regions.RegionFlags;
import com.artingl.easyrg.misc.Utilities.ChatUtils;
import com.sun.jdi.InvalidTypeException;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.Objects;

public class RegionFlagCommandRunner implements CommandRunner {

    @Override
    public boolean run(Player player, World world, String[] args) {
        if (args.length < 4) {
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

        RegionFlags flag = RegionFlags.byName(args[2]);

        if (flag == null) {
            ChatUtils.sendDecoratedMessage(player,
                    MessageFormat.format(
                            Objects.requireNonNull(PluginMain.instance.getLanguage().getString("invalid-flag")),
                            args[2]
                    )
            );
            PluginMain.instance.playCommandErrorSound(player);
            return false;
        }

        try {
            region.setFlag(flag, flag.retrieveType(args[3]));
        } catch (InvalidTypeException ignored) {
            ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("invalid-flag-value"));
            PluginMain.instance.playCommandErrorSound(player);
            return false;
        }

        if (region.update()) {
            ChatUtils.sendDecoratedMessage(player,
                    MessageFormat.format(
                            Objects.requireNonNull(PluginMain.instance.getLanguage().getString("flag-set")),
                            args[2],
                            region.getName(),
                            args[3].strip()
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
