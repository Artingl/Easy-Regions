package com.artingl.easyrg.Commands;

import com.artingl.easyrg.Permissions.Permissions;
import com.artingl.easyrg.PluginMain;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

public class Commands {

    private static final Object[] executors = {
            new CommandEsr(),
            new CommandRegion(),
            new CommandSelection(),
    };

    public static void register() {
        int i = 0;
        for (String cmd : PluginMain.instance.getDescription().getCommands().keySet()) {
            PluginCommand command = PluginMain.instance.getCommand(cmd);

            if (command == null)
                continue;

            command.setPermission(Permissions.getByString(cmd).getPermission());
            command.setExecutor((CommandExecutor) executors[i]);
            command.setTabCompleter((TabCompleter) executors[i++]);
        }
    }

}
