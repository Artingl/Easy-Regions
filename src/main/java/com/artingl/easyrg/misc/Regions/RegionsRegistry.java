package com.artingl.easyrg.misc.Regions;

import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionsRegistry {

    private Map<String, Region> regions;

    public RegionsRegistry() {
        this.regions = new HashMap<>();
    }

    public Map.Entry<Boolean, RegionClaimResult> registerRegion(Region region) {
        for (Region regionToFind: regions.values()) {
            if (regionToFind.getBoundingBox().overlaps(region.getBoundingBox()))
                return Map.entry(false, RegionClaimResult.OVERLAPS_WITH_REGION);
            if (regionToFind.getName().equals(region.getName()))
                return Map.entry(false, RegionClaimResult.NAME_EXISTS);
        }

        regions.put(region.getName(), region);

        return Map.entry(true, RegionClaimResult.SUCCESS);
    }

    public boolean unregisterRegion(Region region) {
        if (regions.containsKey(region.getName())) {
            regions.remove(region.getName());
            return true;
        }

        return false;
    }

    public Region getRegionAt(Location location) {
        BoundingBox boundingBox = new BoundingBox(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getX() + 1,
                location.getY() + 1,
                location.getZ() + 1
        );

        for (Region regionToFind: regions.values()) {
            if (regionToFind.getBoundingBox().overlaps(boundingBox))
                return regionToFind;
        }

        return null;
    }

    public Region getRegion(String name) {
        return regions.get(name);
    }

    public List<Region> getRegionCollide(BoundingBox boundingBox) {
        List<Region> regionList = new ArrayList<>();

        for (Region regionToFind: regions.values()) {
            if (regionToFind.getBoundingBox().overlaps(boundingBox))
                regionList.add(regionToFind);
        }

        return regionList;
    }
}
