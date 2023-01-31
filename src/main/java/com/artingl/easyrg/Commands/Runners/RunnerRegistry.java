package com.artingl.easyrg.Commands.Runners;

import com.artingl.easyrg.Permissions.Permissions;
import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.misc.Utilities.ChatUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class RunnerRegistry {
    public static final RunnerList commandsList = new RunnerList();

    public static void init() {
        commandsList.clear();

        commandsList.register(
                OutlineCommandRunner.class,
                SetCommandRunner.class,
                ClearCommandRunner.class,
                CreateRegionCommandRunner.class,
                RegionInfoCommandRunner.class
        );
    }


    public static class RunnerList {

        private Map<Integer, Class<? extends CommandRunner>> map;

        public RunnerList() {
            map = new HashMap<>();
        }

        public void clear() {
            map.clear();
        }

        public void register(Class<? extends CommandRunner> ...objs) {
            for (Class<? extends CommandRunner> c: objs)
                map.put(c.getName().hashCode(), c);
        }

        public void register(Class<? extends CommandRunner> c) {
            map.put(c.getName().hashCode(), c);
        }

        public boolean execute(Class<? extends CommandRunner> clazz, Player player, String[] args, Permissions permission) {
            if (!permission.hasPermission(player)) {
                ChatUtils.sendDecoratedMessage(player, PluginMain.instance.getLanguage().get("no-permissions").toString());
                return false;
            }

            try {
                Class<? extends CommandRunner> val = map.get(clazz.getName().hashCode());

                if (val == null) {
                    return false;
                }

                return val.newInstance().run(player, player.getWorld(), args);
            } catch (Exception e) {
                PluginMain.logger.log(Level.SEVERE, "Unable to create new instance of CommandRunner!", e);
                System.exit(1);
            }

            return false;
        }
    }
}
