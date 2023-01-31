package com.artingl.easyrg.Storage;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public abstract class StorageItem {

    public static UUID getUniqueId(StorageItem item) {
        return UUID.nameUUIDFromBytes(item.getClass().getName().getBytes(StandardCharsets.UTF_8));
    }

    public static UUID getUniqueId(Class<? extends StorageItem> item) {
        return UUID.nameUUIDFromBytes(item.getName().getBytes(StandardCharsets.UTF_8));
    }

    public abstract void destroy();

}
