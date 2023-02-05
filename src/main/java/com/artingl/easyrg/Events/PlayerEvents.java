package com.artingl.easyrg.Events;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.Storage.RegionFrameInfoItem;
import com.artingl.easyrg.Storage.FirstPositionItem;
import com.artingl.easyrg.Storage.SecondPositionItem;
import com.artingl.easyrg.misc.Utilities.ChatUtils;
import com.artingl.easyrg.misc.Utilities.ConfigUtils;
import com.artingl.easyrg.misc.Utilities.MathUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.text.MessageFormat;
import java.util.Objects;

public class PlayerEvents implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerItemUseEvent(PlayerInteractEvent event) {
        String pos1 = PluginMain.instance.getLanguage().getString("pos1-select");
        String pos2 = PluginMain.instance.getLanguage().getString("pos2-select");

        ConfigurationSection section = PluginMain.instance.getConfig()
                .getConfigurationSection("region-selection");

        boolean enableSounds = PluginMain.instance.isSoundEnabled();
        boolean enableOnSneaking = section.getBoolean("enable-on-sneaking");
        boolean enable = section.getBoolean("enable");

        int maxSize = section.getInt("max-selection-size");

        if (!enable)
            return;

        if (!enableOnSneaking && event.getPlayer().isSneaking())
            return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            Player player = event.getPlayer();
            Location location = event.getClickedBlock().getLocation();

            if (item != null) {
                if (item.getType().equals(Material.WOODEN_AXE)) {
                    FirstPositionItem lastPos1 = (FirstPositionItem) PluginMain.storage.getValue(player, FirstPositionItem.class);
                    SecondPositionItem lastPos2 = (SecondPositionItem) PluginMain.storage.getValue(player, SecondPositionItem.class);

                    switch (event.getAction()) {
                        case LEFT_CLICK_BLOCK: {
                            if (lastPos2 != null)
                                if (MathUtils.calculateLength(lastPos2.getLocation(), location) > maxSize) {
                                    ChatUtils.sendDecoratedMessage(player,
                                            MessageFormat.format(
                                                    Objects.requireNonNull(PluginMain.instance.getLanguage().getString("selection-too-big")),
                                                    maxSize
                                            )
                                    );
                                    event.setCancelled(true);
                                    return;
                                }

                            // first position
                            player.sendMessage(ChatUtils.decorateMessage(
                                    MessageFormat.format(pos1, location.getBlockX(), location.getBlockY(), location.getBlockZ())));
                            PluginMain.storage.setValue(player, new FirstPositionItem(location));
                            break;
                        }

                        case RIGHT_CLICK_BLOCK: {
                            if (lastPos1 != null)
                                if (MathUtils.calculateLength(lastPos1.getLocation(), location) > maxSize) {
                                    ChatUtils.sendDecoratedMessage(player,
                                            MessageFormat.format(
                                                    Objects.requireNonNull(PluginMain.instance.getLanguage().getString("selection-too-big")),
                                                    maxSize
                                            )
                                    );
                                    event.setCancelled(true);
                                    return;
                                }

                            // second position
                            player.sendMessage(ChatUtils.decorateMessage(
                                    MessageFormat.format(pos2, location.getBlockX(), location.getBlockY(), location.getBlockZ())));
                            PluginMain.storage.setValue(player, new SecondPositionItem(location));
                        }
                    }

                    PluginMain.storage.setValue(player, new RegionFrameInfoItem(RegionFrameInfoItem.RegionFrameTypes.FRAME));

                    if (enableSounds)
                        player.playSound(player.getLocation(),
                                ConfigUtils.getSelectionSound()[event.getAction() == Action.LEFT_CLICK_BLOCK ? 0 : 1], 1, 1);

                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerAttackEvent(EntityDamageEvent event) {
        if (event.getEntity().getScoreboardTags().contains("EASYRG_SELECTION_AREA_ARMOURSTAND")) {
            event.setCancelled(true);
        }
    }

}
