package com.artingl.easyrg.Permissions;

import com.artingl.easyrg.PluginMain;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public enum Permissions {

    COMMAND_ESR("commands.easyrg", "easyrg", "easyregions", "esr", "er"),
    COMMAND_RG("commands.rg", "rg", "region"),
    COMMAND_SELECTION("commands.selection", "selection", "sel", "select"),
    COMMAND_SELECTION_CLEAR("commands.selection.clear"),
    COMMAND_SELECTION_OUTLINE("commands.selection.outline"),
    COMMAND_SELECTION_SET("commands.selection.set"),
    COMMAND_RG_CREATE("commands.rg.create"),
    COMMAND_RG_INFO("commands.rg.info"),
    REGIONS_FULL_ACCESS("regions.full_access");


    private final String id;
    private final String[] commands;

    Permissions(String id, String ...commands) {
        this.id = PluginMain.instance.getDescription().getName().toLowerCase(Locale.ROOT) + "." + id;
        this.commands = commands;
    }

    public static Permissions getByString(String cmd) {
        for (Permissions permission: Permissions.values()) {
            if (permission.appliesCommand(cmd))
                return permission;
        }

        return null;
    }

    public static boolean hasPermission(Player player, Permissions perm) {
        return player.isOp() || player.hasPermission(perm.getPermission());
    }

    public static boolean hasPermission(CommandSender sender, Permissions perm) {
        return sender.isOp() || sender.hasPermission(perm.getPermission());
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.isOp() || sender.hasPermission(getPermission());
    }

    public boolean hasPermission(Player player) {
        return player.isOp() || player.hasPermission(getPermission());
    }

    public String getPermission() {
        return id;
    }

    public boolean appliesCommand(String c) {
        for (String cmd: commands) {
            if (cmd.equals(c) || ("/" + cmd).equals(c))
                return true;
        }

        return false;
    }
}
