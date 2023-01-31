package com.artingl.easyrg.misc.Regions;

import com.artingl.easyrg.Permissions.Permissions;
import com.artingl.easyrg.PluginMain;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class Region {

    private final BoundingBox boundingBox;
    private final World world;
    private final List<UUID> members;
    private final String name;
    private Map<RegionFlags, Object> flags;

    public Region(RegionPosition start, RegionPosition end, World world, List<UUID> members, String name) {
        this(start, end, world, members, new HashMap<>(), name);

        this.flags.put(RegionFlags.FALL_DISABLE_DAMAGE, false);
        this.flags.put(RegionFlags.MOB_SPAWN, true);
        this.flags.put(RegionFlags.DISABLE_PVP, false);
    }

    public Region(RegionPosition start, RegionPosition end, World world, List<UUID> members, Map<RegionFlags, Object> flags, String name) {
        this.name = name;
        this.flags = Map.copyOf(flags);
        this.world = world;
        this.members = List.copyOf(members);
        this.boundingBox = new BoundingBox(
                start.getX(), start.getY(), start.getZ(),
                end.getX(), end.getY(), end.getZ()
        );
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

    public List<UUID> getMembers() {
        return members;
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

        if (members == null)
            return false;

        return members.contains(player.getUniqueId());
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Region{members=" + members + ", flags=" + flags + ", name=" + name + ", world=" + world.getName() + "}";
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
