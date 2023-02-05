package com.artingl.easyrg.Commands;

import com.artingl.easyrg.Permissions.Permissions;
import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.misc.Utilities.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CommandEsr implements CommandExecutor, TabCompleter {

    // fixme: should be rewritten

    private boolean help(CommandSender sender) {
        StringBuilder result = new StringBuilder();

        result.append(MessageFormat.format(
                Objects.requireNonNull(PluginMain.instance.getLanguage().getString("plugin-info")),
                PluginMain.instance.getDescription().getVersion(),
                "not found"
        )).append('\n');

        for (Map.Entry<String, Map<String, Object>> entry: PluginMain.instance.getDescription().getCommands().entrySet()) {
            result.append("/").append(entry.getKey()).append(" -> ").append(entry.getValue().get("description")).append('\n');
        }

        ChatUtils.sendDecoratedMessage(sender, result.toString());

        return true;
    }

    private boolean reload(CommandSender sender) {
        PluginMain.instance.reload();
        ChatUtils.sendDecoratedMessage(sender,
                PluginMain.instance.getLanguage().getString("plugin-reloaded"));

        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.hasPermission(sender, Permissions.COMMAND_ESR)) {
            ChatUtils.sendDecoratedMessage(sender, PluginMain.instance.getLanguage().getString("no-permissions"));

            return false;
        }

        if (args.length == 0) {
            ChatUtils.sendDecoratedMessage(sender,
                    MessageFormat.format(
                            Objects.requireNonNull(PluginMain.instance.getLanguage().getString("plugin-info")),
                            PluginMain.instance.getDescription().getVersion(),
                            "not found"
                    )
            );

            return true;
        }
        else if (args[0].equals("reload")) return this.reload(sender);
        else if (args[0].equals("help")) return this.help(sender);

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
            values.add("help");
            values.add("reload");
        }

        return values;
    }
}
