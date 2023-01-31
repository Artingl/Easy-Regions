package com.artingl.easyrg.Storage;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Storage {

    private final Map<UUID, Map<UUID, StorageItem>> storage;

    public Storage() {
        this.storage = new ConcurrentHashMap<>();
    }

    public void clear() {
        for (UUID uuid: storage.keySet())
            this.remove(uuid);

        this.storage.clear();
    }

    public StorageItem getValue(Player player, Class<? extends StorageItem> clazz) {
        if (!storage.containsKey(player.getUniqueId()))
            return null;

        return storage.get(player.getUniqueId()).get(StorageItem.getUniqueId(clazz));
    }

    public void setValue(Player player, StorageItem item) {
        Map<UUID, StorageItem> values = storage.get(player.getUniqueId());

        if (values == null) {
            values = new ConcurrentHashMap<>();
        }

        if (values.containsKey(StorageItem.getUniqueId(item)))
            values.get(StorageItem.getUniqueId(item)).destroy();

        values.put(StorageItem.getUniqueId(item), item);

        storage.put(player.getUniqueId(), values);
    }

    public Map<UUID, Map<UUID, StorageItem>> getValues() {
        return this.storage;
    }

    public void remove(UUID uuid) {
        if (!this.storage.containsKey(uuid))
            return;

        for (Map.Entry<UUID, StorageItem> entry: this.storage.get(uuid).entrySet()) {
            entry.getValue().destroy();
        }

        this.storage.remove(uuid);
    }
}
