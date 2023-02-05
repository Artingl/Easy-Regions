package com.artingl.easyrg.misc.Regions;

import com.artingl.easyrg.misc.Serializable;
import com.google.gson.JsonElement;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class RegionMember implements Serializable {

    private UUID uuid;
    private Permissions permissions;
    private long timestamp;

    public static RegionMember mask(UUID uuid) {
        return new RegionMember(uuid, Permissions.MEMBER, System.currentTimeMillis());
    }

    public static RegionMember mask(UUID uuid, Permissions permissions) {
        return new RegionMember(uuid, permissions, System.currentTimeMillis());
    }

    public RegionMember(UUID uuid, Permissions permissions, long timestamp) {
        this.uuid = uuid;
        this.permissions = permissions;
        this.timestamp = timestamp;
    }

    public RegionMember() {
    }

    public UUID UUID() {
        return uuid;
    }

    public Permissions permissions() {
        return permissions;
    }

    public long timestampSince() {
        return timestamp;
    }

    public OfflinePlayer offlinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    @Override
    public String toString() {
        return "RegionMember{uuid=" + uuid + ", permissions=" + permissions + ", timestamp=" + timestamp + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RegionMember))
            return false;

        return ((RegionMember) obj).uuid.equals(uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String serialize() {
        return "{\"uuid\": \"" + uuid.toString() + "\", \"permissions\": \"" + permissions + "\", \"timestamp\": " + timestamp + "}";
    }

    @Override
    public String serializeName() {
        return null;
    }

    @Override
    public void deserialize(String key, JsonElement entry) {
        switch (key) {
            case "uuid": {
                uuid = UUID.fromString(entry.getAsString());
                break;
            }

            case "permissions": {
                permissions = Permissions.valueOf(entry.getAsString());
                break;
            }

            case "timestamp": {
                timestamp = entry.getAsLong();
                break;
            }
        }
    }

    public enum Permissions {
        MEMBER,
        MODERATOR,
        OWNER

    }

}
