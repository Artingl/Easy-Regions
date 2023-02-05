package com.artingl.easyrg.misc.Utilities;

import com.artingl.easyrg.PluginMain;
import org.bukkit.Sound;

public class ConfigUtils {

    private static Sound[] selectionSound = new Sound[2];

    public static void load() {
        selectionSound[0] = null;
        selectionSound[1] = null;

        Object soundKey = PluginMain.instance.getConfig()
                .getConfigurationSection("region-selection")
                .getConfigurationSection("sounds")
                .getString("region-selection1");
        if (soundKey != null) {
            int i = 0;

            for (Sound sound : Sound.values()) {
                if (sound.name().equals(soundKey)) {
                    selectionSound[i++] = sound;
                    soundKey = PluginMain.instance.getConfig()
                            .getConfigurationSection("region-selection")
                            .getConfigurationSection("sounds")
                            .getString("region-selection2");

                    if (soundKey == null)
                        break;
                }

                if (i > 1)
                    break;
            }
        }

    }

    public static Sound[] getSelectionSound() {
        return selectionSound;
    }
}
