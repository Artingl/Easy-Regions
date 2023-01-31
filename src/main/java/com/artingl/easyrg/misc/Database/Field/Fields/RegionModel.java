package com.artingl.easyrg.misc.Database.Field.Fields;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.misc.Database.DatabaseProvider;
import com.artingl.easyrg.misc.Database.Exception.ModelNotExist;
import com.artingl.easyrg.misc.Database.Field.DatabaseModel;
import com.artingl.easyrg.misc.Database.Field.ModelCondition;
import com.artingl.easyrg.misc.Database.Field.ModelField;
import com.artingl.easyrg.misc.Regions.Region;
import com.artingl.easyrg.misc.Regions.RegionFlags;
import com.artingl.easyrg.misc.Regions.RegionPosition;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.util.BoundingBox;

import java.sql.SQLException;
import java.util.*;

public class RegionModel implements DatabaseModel {

    public final ModelField<Double> minX = new ModelField<>("minX", "DOUBLE");
    public final ModelField<Double> minY = new ModelField<>("minY", "DOUBLE");
    public final ModelField<Double> minZ = new ModelField<>("minZ", "DOUBLE");
    public final ModelField<Double> maxX = new ModelField<>("maxX", "DOUBLE");
    public final ModelField<Double> maxY = new ModelField<>("maxY", "DOUBLE");
    public final ModelField<Double> maxZ = new ModelField<>("maxZ", "DOUBLE");

    public final ModelField<String> world = new ModelField<>("world", "TEXT");
    public final ModelField<String> name = new ModelField<>("name", "TEXT");
    public final ModelField<List<UUID>> members = new ModelField<>("members", "JSON");
    public final ModelField<Map<RegionFlags, Object>> flags = new ModelField<>("flags", "JSON");

    private final DatabaseProvider databaseProvider = PluginMain.instance.getDatabaseProvider();

    @Override
    public List<ModelField<?>> fields() {
        List<ModelField<?>> list = new ArrayList<>();

        list.add(minX);
        list.add(minY);
        list.add(minZ);
        list.add(maxX);
        list.add(maxY);
        list.add(maxZ);
        list.add(world);
        list.add(name);
        list.add(members);
        list.add(flags);

        return list;
    }

    @Override
    public List<String> names() {
        List<String> list = new ArrayList<>();

        list.add(minX.name());
        list.add(minY.name());
        list.add(minZ.name());
        list.add(maxX.name());
        list.add(maxY.name());
        list.add(maxZ.name());
        list.add(world.name());
        list.add(name.name());
        list.add(members.name());
        list.add(flags.name());

        return list;
    }

    @Override
    public String name() {
        return "RegionModel";
    }

    @Override
    public Region get(ModelCondition... keys) throws ModelNotExist, SQLException {
        synchronized (this) {
            boolean result = databaseProvider.getValue((set) -> {
                try {
                    minX.set(set.getDouble("minX"));
                    minY.set(set.getDouble("minY"));
                    minZ.set(set.getDouble("minZ"));
                    maxX.set(set.getDouble("maxX"));
                    maxY.set(set.getDouble("maxY"));
                    maxZ.set(set.getDouble("maxZ"));

                    world.set(set.getString("world"));
                    name.set(set.getString("name"));

                    flags.initializeMap();
                    members.initializeList();

                    JsonArray membersArray = new JsonParser().parse(set.getString("members")).getAsJsonArray();
                    JsonObject flagsObject = new JsonParser().parse(set.getString("flags")).getAsJsonObject();

                    for (JsonElement object : membersArray)
                        members.get().add(UUID.fromString(object.getAsString()));

                    for (Map.Entry<String, JsonElement> entry : flagsObject.entrySet()) {
                        flags.get().put(RegionFlags.valueOf(entry.getKey()), entry.getValue());
                    }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, this, keys);

            if (!result)
                throw new ModelNotExist("Unable to find RegionModel in the database.");

            Region region = new Region(
                    new RegionPosition(minX.get(), minY.get(), minZ.get()),
                    new RegionPosition(maxX.get(), maxY.get(), maxZ.get()),
                    Bukkit.getWorld(world.get()),
                    members.get(),
                    flags.get(),
                    name.get()
            );

            members.clear();
            flags.clear();

            return region;
        }
    }


}
