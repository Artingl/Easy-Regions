package com.artingl.easyrg.misc.Database.Field;

import com.artingl.easyrg.misc.Serializable;
import org.bukkit.util.BoundingBox;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ModelField <T> {

    private T value;
    private String settings;
    private final String name;

    public ModelField(T value, String name, String settings) {
        this.value = value;
        this.settings = settings;
        this.name = name;
    }

    public ModelField(String name, String settings) {
        this.settings = settings;
        this.name = name;
    }

    public String serialize() {
        return serialize(value, false);
    }

    private String serialize(Object value, boolean quotes) {
        if (value instanceof String) {
            if (quotes)
                return '\"' + (String) value + '\"';

            return (String) value;
        }
        else if (value instanceof Integer
                || value instanceof Boolean
                || value instanceof Float
                || value instanceof Double) {
            return String.valueOf(value);
        }
        else if (value instanceof UUID) {
            if (quotes)
                return '\"' + value.toString() + '\"';

            return value.toString();
        }
        else if (value instanceof BoundingBox) {
            // fixme: bad approach
            BoundingBox box = ((BoundingBox) value);

            return "{\"minX\": " + box.getMinX() + ", \"minY\": " + box.getMinY() + ", \"minZ\": " + box.getMinZ() + ", \"maxX\": " + box.getMaxX() + ", \"maxY\": " + box.getMaxY() + ", \"maxZ\": " + box.getMaxZ() + "}";
        }
        else if (value instanceof Serializable) {
            return ((Serializable) value).serialize();
        }
        else if (value instanceof Map) {
            StringBuilder json = new StringBuilder();
            Map<Object, Object> map = (Map<Object, Object>) value;

            for (Map.Entry<Object, Object> entry: map.entrySet()) {
                if (entry.getKey() instanceof Serializable) {
                    json.append('"').append(((Serializable) entry.getKey()).serializeName()).append('"');
                }
                else {
                    json.append('"').append(serialize(entry.getKey(), true)).append('"');
                }

                json.append(": ").append(serialize(entry.getValue(), true)).append(',');
            }

            return '{' + json.substring(0, json.length()-1) + '}';
        }
        else if (value instanceof List) {
            StringBuilder json = new StringBuilder();
            List<Object> list = (List<Object>) value;

            for (Object obj: list) {
                json.append(serialize(obj, true)).append(',');
            }

            return '[' + json.substring(0, json.length()-1) + ']';
        }

        return null;
    }

    public String name() {
        return name;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    public String fieldSettings() {
        return settings;
    }
}
