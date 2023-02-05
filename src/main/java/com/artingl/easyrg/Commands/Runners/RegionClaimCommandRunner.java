package com.artingl.easyrg.Commands.Runners;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.Storage.FirstPositionItem;
import com.artingl.easyrg.Storage.SecondPositionItem;
import com.artingl.easyrg.misc.Regions.Region;
import com.artingl.easyrg.misc.Regions.RegionClaimResult;
import com.artingl.easyrg.misc.Regions.RegionMember;
import com.artingl.easyrg.misc.Regions.RegionPosition;
import com.artingl.easyrg.misc.Utilities.ChatUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;

public class RegionClaimCommandRunner implements CommandRunner {

    @Override
    public boolean run(Player player, World world, String[] args) {
        if (args.length < 2) {
            ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("not-enough-arguments-provided"));
            PluginMain.instance.playCommandErrorSound(player);
            return false;
        }

        FirstPositionItem pos1 = (FirstPositionItem) PluginMain.storage.getValue(player, FirstPositionItem.class);
        SecondPositionItem pos2 = (SecondPositionItem) PluginMain.storage.getValue(player, SecondPositionItem.class);

        if (pos1 == null || pos2 == null) {
            ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("sel-no-selection"));
            PluginMain.instance.playCommandErrorSound(player);
            return false;
        }

        int minX = Math.min(pos1.getLocation().getBlockX(), pos2.getLocation().getBlockX());
        int minY = Math.min(pos1.getLocation().getBlockY(), pos2.getLocation().getBlockY());
        int minZ = Math.min(pos1.getLocation().getBlockZ(), pos2.getLocation().getBlockZ());
        int maxX = Math.max(pos1.getLocation().getBlockX(), pos2.getLocation().getBlockX());
        int maxY = Math.max(pos1.getLocation().getBlockY(), pos2.getLocation().getBlockY());
        int maxZ = Math.max(pos1.getLocation().getBlockZ(), pos2.getLocation().getBlockZ());

        List<RegionMember> members = new ArrayList<>();
        members.add(RegionMember.mask(player.getUniqueId(), RegionMember.Permissions.OWNER));

        String regionName = args[1];
        Region region = new Region(
                new RegionPosition(minX, minY, minZ),
                new RegionPosition(maxX, maxY, maxZ),
                player.getWorld(),
                members,
                regionName
        );

        members.clear();
        RegionClaimResult result = region.claim();

        switch (result) {
            case NAME_EXISTS: {
                ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("region-name-exists"));
                PluginMain.instance.playCommandErrorSound(player);
                return false;
            }

            case OVERLAPS_WITH_REGION: {
                ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("region-overlaps"));
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
                        Objects.requireNonNull(PluginMain.instance.getLanguage().getString("region-created")),
                        region.getName()
                )
        );

        PluginMain.storage.remove(player.getUniqueId());

        return true;
    }

}
