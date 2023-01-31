package com.artingl.easyrg.misc.Regions;

import com.artingl.easyrg.misc.Serializable;

public enum RegionFlags implements Serializable {

    MOB_SPAWN("MOB_SPAWN", 0),
    DISABLE_PVP("DISABLE_PVP", 1),
    FALL_DISABLE_DAMAGE("FALL_DISABLE_DAMAGE", 2),
    ;


    private final int value;
    private final String name;

    RegionFlags(String name, int value) {
        this.value = value;
        this.name = name;
    }

    @Override
    public String serialize() {
        return String.valueOf(value);
    }

    @Override
    public String serializeName() {
        return name;
    }
}
