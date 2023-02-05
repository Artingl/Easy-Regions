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

import java.text.MessageFormat;
import java.util.List;

public class RegionInfoCommandRunner implements CommandRunner {

    @Override
    public boolean run(Player player, World world, String[] args) {
        Region[] regions = PluginMain.instance.getRegionsRegistry().getRegionCollide(player.getBoundingBox());

        if (regions == null) {
            ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("region-not-found"));
            PluginMain.instance.playCommandErrorSound(player);
            return false;
        }

        if (regions.length == 0) {
            ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("region-not-found"));
            PluginMain.instance.playCommandErrorSound(player);
            return false;
        }

        Location loc0 = new Location(world, regions[0].getBoundingBox().getMinX(), regions[0].getBoundingBox().getMinY(), regions[0].getBoundingBox().getMinZ());
        Location loc1 = new Location(world, regions[0].getBoundingBox().getMaxX(), regions[0].getBoundingBox().getMaxY(), regions[0].getBoundingBox().getMaxZ());

        PluginMain.storage.setValue(player, new FirstPositionItem(loc0));
        PluginMain.storage.setValue(player, new SecondPositionItem(loc1));
        PluginMain.storage.setValue(player, new RegionFrameInfoItem(RegionFrameInfoItem.RegionFrameTypes.RG_INFO));

        if (args.length >= 2)
            regions[0].printInfo(player, Integer.parseInt(args[1]));
        else
            regions[0].printInfo(player);

        return true;
    }

}
