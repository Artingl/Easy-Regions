package com.artingl.easyrg.misc.Database.Field.Fields;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.misc.Database.DatabaseResult;
import com.artingl.easyrg.misc.Database.Exception.ModelNotExist;
import com.artingl.easyrg.misc.Database.Field.DatabaseModel;
import com.artingl.easyrg.misc.Database.Field.ModelCondition;
import com.artingl.easyrg.misc.Database.Field.ModelField;
import com.artingl.easyrg.misc.Regions.Region;
import com.artingl.easyrg.misc.Regions.RegionFlags;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class RegionModel implements DatabaseModel {

    public final ModelField<BoundingBox> boundingBox = new ModelField<>("boundingBox", "JSON");
    public final ModelField<String> world = new ModelField<>("world", "TEXT");
    public final ModelField<List<UUID>> owners  = new ModelField<>("owners", "JSON");
    public final ModelField<Map<RegionFlags, Object>> flags = new ModelField<>("flags", "JSON");

    @Override
    public List<ModelField> fields() {
        List<ModelField> list = new ArrayList<>();

        list.add(boundingBox);
        list.add(world);
        list.add(owners);
        list.add(flags);

        return list;
    }

    @Override
    public List<String> names() {
        List<String> list = new ArrayList<>();

        list.add(boundingBox.name());
        list.add(world.name());
        list.add(owners.name());
        list.add(flags.name());

        return list;
    }

    @Override
    public String name() {
        return "RegionModel";
    }

    @Override
    public Region load(ModelCondition... keys) throws ModelNotExist {
        Map.Entry<DatabaseResult, ModelField[]> result = PluginMain.instance.getDatabaseProvider().getValue(this, keys);
        ModelField[] fields = result.getValue();

        if (result.getKey().equals(DatabaseResult.NOT_FOUND))
            throw new ModelNotExist("Unable to find RegionModel in the database.");

        System.out.println(Arrays.toString(fields));

        return null;
//        new Region(
//                new RegionPosition()
//        );

//        boundingBox.set((BoundingBox) fields[1].get());
//        world.set(Bukkit.getWorld(fields[2].get()));
//        owners.set((Integer) fields[3].get());
//        flags.set((Integer) fields[4].get());
    }


}
