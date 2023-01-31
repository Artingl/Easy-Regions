package com.artingl.easyrg.Storage;

import com.artingl.easyrg.PluginMain;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

public class RegionFrameInfoItem extends StorageItem {

    private Team team;
    private Scoreboard scoreboard;
    private RegionFrameTypes type;
    private int liveTime;

    private boolean frameSpawned;
    private boolean isRainbowEnabled;
    private int rainbowSpeed;
    private int colorValue;
    private boolean isInitialized;

    private ArrayList<Entity> entities;

    public RegionFrameInfoItem(RegionFrameTypes type) {
        this.type = type;
        this.entities = new ArrayList<>();
    }

    public void init(Player player) {
        if (!isInitialized) {
            this.isRainbowEnabled = PluginMain.instance.getConfig()
                    .getConfigurationSection("region-selection")
                    .getConfigurationSection("visuals")
                    .getConfigurationSection("rainbow")
                    .getBoolean("enable");

            this.rainbowSpeed = PluginMain.instance.getConfig()
                    .getConfigurationSection("region-selection")
                    .getConfigurationSection("visuals")
                    .getConfigurationSection("rainbow")
                    .getInt("speed");

            this.rainbowSpeed = this.rainbowSpeed > 16 ? 16 : (Math.max(this.rainbowSpeed, 1));

            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            this.team = this.scoreboard.registerNewTeam("GLOWING");
            this.liveTime = 200; // in ticks

            this.isInitialized = true;
        }
    }

    @Override
    public void destroy() {
        this.destroyEntities();

        if (this.team != null)
            this.team.unregister();
    }

    public void destroyEntities() {
        for (Entity entity: entities) {
            if (entity instanceof LivingEntity)
                ((LivingEntity) entity).setLeashHolder(null);

            entity.leaveVehicle();
            entity.remove();

            this.team.removeEntry(entity.getUniqueId().toString());
        }

        this.entities.clear();
    }

    public Color getParticleColor() {
        if (!isRainbowEnabled)
            return Color.fromRGB(255, 127, 255);

        colorValue += rainbowSpeed;
        if (colorValue >= 256)
            colorValue = 0;

        java.awt.Color color = java.awt.Color.getHSBColor(colorValue/255f, 1, 1);

        return Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }

    public RegionFrameTypes getType() {
        return type;
    }

    public int getLiveTime(int ticksElapsed) {
        this.liveTime -= ticksElapsed;
        return liveTime + ticksElapsed;
    }

    public Team getTeam() {
        return team;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public boolean isFrameSpawned() {
        return frameSpawned;
    }

    public void setFrameSpawned(boolean frameSpawned) {
        this.frameSpawned = frameSpawned;
    }

    public void addEntity(Entity entity) {
        this.entities.add(entity);
        this.team.addEntry(entity.getUniqueId().toString());
    }

    public enum RegionFrameTypes {
        FRAME, RG_INFO

    }
}
