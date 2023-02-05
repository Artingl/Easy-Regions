package com.artingl.easyrg.misc.Utilities;

import com.artingl.easyrg.PluginMain;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

    public static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    public static final List<String> MATERIALS_LIST = new ArrayList<>();
    public static final List<String> PLAYER_NAMES_LIST = new ArrayList<>();

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

            result.append((PluginMain.instance.getPrefix() + " &f" + line).replace('&', ChatColor.COLOR_CHAR)).append('\n');
        }

        return result.toString();
    }

    public static void sendDecoratedMessage(Player player, String message) {
        player.sendMessage(decorateMessage(message));
    }

    public static void sendDecoratedMessage(CommandSender sender, String message) {
        sender.sendMessage(decorateMessage(message));
    }

    public static String wrapYaml(List<String> list) {
        StringBuilder result = new StringBuilder();

        for (String line: list) {
            if (line.isEmpty()) {
                result.append(" \n");
                continue;
            }

            result.append(line).append('\n');
        }

        return result.toString();
    }

    public static TextComponent coloredTextComponent(String string, ChatColor color) {
        TextComponent text = new TextComponent(string);
        text.setColor(color);
        return text;
    }

    public static TextComponent createLeftArrowList(String command) {
        TextComponent arrow = new TextComponent("«");
        arrow.setBold(true);
        arrow.setColor(ChatColor.GREEN);

        arrow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        arrow.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                ChatUtils.coloredTextComponent(PluginMain.instance.getLanguage().getString("list-backward-click"), ChatColor.GREEN)
        }));

        return arrow;
    }

    public static TextComponent createRightArrowList(String command) {
        TextComponent arrow = new TextComponent("»");
        arrow.setBold(true);
        arrow.setColor(ChatColor.GREEN);

        arrow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        arrow.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                ChatUtils.coloredTextComponent(PluginMain.instance.getLanguage().getString("list-forward-click"), ChatColor.GREEN)
        }));

        return arrow;
    }
}
