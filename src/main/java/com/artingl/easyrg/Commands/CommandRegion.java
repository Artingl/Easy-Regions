package com.artingl.easyrg.Commands;

import com.artingl.easyrg.Commands.Runners.*;
import com.artingl.easyrg.Permissions.Permissions;
import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.misc.Regions.Region;
import com.artingl.easyrg.misc.Regions.RegionFlags;
import com.artingl.easyrg.misc.Regions.RegionMember;
import com.artingl.easyrg.misc.Utilities.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CommandRegion implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = Bukkit.getPlayer(sender.getName());

        if (player == null) {
            ChatUtils.sendDecoratedMessage(sender, PluginMain.instance.getLanguage().getString("player-only-command"));
            return false;
        }

        if (!Permissions.hasPermission(sender, Permissions.COMMAND_RG)) {
            ChatUtils.sendDecoratedMessage(sender, PluginMain.instance.getLanguage().getString("no-permissions"));
            return false;
        }

        if (args.length == 0) {
            ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().getString("not-enough-arguments-provided"));
            return false;
        }
        else if (args[0].equals("claim"))
            return RunnerRegistry.commandsList.execute(RegionClaimCommandRunner.class, player, args, Permissions.COMMAND_RG_CLAIM);
        else if (args[0].equals("info"))
            return RunnerRegistry.commandsList.execute(RegionInfoCommandRunner.class, player, args, Permissions.COMMAND_RG_INFO);
        else if (args[0].equals("unclaim"))
            return RunnerRegistry.commandsList.execute(RegionUnclaimCommandRunner.class, player, args, Permissions.COMMAND_RG_UNCLAIM);
        else if (args[0].equals("flag"))
            return RunnerRegistry.commandsList.execute(RegionFlagCommandRunner.class, player, args, Permissions.COMMAND_RG_FLAG);
        else if (args[0].equals("add"))
            return RunnerRegistry.commandsList.execute(RegionAddCommandRunner.class, player, args, Permissions.COMMAND_RG_ADD);
        else if (args[0].equals("remove"))
            return RunnerRegistry.commandsList.execute(RegionRemoveCommandRunner.class, player, args, Permissions.COMMAND_RG_REMOVE);

        ChatUtils.sendDecoratedMessage(sender,
                MessageFormat.format(
                        Objects.requireNonNull(PluginMain.instance.getLanguage().getString("invalid-argument")),
                        args[0]
                )
        );

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> values = new ArrayList<>();

        if (args.length == 1) {
            values.add("claim");
            values.add("unclaim");
            values.add("flag");
            values.add("info");
            values.add("add");
            values.add("remove");
            values.add("permissions");
        }
        else if (args[0].equals("add") && args.length == 3) {
            values.addAll(ChatUtils.PLAYER_NAMES_LIST);
        }
        else if (args[0].equals("permissions") && args.length == 4) {
            values.add("member");
            values.add("moderator");
            values.add("owner");
        }
        else if ((args[0].equals("remove")
                || args[0].equals("permissions")) && args.length == 3) {
            Region region = PluginMain.instance.getRegionsRegistry().getRegion(args[1]);

            if (region != null) {
                for (RegionMember member: region.getMembers()) {
                    values.add(member.offlinePlayer().getName());
                }
            }
        }
        else if ((args[0].equals("unclaim")
                || args[0].equals("flag")
                || args[0].equals("add")
                || args[0].equals("remove")
                || args[0].equals("permissions")) && args.length == 2) {
            Player player = Bukkit.getPlayer(sender.getName());

            if (player != null) {
                Region[] regions = PluginMain.instance.getRegionsRegistry().getRegionsByMember(player.getUniqueId());

                if (regions != null) {
                    for (Region region: regions)
                        values.add(region.getName());
                }
            }
        }
        else if (args[0].equals("flag")) {
            if (args.length == 3) {
                for (RegionFlags flag : RegionFlags.values())
                    values.add(flag.serializeName());
            }
            else if (args.length == 4) {
                RegionFlags.byName(args[2]).addHints(values);
            }
        }

        return values;
    }

}
