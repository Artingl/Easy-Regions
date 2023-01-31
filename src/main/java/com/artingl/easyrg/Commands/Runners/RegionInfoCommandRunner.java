package com.artingl.easyrg.Commands.Runners;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.Storage.RegionFrameInfoItem;
import com.artingl.easyrg.Storage.FirstPositionItem;
import com.artingl.easyrg.Storage.SecondPositionItem;
import com.artingl.easyrg.misc.Regions.Region;
import com.artingl.easyrg.misc.Utilities.ChatUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class RegionInfoCommandRunner implements CommandRunner {

    @Override
    public boolean run(Player player, World world, String[] args) {
        Region region = PluginMain.instance.getRegionsRegistry().getRegionAt(player.getLocation());

        if (region == null) {
            ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().get("region-not-found").toString());
            return false;
        }

        Location loc0 = new Location(world, region.getBoundingBox().getMinX(), region.getBoundingBox().getMinY(), region.getBoundingBox().getMinZ());
        Location loc1 = new Location(world, region.getBoundingBox().getMaxX(), region.getBoundingBox().getMaxY(), region.getBoundingBox().getMaxZ());

        PluginMain.storage.setValue(player, new FirstPositionItem(loc0));
        PluginMain.storage.setValue(player, new SecondPositionItem(loc1));
        PluginMain.storage.setValue(player, new RegionFrameInfoItem(RegionFrameInfoItem.RegionFrameTypes.RG_INFO));

        ChatUtils.sendDecoratedMessage(player, region.getName());

        return true;
    }

}
