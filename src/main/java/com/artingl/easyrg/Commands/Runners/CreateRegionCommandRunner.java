package com.artingl.easyrg.Commands.Runners;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.Storage.FirstPositionItem;
import com.artingl.easyrg.Storage.SecondPositionItem;
import com.artingl.easyrg.misc.Regions.Region;
import com.artingl.easyrg.misc.Regions.RegionClaimResult;
import com.artingl.easyrg.misc.Regions.RegionPosition;
import com.artingl.easyrg.misc.Utilities.ChatUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CreateRegionCommandRunner implements CommandRunner {

    @Override
    public boolean run(Player player, World world, String[] args) {
        if (args.length < 2) {
            ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().get("not-enough-arguments-provided").toString());
            return false;
        }

        FirstPositionItem pos1 = (FirstPositionItem) PluginMain.storage.getValue(player, FirstPositionItem.class);
        SecondPositionItem pos2 = (SecondPositionItem) PluginMain.storage.getValue(player, SecondPositionItem.class);

        if (pos1 == null || pos2 == null) {
            ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().get("sel-no-selection").toString());
            return false;
        }

        int minX = Math.min(pos1.getLocation().getBlockX(), pos2.getLocation().getBlockX());
        int minY = Math.min(pos1.getLocation().getBlockY(), pos2.getLocation().getBlockY());
        int minZ = Math.min(pos1.getLocation().getBlockZ(), pos2.getLocation().getBlockZ());
        int maxX = Math.max(pos1.getLocation().getBlockX(), pos2.getLocation().getBlockX());
        int maxY = Math.max(pos1.getLocation().getBlockY(), pos2.getLocation().getBlockY());
        int maxZ = Math.max(pos1.getLocation().getBlockZ(), pos2.getLocation().getBlockZ());

        String regionName = args[1];
        Region region = new Region(
                new RegionPosition(minX, minY, minZ),
                new RegionPosition(maxX, maxY, maxZ),
                player.getWorld(),
                List.of(player.getUniqueId()),
                regionName
        );

        Map.Entry<Boolean, RegionClaimResult> result = region.claim();

        if (!result.getKey()) {
            switch (result.getValue()) {
                case NAME_EXISTS: {
                    ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().get("region-name-exists").toString());
                    break;
                }

                case OVERLAPS_WITH_REGION: {
                    ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().get("region-overlaps").toString());
                    break;
                }
            }

            return false;
        }

        ChatUtils.sendDecoratedMessage(player,
                MessageFormat.format(
                        PluginMain.instance.getLanguage().get("region-created").toString(),
                        region.getName()
                )
        );

        PluginMain.storage.remove(player.getUniqueId());

        return true;
    }

}
