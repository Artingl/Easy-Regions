package com.artingl.easyrg.Events;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.misc.Regions.Region;
import com.artingl.easyrg.misc.Regions.RegionFlags;
import com.artingl.easyrg.misc.Utilities.ChatUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

public class RegionEvents implements Listener {

    private boolean handleEntityEvent(Player player, Entity entity, Location location, boolean showMessage) {
        Region region = PluginMain.instance.getRegionsRegistry().getRegionAt(location);

        if (region != null) {
            if (region.hasAccess(player)) {
                if (showMessage)
                    ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().get("region-restriction").toString());
                return false;
            }
        }

        return true;
    }

    private boolean handleRegionEvent(Player player, Location location, boolean showMessage) {
        Region region = PluginMain.instance.getRegionsRegistry().getRegionAt(location);

        if (region != null) {
            if (!region.hasAccess(player)) {
                if (showMessage)
                    ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().get("region-restriction").toString());
                return false;
            }
        }

        return true;
    }

    private boolean handleDamageEvent(Player player, Entity dest, Location location, RegionFlags flag, boolean showMessage) {
        Region region = PluginMain.instance.getRegionsRegistry().getRegionAt(location);

        if (region != null) {
            Object flagObj = region.getFlag(flag);
            if (flagObj instanceof Boolean) {
                if ((boolean)flagObj) {
                    if (showMessage)
                        ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().get("region-flag-restriction").toString());
                    return false;
                }
            }

            if (!region.hasAccess(player) && !(dest instanceof Player)) {
                if (showMessage)
                    ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().get("region-flag-restriction").toString());
                return false;
            }
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteract0(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
            case RIGHT_CLICK_AIR: {
                if (event.getClickedBlock() != null)
                    event.setCancelled(!handleRegionEvent(event.getPlayer(), event.getClickedBlock().getLocation(), true));
                break;
            }

            case PHYSICAL: {
                event.setCancelled(!handleRegionEvent(event.getPlayer(), event.getPlayer().getLocation(), false));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteract1(EntityDamageByEntityEvent event) {
        if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            if (event.getDamager() instanceof Player)
                event.setCancelled(!handleDamageEvent((Player) event.getDamager(), event.getEntity(), event.getDamager().getLocation(), RegionFlags.DISABLE_PVP, true));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteract2(EntityDamageEvent event) {
        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            if (event.getEntity() instanceof Player)
                event.setCancelled(!handleDamageEvent((Player) event.getEntity(), event.getEntity(), event.getEntity().getLocation(), RegionFlags.FALL_DISABLE_DAMAGE, false));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteract3(PlayerInteractAtEntityEvent event) {
        // todo: does not work with villagers
        event.setCancelled(!handleEntityEvent(event.getPlayer(), event.getRightClicked(), event.getRightClicked().getLocation(), true));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockInteract0(BlockPlaceEvent event) {
        event.setCancelled(!handleRegionEvent(event.getPlayer(), event.getBlock().getLocation(), true));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockInteract1(BlockBreakEvent event) {
        event.setCancelled(!handleRegionEvent(event.getPlayer(), event.getBlock().getLocation(), true));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockInteract2(PlayerBedEnterEvent event) {
        event.setCancelled(!handleRegionEvent(event.getPlayer(), event.getBed().getLocation(), true));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockInteract4(PlayerBucketFillEvent event) {
        event.setCancelled(!handleRegionEvent(event.getPlayer(), event.getBlock().getLocation(), true));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockInteract5(PlayerTakeLecternBookEvent event) {
        event.setCancelled(!handleRegionEvent(event.getPlayer(), event.getLectern().getLocation(), true));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockInteract6(PlayerFishEvent event) {
        event.setCancelled(!handleRegionEvent(event.getPlayer(), event.getPlayer().getLocation(), true));
    }

}
