package com.artingl.easyrg.Events;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.Storage.RegionFrameInfoItem;
import com.artingl.easyrg.Storage.FirstPositionItem;
import com.artingl.easyrg.Storage.SecondPositionItem;
import com.artingl.easyrg.Storage.StorageItem;
import com.artingl.easyrg.misc.Utilities.MathUtils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;

public class RegionsTickEvent extends BukkitRunnable {

    @Override
    public void run() {
        Map<UUID, Map<UUID, StorageItem>> values = PluginMain.storage.getValues();

        ConfigurationSection glowing = PluginMain.instance.getConfig()
                .getConfigurationSection("region-selection")
                .getConfigurationSection("visuals")
                .getConfigurationSection("glowing");

        boolean enableSelection = PluginMain.instance.getConfig()
                .getConfigurationSection("region-selection")
                .getConfigurationSection("visuals")
                .getBoolean("enable");

        boolean enableGlowing = glowing.getBoolean("enable");
        boolean observerPlayer = glowing.getBoolean("observer-player");
        String glowingInsideColor = glowing.get("player-inside-color").toString();
        String glowingOutsideColor = glowing.get("player-outside-color").toString();

        if (!enableSelection) {
            PluginMain.storage.clear();
            return;
        }

        for (Map.Entry<UUID, Map<UUID, StorageItem>> entry: values.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());

            if (player != null) {
                FirstPositionItem pos1 = (FirstPositionItem) PluginMain.storage.getValue(player, FirstPositionItem.class);
                SecondPositionItem pos2 = (SecondPositionItem) PluginMain.storage.getValue(player, SecondPositionItem.class);
                RegionFrameInfoItem regionValues = (RegionFrameInfoItem) PluginMain.storage.getValue(player, RegionFrameInfoItem.class);

                if (pos1 != null && pos2 != null && regionValues != null) {
                    this.drawRegionFrame(
                            player,
                            enableGlowing,
                            observerPlayer,
                            glowingInsideColor,
                            glowingOutsideColor,
                            pos1,
                            pos2,
                            regionValues
                    );
                }
            }
            else {
                PluginMain.storage.remove(entry.getKey());
            }
        }
    }

    private void drawRegionFrame(
            Player player,
            boolean enableGlowing,
            boolean observerPlayer,
            String glowingInsideColor,
            String glowingOutsideColor,
            FirstPositionItem pos1,
            SecondPositionItem pos2,
            RegionFrameInfoItem frameInfo) {

        double minX = Math.min(pos1.getLocation().getBlockX(), pos2.getLocation().getBlockX());
        double minY = Math.min(pos1.getLocation().getBlockY(), pos2.getLocation().getBlockY());
        double minZ = Math.min(pos1.getLocation().getBlockZ(), pos2.getLocation().getBlockZ());
        double maxX = Math.max(pos1.getLocation().getBlockX(), pos2.getLocation().getBlockX()) + 1;
        double maxY = Math.max(pos1.getLocation().getBlockY(), pos2.getLocation().getBlockY()) + 1;
        double maxZ = Math.max(pos1.getLocation().getBlockZ(), pos2.getLocation().getBlockZ()) + 1;

        frameInfo.init(player);

        Color color = frameInfo.getParticleColor();

        BoundingBox boundingBox = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1);

        Team team = frameInfo.getTeam();
        Scoreboard scoreboard = frameInfo.getScoreboard();

        player.setScoreboard(scoreboard);

        if (!team.hasEntry(player.getUniqueId().toString()))
            team.addEntry(player.getUniqueId().toString());

        if (frameInfo.getType().equals(RegionFrameInfoItem.RegionFrameTypes.FRAME)) {
            if (observerPlayer)
                team.setColor(boundingBox.overlaps(player.getBoundingBox()) ?
                        ChatColor.valueOf(glowingInsideColor) : ChatColor.valueOf(glowingOutsideColor));
            else team.setColor(ChatColor.WHITE);
        }
        else if (!boundingBox.overlaps(player.getBoundingBox()) || frameInfo.getLiveTime(10) < 0) {
            PluginMain.storage.remove(player.getUniqueId());
            return;
        }

        if (!frameInfo.isFrameSpawned()) {
            frameInfo.destroyEntities();

            int rot = 0;

            for (double x = minX; x < maxX + 1; x++)
                for (double y = minY; y < maxY + 1; y++)
                    for (double z = minZ; z < maxZ + 1; z++) {
                        if ((x == minX || x == maxX)
                                && (z == minZ || z == maxZ)) {
                            rot = rot == 0 ? 90 : 0;
                        }

                        if (((x == minX || x == maxX)
                                || (z == minZ || z == maxZ)) && (y == minY || y == maxY)) {
                            World world = player.getWorld();

                            Location loc = new Location(world, x, y - 1.82f, z, rot, 0);
                            ArmorStand entity = (ArmorStand) world.spawnEntity(loc, EntityType.ARMOR_STAND);

                            if (enableGlowing)
                                entity.setGlowing(true);

                            entity.setCollidable(false);
                            entity.setVisible(false);
                            entity.setGravity(false);
                            entity.setMarker(true);
                            entity.setInvulnerable(true);
                            entity.addScoreboardTag("EASYRG_SELECTION_" + player.getUniqueId());
                            entity.getEquipment().setHelmet(new ItemStack(Material.STONE_BUTTON));

                            frameInfo.addEntity(entity);
                        }

                    }

            frameInfo.setFrameSpawned(true);
        }

        for (double x = minX; x < maxX + 1; x++)
            for (double y = minY; y < maxY + 1; y++)
                for (double z = minZ; z < maxZ + 1; z++) {
                    if ((x == minX || x == maxX)
                            && (z == minZ || z == maxZ)
                            && (y == minY || y == maxY)
                            && MathUtils.calculateLength(x, y, z, player.getLocation()) < 16 * 3) {
                        player.spawnParticle(Particle.REDSTONE, x, y, z, 5, dustOptions);
                    }
                }

    }

}
