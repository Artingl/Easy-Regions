package com.artingl.easyrg.Commands;

import com.artingl.easyrg.Commands.Runners.ClearCommandRunner;
import com.artingl.easyrg.Commands.Runners.RunnerRegistry;
import com.artingl.easyrg.Commands.Runners.OutlineCommandRunner;
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

public class CommandSelection implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = Objects.requireNonNull(Bukkit.getPlayer(sender.getName()));

        if (!Permissions.hasPermission(sender, Permissions.COMMAND_SELECTION)) {
            ChatUtils.sendDecoratedMessage(sender, PluginMain.instance.getLanguage().get("no-permissions").toString());
            return false;
        }

        if (args.length == 0) {
            ChatUtils.sendDecoratedMessage(sender, PluginMain.instance.getLanguage().get("not-enough-arguments-provided").toString());
            return false;
        }
        else if (args[0].equals("clear")) return RunnerRegistry.commandsList.execute(ClearCommandRunner.class, player, null, Permissions.COMMAND_SELECTION_CLEAR);
        else if (args[0].equals("outline")) return RunnerRegistry.commandsList.execute(OutlineCommandRunner.class, player, args, Permissions.COMMAND_SELECTION_OUTLINE);
//        else if (args[0].equals("set")) return CommandRunner.commandsList.execute(SetRunner.class, player, args, Permissions.COMMAND_SELECTION_SET);

        ChatUtils.sendDecoratedMessage(sender,
                MessageFormat.format(
                        PluginMain.instance.getLanguage().get("invalid-argument").toString(),
                        args[0]
                )
        );

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> values = new ArrayList<>();

        if (args.length == 1) {
            values.add("clear");
            values.add("outline");
//            values.add("set");
        }
        else if (args[0].equals("set")) {
            return ChatUtils.MATERIALS_LIST;
        }

        return values;
    }

}