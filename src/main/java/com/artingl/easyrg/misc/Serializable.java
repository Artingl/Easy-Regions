package com.artingl.easyrg.misc;

import com.google.gson.JsonElement;

public interface Serializable {

    String serialize();

    String serializeName();

    void deserialize(String key, JsonElement entry);
}
