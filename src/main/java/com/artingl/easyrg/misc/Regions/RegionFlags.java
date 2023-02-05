package com.artingl.easyrg.misc.Regions;

import com.artingl.easyrg.misc.Serializable;
import com.google.gson.JsonElement;
import com.sun.jdi.InvalidTypeException;

import java.util.Arrays;
import java.util.List;

public enum RegionFlags implements Serializable {

    MOB_SPAWN(
            "MOB_SPAWN",
            "mob-spawn",
            "Allow Mob spawn",
            Boolean.class, 0),
    NO_PVP(
            "NO_PVP",
            "no-pvp",
            "No PVP",
            Boolean.class, 1),
    NO_FALL_DAMAGE(
            "NO_FALL_DAMAGE",
            "no-fall-damage",
            "No Fall Damage",
            Boolean.class, 2),
    ALLOW_REDSTONE(
            "ALLOW_REDSTONE",
            "allow-redstone",
            "Allow Redstone",
            Boolean.class, 3);


    private final int value;
    private final String name;
    private final Class<?> type;
    private final String[] hints;
    private final String descriptionTag;
    private final String friendlyName;

    RegionFlags(String name, String descriptionTag, String friendlyName, Class<?> type, int value, String ...hints) {
        if (hints.length == 0 && type.equals(Boolean.class)) {
            hints = new String[]{"true", "false"};
        }

        this.value = value;
        this.name = name;
        this.type = type;
        this.hints = hints;
        this.descriptionTag = descriptionTag;
        this.friendlyName = friendlyName;
    }

    public static RegionFlags byName(String name) {
        for (RegionFlags flag: RegionFlags.values())
            if (flag.name.equals(name))
                return flag;

        return null;
    }

    @Override
    public String serialize() {
        return String.valueOf(value);
    }

    @Override
    public String serializeName() {
        return name;
    }

    @Override
    public void deserialize(String key, JsonElement entry) {
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public Object retrieveType(JsonElement value) {
        if (type.equals(Boolean.class)) {
            return value.getAsBoolean();
        }
        else if (type.equals(Integer.class)) {
            return value.getAsInt();
        }
        else if (type.equals(Float.class)) {
            return value.getAsFloat();
        }
        else if (type.equals(Double.class)) {
            return value.getAsDouble();
        }
        else if (type.equals(Long.class)) {
            return value.getAsLong();
        }
        else if (type.equals(String.class)) {
            return value.getAsString();
        }
        else if (type.equals(Short.class)) {
            return value.getAsShort();
        }
        else if (type.equals(Byte.class)) {
            return value.getAsByte();
        }

        return null;
    }

    public Object retrieveType(String value) throws InvalidTypeException {
        if (type.equals(Boolean.class)) {
            if (!value.equals("true") && !value.equals("false"))
                throw new InvalidTypeException("Invalid flag");

            return value.equals("true");
        }
        else if (type.equals(Integer.class)) {
            return Integer.parseInt(value);
        }
        else if (type.equals(Float.class)) {
            return Float.parseFloat(value);
        }
        else if (type.equals(Double.class)) {
            return Double.parseDouble(value);
        }
        else if (type.equals(Long.class)) {
            return Long.parseLong(value);
        }
        else if (type.equals(String.class)) {
            return value;
        }
        else if (type.equals(Short.class)) {
            return Short.parseShort(value);
        }
        else if (type.equals(Byte.class)) {
            return Byte.parseByte(value);
        }

        return null;
    }

    public void addHints(List<String> values) {
        values.addAll(Arrays.asList(hints));
    }

    public String getDescriptionTag() {
        return descriptionTag + "-flag-description";
    }
}
