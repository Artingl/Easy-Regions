package com.artingl.easyrg.Commands;

import com.artingl.easyrg.Commands.Runners.CreateRegionCommandRunner;
import com.artingl.easyrg.Commands.Runners.RegionInfoCommandRunner;
import com.artingl.easyrg.Commands.Runners.RunnerRegistry;
import com.artingl.easyrg.Permissions.Permissions;
import com.artingl.easyrg.PluginMain;
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

public class CommandRegion implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = Objects.requireNonNull(Bukkit.getPlayer(sender.getName()));

        if (!Permissions.hasPermission(sender, Permissions.COMMAND_RG)) {
            ChatUtils.sendDecoratedMessage(sender, PluginMain.instance.getLanguage().get("no-permissions").toString());
            return false;
        }

        if (args.length == 0) {
            ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().get("not-enough-arguments-provided").toString());
            return false;
        }
        else if (args[0].equals("create"))
            return RunnerRegistry.commandsList.execute(CreateRegionCommandRunner.class, player, args, Permissions.COMMAND_RG_CREATE);
        else if (args[0].equals("info"))
            return RunnerRegistry.commandsList.execute(RegionInfoCommandRunner.class, player, args, Permissions.COMMAND_RG_INFO);

        ChatUtils.sendDecoratedMessage(sender,
                MessageFormat.format(
                        PluginMain.instance.getLanguage().get("invalid-argument").toString(),
                        args[0]
                )
        );

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> values = new ArrayList<>();

        if (args.length == 1) {
            values.add("create");
            values.add("remove");
            values.add("flags");
            values.add("info");
        }

        return values;
    }

}
