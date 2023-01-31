package com.artingl.easyrg.misc.Regions;

import com.artingl.easyrg.Permissions.Permissions;
import com.artingl.easyrg.PluginMain;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.HashMap;
import java.util.Map;

public class Region {

    private final BoundingBox boundingBox;
    private final World world;
    private final Player initialOwner;
    private final String name;
    private Map<RegionFlags, Object> flags;

    public Region(RegionPosition start, RegionPosition end, World world, Player initialOwner, String name) {
        this.name = name;
        this.flags = new HashMap<>();
        this.world = world;
        this.initialOwner = initialOwner;
        this.boundingBox = new BoundingBox(
                start.getX(), start.getY(), start.getZ(),
                end.getX(), end.getY(), end.getZ()
        );

        this.flags.put(RegionFlags.FALL_DISABLE_DAMAGE, false);
        this.flags.put(RegionFlags.MOB_SPAWN, true);
        this.flags.put(RegionFlags.DISABLE_PVP, false);
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public Map.Entry<Boolean, RegionClaimResult> claim() {
        return PluginMain.instance.getRegionsRegistry().registerRegion(this);
    }

    public boolean remove() {
        return PluginMain.instance.getRegionsRegistry().unregisterRegion(this);
    }

    public World getWorld() {
        return world;
    }

    public Player getInitialOwner() {
        return initialOwner;
    }

    public Object getFlag(RegionFlags flag) {
        return flags.get(flag);
    }

    public void setFlag(RegionFlags flag, Object value) {
        flags.put(flag, value);
    }

    public boolean hasAccess(Player player) {
        if (Permissions.hasPermission(player, Permissions.REGIONS_FULL_ACCESS))
            return true;

        if (initialOwner == null)
            return false;

        return player.getUniqueId().equals(initialOwner.getUniqueId());
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Region{initialOwner=" + initialOwner.getUniqueId() + ", world=" + world.getName() + "}";
    }

    @Override
    public int hashCode() {
        return boundingBox.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Region))
            return false;

        return ((Region) obj).boundingBox.equals(boundingBox);
    }
}
