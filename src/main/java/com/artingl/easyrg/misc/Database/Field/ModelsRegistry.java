package com.artingl.easyrg.misc.Database.Field;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.misc.Database.Field.Fields.RegionModel;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ModelsRegistry {
    public static final ModelsList modelsList = new ModelsList();

    public static void init() {
        modelsList.clear();

        modelsList.register(
                RegionModel.class
        );
    }


    public static class ModelsList {

        private Map<Integer, Class<? extends DatabaseModel>> map;

        public ModelsList() {
            map = new HashMap<>();
        }

        public void clear() {
            map.clear();
        }

        public void register(Class<? extends DatabaseModel> ...objs) {
            for (Class<? extends DatabaseModel> c: objs)
                map.put(c.getName().hashCode(), c);
        }

        public void register(Class<? extends DatabaseModel> c) {
            map.put(c.getName().hashCode(), c);
        }

        public Map<Integer, Class<? extends DatabaseModel>> map() {
            return map;
        }

        public DatabaseModel get(Class<? extends DatabaseModel> clazz) {
            try {
                Class<? extends DatabaseModel> val = map.get(clazz.getName().hashCode());

                if (val == null)
                    return null;

                return val.newInstance();
            } catch (Exception e) {
                PluginMain.logger.log(Level.SEVERE, "Unable to initialize model class!", e);
                System.exit(1);
            }

            return null;
        }
    }
}
