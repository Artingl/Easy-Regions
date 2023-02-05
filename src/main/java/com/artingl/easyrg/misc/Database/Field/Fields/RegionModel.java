package com.artingl.easyrg.misc.Database.Field.Fields;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.misc.Database.DatabaseProvider;
import com.artingl.easyrg.misc.Database.Exception.ModelNotExist;
import com.artingl.easyrg.misc.Database.Field.DatabaseModel;
import com.artingl.easyrg.misc.Database.Field.ModelCondition;
import com.artingl.easyrg.misc.Database.Field.ModelField;
import com.artingl.easyrg.misc.Regions.Region;
import com.artingl.easyrg.misc.Regions.RegionFlags;
import com.artingl.easyrg.misc.Regions.RegionMember;
import com.artingl.easyrg.misc.Regions.RegionPosition;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.util.NumberConversions;

import java.sql.SQLException;
import java.util.*;

public class RegionModel implements DatabaseModel {

    public final ModelField<Integer> minX = new ModelField<>("minX", "INTEGER");
    public final ModelField<Integer> minY = new ModelField<>("minY", "INTEGER");
    public final ModelField<Integer> minZ = new ModelField<>("minZ", "INTEGER");
    public final ModelField<Integer> maxX = new ModelField<>("maxX", "INTEGER");
    public final ModelField<Integer> maxY = new ModelField<>("maxY", "INTEGER");
    public final ModelField<Integer> maxZ = new ModelField<>("maxZ", "INTEGER");

    public final ModelField<String> world = new ModelField<>("world", "TEXT");
    public final ModelField<String> name = new ModelField<>("name", "TEXT");
    public final ModelField<List<RegionMember>> members = new ModelField<>("members", "JSON");
    public final ModelField<Map<RegionFlags, Object>> flags = new ModelField<>("flags", "JSON");

    private final DatabaseProvider databaseProvider = PluginMain.instance.getDatabaseProvider();

    public RegionModel() {
    }

    public RegionModel(Region region) {
        minX.set(NumberConversions.floor(region.getBoundingBox().getMinX()));
        minY.set(NumberConversions.floor(region.getBoundingBox().getMinY()));
        minZ.set(NumberConversions.floor(region.getBoundingBox().getMinZ()));
        maxX.set(NumberConversions.floor(region.getBoundingBox().getMaxX()));
        maxY.set(NumberConversions.floor(region.getBoundingBox().getMaxY()));
        maxZ.set(NumberConversions.floor(region.getBoundingBox().getMaxZ()));
        world.set(region.getWorld().getName());
        name.set(region.getName());
        members.set(region.getMembers());
        flags.set(region.getFlags());
    }

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
    public Region[] get(ModelCondition... keys) throws NullPointerException, ModelNotExist, SQLException {
        synchronized (this) {
            List<Region> regions = new ArrayList<>();

            boolean result = databaseProvider.getValue((rs) -> {
                try {
                    do {
                        flags.initializeMap();
                        members.initializeList();

                        if (rs.isClosed())
                            break;

                        JsonArray membersArray = new JsonParser().parse(rs.getString("members")).getAsJsonArray();
                        JsonObject flagsObject = new JsonParser().parse(rs.getString("flags")).getAsJsonObject();

                        for (JsonElement object : membersArray) {
                            RegionMember member = new RegionMember();

                            for (Map.Entry<String, JsonElement> entry : object.getAsJsonObject().entrySet()) {
                                member.deserialize(entry.getKey(), entry.getValue());
                            }

                            members.get().add(member);
                        }

                        for (Map.Entry<String, JsonElement> entry : flagsObject.entrySet()) {
                            RegionFlags flag = RegionFlags.valueOf(entry.getKey());

                            flags.get().put(flag, flag.retrieveType(entry.getValue()));
                        }

                        regions.add(new Region(
                                new RegionPosition(rs.getInt("minX"), rs.getInt("minY"), rs.getInt("minZ")),
                                new RegionPosition(rs.getInt("maxX"), rs.getInt("maxY"), rs.getInt("maxZ")),
                                Bukkit.getWorld(rs.getString("world")),
                                members.get(),
                                flags.get(),
                                rs.getString("name")
                        ));

                        members.clear();
                        flags.clear();

                    } while (rs.next());

                    members.clear();
                    flags.clear();

                } catch (SQLException ignored) {
                }
            }, this, keys);

            if (!result)
                throw new ModelNotExist("Unable to find RegionModel in the database.");

            return regions.toArray(new Region[0]);
        }
    }

    @Override
    public boolean update() throws SQLException {
        return databaseProvider.update(this);
    }

    @Override
    public String identifierSQL() {
        return "name='" + name.get() + "'";
    }

}
