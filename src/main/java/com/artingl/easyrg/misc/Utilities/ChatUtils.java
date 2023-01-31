package com.artingl.easyrg.misc.Utilities;

import com.artingl.easyrg.PluginMain;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

    public static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    public static final ArrayList<String> MATERIALS_LIST = new ArrayList<>();

    public static String translateHexColorCodes(String message) {
        // Sourced from this post by imDaniX: https://github.com/SpigotMC/BungeeCord/pull/2883#issuecomment-653955600
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.COLOR_CHAR + "x"
                    + ChatColor.COLOR_CHAR + group.charAt(0) + ChatColor.COLOR_CHAR + group.charAt(1)
                    + ChatColor.COLOR_CHAR + group.charAt(2) + ChatColor.COLOR_CHAR + group.charAt(3)
                    + ChatColor.COLOR_CHAR + group.charAt(4) + ChatColor.COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }

    public static String decorateMessage(String message) {
        StringBuilder result = new StringBuilder();

        for (String line: message.split("\n")) {
            if (line.isEmpty()) {
                result.append(" \n");
                continue;
            }

            result.append((PluginMain.instance.getConfig().get("prefix") + " &f" + line).replace('&', ChatColor.COLOR_CHAR)).append('\n');
        }

        return result.toString();
    }

    public static void sendDecoratedMessage(Player player, String message) {
        player.sendMessage(decorateMessage(message));
    }

    public static void sendDecoratedMessage(CommandSender sender, String message) {
        sender.sendMessage(decorateMessage(message));
    }
}
