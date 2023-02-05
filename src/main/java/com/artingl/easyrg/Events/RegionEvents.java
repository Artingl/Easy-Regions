package com.artingl.easyrg.Events;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.misc.Regions.Region;
import com.artingl.easyrg.misc.Regions.RegionFlags;
import com.artingl.easyrg.misc.Utilities.ChatUtils;
import com.artingl.easyrg.misc.Utilities.WorldUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.Objects;

public class RegionEvents implements Listener {

    private boolean handleEntityEvent(Player player, Entity entity, Location location, boolean showMessage) {
        Region region = PluginMain.instance.getRegionsRegistry().getRegionAt(location);

        if (region != null) {
            if (!region.hasAccess(player)) {
                if (showMessage)
                    ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("region-restriction"));
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
                    ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("region-restriction"));
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
                        ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("region-flag-restriction"));
                    return false;
                }
            }

            if (!region.hasAccess(player) && !(dest instanceof Player)) {
                if (showMessage)
                    ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("region-flag-restriction"));
                return false;
            }
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void explodeEvent(EntityExplodeEvent event) {
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockSpreadEvent(BlockSpreadEvent event) {
        Region region0 = PluginMain.instance.getRegionsRegistry().getRegionAt(event.getSource().getLocation());
        Region region1 = PluginMain.instance.getRegionsRegistry().getRegionAt(event.getBlock().getLocation());

        if ((region0 == null && region1 != null)
                || (region1 == null && region0 != null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void redstoneEvent(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        Region region0 = PluginMain.instance.getRegionsRegistry().getRegionAt(block.getLocation());

        BlockFace[] blockFaces = {
                BlockFace.NORTH,
                BlockFace.SOUTH,
                BlockFace.EAST,
                BlockFace.WEST,
                BlockFace.DOWN,
                BlockFace.UP,
        };

        for (BlockFace blockFace: blockFaces) {
            Block relative = block.getRelative(blockFace);
            Region region1 = PluginMain.instance.getRegionsRegistry().getRegionAt(relative.getLocation());

            if ((region0 == null && region1 != null && !(boolean)region1.getFlag(RegionFlags.ALLOW_REDSTONE))
                    || (region1 == null && region0 != null && !(boolean)region0.getFlag(RegionFlags.ALLOW_REDSTONE))) {
                event.setNewCurrent(0);
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void pistonEvent(BlockPistonExtendEvent event) {
        BlockFace dir = event.getDirection();

        Region region0 = PluginMain.instance.getRegionsRegistry().getRegionAt(event.getBlock().getRelative(dir).getLocation());
        Region region1 = PluginMain.instance.getRegionsRegistry().getRegionAt(event.getBlock().getRelative(dir).getRelative(dir).getLocation());

        if ((region0 == null && region1 != null)
                || (region1 == null && region0 != null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void pistonEvent(BlockPistonRetractEvent event) {
        BlockFace dir = event.getDirection();

        Region region0 = PluginMain.instance.getRegionsRegistry().getRegionAt(event.getBlock().getRelative(dir).getLocation());
        Region region1 = PluginMain.instance.getRegionsRegistry().getRegionAt(event.getBlock().getRelative(dir).getRelative(dir).getLocation());

        if ((region0 == null && region1 != null)
                || (region1 == null && region0 != null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void liquidSpread(BlockFromToEvent event) {
        Region region0 = PluginMain.instance.getRegionsRegistry().getRegionAt(event.getBlock().getLocation());
        Region region1 = PluginMain.instance.getRegionsRegistry().getRegionAt(event.getToBlock().getLocation());

        if ((region0 == null && region1 != null)
                || (region1 == null && region0 != null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteract0(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();

        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))
            return;

        if (block != null) {
            Location location = block.getLocation();

            if (!event.getMaterial().isBlock()) {
                event.setCancelled(!handleRegionEvent(event.getPlayer(), location, !Objects.equals(event.getHand(), EquipmentSlot.OFF_HAND)));
                return;
            }

            if (WorldUtils.isMaterialTransmitter(block.getType()) && WorldUtils.isMaterialTransmitter(event.getMaterial())) {
                event.setCancelled(!handleRegionEvent(event.getPlayer(), location, !Objects.equals(event.getHand(), EquipmentSlot.OFF_HAND)));
                return;
            }

            location.add(event.getBlockFace().getDirection());
            event.setCancelled(!handleRegionEvent(event.getPlayer(), location, !Objects.equals(event.getHand(), EquipmentSlot.OFF_HAND)));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteract1(EntityDamageByEntityEvent event) {
        if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            if (event.getDamager() instanceof Player)
                event.setCancelled(!handleDamageEvent((Player) event.getDamager(), event.getEntity(), event.getDamager().getLocation(), RegionFlags.NO_PVP, true));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteract2(EntityDamageEvent event) {
        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            if (event.getEntity() instanceof Player)
                event.setCancelled(!handleDamageEvent((Player) event.getEntity(), event.getEntity(), event.getEntity().getLocation(), RegionFlags.NO_FALL_DAMAGE, false));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteract3(PlayerInteractEntityEvent event) {
        event.setCancelled(!handleEntityEvent(event.getPlayer(), event.getRightClicked(), event.getRightClicked().getLocation(), true));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockInteract0(BlockBreakEvent event) {
        if (event.getBlock().hasMetadata("easyrg-region-outline")) {
            event.setDropItems(false);
        }
        else
            event.setCancelled(!handleRegionEvent(event.getPlayer(), event.getBlock().getLocation(), true));
    }

//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void blockInteract1(BlockPlaceEvent event) {
//        event.setCancelled(!handleRegionEvent(event.getPlayer(), event.getBlockPlaced().getLocation(), true));
//    }

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
