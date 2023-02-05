package com.artingl.easyrg.misc.Regions;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.misc.Database.DatabaseProvider;
import com.artingl.easyrg.misc.Database.Field.Fields.RegionModel;
import com.artingl.easyrg.misc.Database.Field.ModelCondition;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.UUID;
import java.util.logging.Level;

public class RegionsRegistry {

    // TODO: Make a cache for regions.

    private DatabaseProvider databaseProvider;

    public RegionsRegistry() {
        setDatabaseProvider(PluginMain.instance.getDatabaseProvider());
    }

    public RegionClaimResult registerRegion(Region region) {
        try {
            try {
                // Check if the region overlaps with existing one

                BoundingBox box = region.getBoundingBox();
                RegionModel model = new RegionModel();
                Region[] regions = model.get(ModelCondition.make(
                            model.minX, ModelCondition.Cond.LESS, box.getMaxX() + 1, ModelCondition.Cond.AND,
                            model.maxX, ModelCondition.Cond.GREATER, box.getMinX(), ModelCondition.Cond.AND,
                            model.minY, ModelCondition.Cond.LESS, box.getMaxY() + 1, ModelCondition.Cond.AND,
                            model.maxY, ModelCondition.Cond.GREATER, box.getMinY(), ModelCondition.Cond.AND,
                            model.minZ, ModelCondition.Cond.LESS, box.getMaxZ() + 1, ModelCondition.Cond.AND,
                            model.maxZ, ModelCondition.Cond.GREATER, box.getMinZ()
                        )
                );

                if (regions.length != 0)
                    return RegionClaimResult.OVERLAPS_WITH_REGION;
            } catch (Exception ignore) {
                // This is fine, because we're checking that
                // the overlapping region does not exist.
            }

            if (getRegion(region.getName()) != null)
                return RegionClaimResult.NAME_EXISTS;

            boolean result = databaseProvider.setValue(new RegionModel(region));

            if (!result)
                return RegionClaimResult.OVERLAPS_WITH_REGION;

        } catch (Exception e) {
            PluginMain.logger.log(Level.SEVERE, "Unable to register region", e);
            return RegionClaimResult.INTERNAL_ERROR;
        }

        return RegionClaimResult.SUCCESS;
    }

    public RegionRemoveResult unregisterRegion(Player player, String name) {
        Region region = getRegion(name);

        if (region == null)
            return RegionRemoveResult.NOT_EXISTS;

        if (!region.hasAccess(player))
            return RegionRemoveResult.NO_PERMISSIONS;

        try {
            databaseProvider.delete(new RegionModel(region));
        } catch (Exception e) {
            PluginMain.logger.log(Level.SEVERE, "Unable to unregister region", e);
            return RegionRemoveResult.INTERNAL_ERROR;
        }


        return RegionRemoveResult.SUCCESS;
    }

    public RegionRemoveResult unregisterRegion(String name) {
        Region region = getRegion(name);

        if (region == null)
            return RegionRemoveResult.NOT_EXISTS;

        try {
            databaseProvider.delete(new RegionModel(region));
        } catch (Exception e) {
            PluginMain.logger.log(Level.SEVERE, "Unable to unregister region", e);
            return RegionRemoveResult.INTERNAL_ERROR;
        }


        return RegionRemoveResult.SUCCESS;
    }

    public Region getRegionAt(Location location) {
        try {
            BoundingBox box = new BoundingBox(
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    location.getX() - 1,
                    location.getY() - 1,
                    location.getZ() - 1
            );
            RegionModel model = new RegionModel();
            return model.get(ModelCondition.make(
                                model.minX, ModelCondition.Cond.LESS, box.getMaxX() + 1, ModelCondition.Cond.AND,
                                model.maxX, ModelCondition.Cond.GREATER, box.getMinX(), ModelCondition.Cond.AND,
                                model.minY, ModelCondition.Cond.LESS, box.getMaxY() + 1, ModelCondition.Cond.AND,
                                model.maxY, ModelCondition.Cond.GREATER, box.getMinY(), ModelCondition.Cond.AND,
                                model.minZ, ModelCondition.Cond.LESS, box.getMaxZ() + 1, ModelCondition.Cond.AND,
                                model.maxZ, ModelCondition.Cond.GREATER, box.getMinZ()
                        )
                )[0];
        } catch (Exception ignore) {
        }

        return null;
    }

    public Region getRegion(String name) {
        RegionModel model = new RegionModel();

        try {
            return model.get(ModelCondition.make(model.name, ModelCondition.Cond.EQUALS, name))[0];
        } catch (Exception ignored) {
        }

        return null;
    }

    public Region[] getRegionCollide(BoundingBox box) {
        try {
            RegionModel model = new RegionModel();
            return model.get(ModelCondition.make(
                            model.minX, ModelCondition.Cond.LESS, box.getMaxX() + 1, ModelCondition.Cond.AND,
                            model.maxX, ModelCondition.Cond.GREATER, box.getMinX(), ModelCondition.Cond.AND,
                            model.minY, ModelCondition.Cond.LESS, box.getMaxY() + 1, ModelCondition.Cond.AND,
                            model.maxY, ModelCondition.Cond.GREATER, box.getMinY(), ModelCondition.Cond.AND,
                            model.minZ, ModelCondition.Cond.LESS, box.getMaxZ() + 1, ModelCondition.Cond.AND,
                            model.maxZ, ModelCondition.Cond.GREATER, box.getMinZ()
                    )
            );
        } catch (Exception ignored) {
        }

        return null;
    }

    public void setDatabaseProvider(DatabaseProvider provider) {
        this.databaseProvider = provider;
    }

    public Region[] getRegionsByMember(UUID uuid) {
        try {
            RegionModel model = new RegionModel();
            return model.get(ModelCondition.make(
                            model.members, ModelCondition.Cond.CONTAINS, uuid
                    )
            );
        } catch (Exception ignored) {
        }

        return null;
    }

    public boolean updateRegion(Region region) {
        try {
            return new RegionModel(region).update();
        } catch (Exception ignored) {
        }

        return false;
    }
}
