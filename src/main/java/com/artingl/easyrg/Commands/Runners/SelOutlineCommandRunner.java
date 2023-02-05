package com.artingl.easyrg.Commands.Runners;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.Storage.FirstPositionItem;
import com.artingl.easyrg.Storage.SecondPositionItem;
import com.artingl.easyrg.misc.Regions.Region;
import com.artingl.easyrg.misc.Utilities.ChatUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.BoundingBox;

public class SelOutlineCommandRunner implements CommandRunner {

    @Override
    public boolean run(Player player, World world, String[] args) {
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

        Region[] regions = PluginMain.instance.getRegionsRegistry().getRegionCollide(
                new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ)
        );

        if (regions != null) {
            for (Region region: regions) {
                if (!region.hasAccess(player)) {
                    ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("sel-outline-overlaps"));
                    PluginMain.instance.playCommandErrorSound(player);
                    return false;
                }
            }
        }

        for (int x = minX; x < maxX + 1; x++)
            for (int z = minZ; z < maxZ + 1; z++) {
                if ((x == minX || x == maxX)
                        || (z == minZ || z == maxZ)) {

                    Block block = null;

                    for (int y = minY; y < maxY + 1; y++) {
                        if (!world.getBlockAt(x, y, z).getType().isTransparent()) {
                            block = world.getBlockAt(x, y, z);
                        } else {
                            if (block == null)
                                block = world.getBlockAt(x, y - 1, z);

                            break;
                        }
                    }

                    if (block != null) {
                        block.setMetadata("easyrg-region-outline", new FixedMetadataValue(PluginMain.instance, true));
                        block.setType(Material.RED_WOOL);
                    }
                }
            }

        return true;
    }

}
