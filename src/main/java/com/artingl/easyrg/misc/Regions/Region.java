package com.artingl.easyrg.misc.Regions;

import com.artingl.easyrg.Permissions.Permissions;
import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.misc.Utilities.ChatUtils;
import com.artingl.easyrg.misc.Utilities.StrSubstitutor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class Region {

    private final BoundingBox boundingBox;
    private final World world;
    private final List<RegionMember> members;
    private final String name;
    private Map<RegionFlags, Object> flags;

    public Region(RegionPosition start, RegionPosition end, World world, List<RegionMember> members, String name) {
        this(start, end, world, members, new HashMap<>(), name);

        this.flags.put(RegionFlags.NO_FALL_DAMAGE, false);
        this.flags.put(RegionFlags.ALLOW_REDSTONE, false);
        this.flags.put(RegionFlags.MOB_SPAWN, true);
        this.flags.put(RegionFlags.NO_PVP, false);
    }

    public Region(RegionPosition start, RegionPosition end, World world, List<RegionMember> members, Map<RegionFlags, Object> flags, String name) {
        this.name = name;
        this.flags = new HashMap<>(flags);
        this.world = world;
        this.members = new ArrayList<>(members);
        this.boundingBox = new BoundingBox(
                start.getX(), start.getY(), start.getZ(),
                end.getX(), end.getY(), end.getZ()
        );
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public RegionClaimResult claim() {
        return PluginMain.instance.getRegionsRegistry().registerRegion(this);
    }

    public RegionRemoveResult remove(Player player) {
        if (!hasAccess(player))
            return RegionRemoveResult.NO_PERMISSIONS;

        return PluginMain.instance.getRegionsRegistry().unregisterRegion(getName());
    }

    public boolean update() {
        return PluginMain.instance.getRegionsRegistry().updateRegion(this);
    }

    public World getWorld() {
        return world;
    }

    public List<RegionMember> getMembers() {
        return members;
    }

    public boolean addMember(UUID uuid) {
        if (members.contains(RegionMember.mask(uuid)))
            return false;

        members.add(new RegionMember(uuid, RegionMember.Permissions.MEMBER, System.currentTimeMillis()));
        return true;
    }

    public int removeMember(UUID uuid) {
        if (members.size() <= 1)
            return 2;

        if (!members.contains(uuid))
            return 1;

        members.remove(uuid);

        return 0;
    }

    public String getFlagDescription(RegionFlags flag) {
        return PluginMain.instance.getLanguage()
                .getString(flag.getDescriptionTag());
    }

    public Object getFlag(RegionFlags flag) {
        return flags.get(flag);
    }

    public void setFlag(RegionFlags flag, Object value) {
        flags.put(flag, value);
    }

    public boolean hasAccess(Player player) {
        if (Permissions.hasPermission(player, Permissions.REGIONS_FULL_ACCESS))
            return true;

        if (members == null)
            return false;

        return members.contains(player.getUniqueId());
    }

    public String getName() {
        return name;
    }

    public Map<RegionFlags, Object> getFlags() {
        return flags;
    }

    @Override
    public String toString() {
        return "Region{members=" + members + ", flags=" + flags + ", name=" + name + ", world=" + world.getName() + "}";
    }

    public void printInfo(CommandSender sender, int flagsScroll) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", getName());
        values.put("members", getMembersFormattedString(3));
        values.put("flags", getFlagsFormattedString(flagsScroll));

        if (flagsScroll == 0) values.put("button-flags-backwards", "");
        else values.put("button-flags-backwards", ChatUtils.createLeftArrowList("/rg info " + (flagsScroll - 1)));

        if (flags.size() / 2 > flagsScroll) values.put("button-flags-forward", ChatUtils.createRightArrowList("/rg info " + (flagsScroll + 1)));
        else values.put("button-flags-forward", "");

        StrSubstitutor sub = new StrSubstitutor(ChatUtils.wrapYaml(PluginMain.instance.getLanguage().getStringList("region-info")), '{', '}');
        sub.sendMessage(sender, values);
    }

    public void printInfo(Player player) {
        printInfo(player, 0);

        if (PluginMain.instance.isSoundEnabled())
            player.playSound(player.getLocation(), Sound.valueOf(PluginMain.instance.getConfig()
                    .getConfigurationSection("region-info")
                    .getConfigurationSection("sounds")
                    .getString("region-info-sound")), 1, 1);
    }

    private BaseComponent[] getFlagsFormattedString(int scroll) {
        StrSubstitutor sub =
                new StrSubstitutor(ChatUtils.wrapYaml(PluginMain.instance.getLanguage().getStringList("region-flag-hover")), '{', '}');
        ComponentBuilder builder = new ComponentBuilder();
        Map<String, Object> values = new HashMap<>();

        int i = 0;
        for (Map.Entry<RegionFlags, Object> entry: flags.entrySet()) {
            if (i++ >= scroll + 1) {
                break;
            }

            if (i < scroll)
                continue;

            if (i >= scroll + 1 && i != 1)
                builder.append(ChatUtils.coloredTextComponent(", ", ChatColor.GOLD));

            values.put("name", entry.getKey().getFriendlyName());
            values.put("value", entry.getValue());
            values.put("description", getFlagDescription(entry.getKey()));

            TextComponent memberText = new TextComponent();
            memberText.setText(entry.getKey().getFriendlyName());
            memberText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, sub.createBaseComponent(values)));

            builder.append(memberText);
        }

        return builder.create();
    }

    private BaseComponent[] getMembersFormattedString(int max) {
        String timeFormat = PluginMain.instance.getConfig()
                .getConfigurationSection("global-settings")
                .getConfigurationSection("misc")
                .getString("date-format");
        StrSubstitutor sub =
                new StrSubstitutor(ChatUtils.wrapYaml(PluginMain.instance.getLanguage().getStringList("region-player-hover")), '{', '}');
        ComponentBuilder builder = new ComponentBuilder();
        Map<String, Object> values = new HashMap<>();

        int i = 0;

        for (RegionMember member: members) {
            if (i++ >= max) {
                builder.append(" ...");
                break;
            }

            if (i > 1)
                builder.append(", ");

            OfflinePlayer player = member.offlinePlayer();

            if (player.getName() == null)
                continue;

            values.put("name", player.getName());
            values.put("since", new SimpleDateFormat(timeFormat).format(new Timestamp(member.timestampSince())));
            values.put("permissions", member.permissions().toString());

            TextComponent memberText = new TextComponent();
            memberText.setText(player.getName());
            memberText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, sub.createBaseComponent(values)));

            builder.append(memberText);
        }

        return builder.create();
    }

    @Override
    public int hashCode() {
        return boundingBox.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Region))
            return false;

        return ((Region) obj).boundingBox.equals(boundingBox);
    }
}
