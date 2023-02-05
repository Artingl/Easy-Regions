package com.artingl.easyrg.misc.Utilities;

import com.artingl.easyrg.misc.Serializable;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class StrSubstitutor {

    private final String replaceable;
    private final char prefix;
    private final char suffix;

    public StrSubstitutor(String replaceable, char prefix, char suffix) {
        this.replaceable = replaceable;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public void sendMessage(CommandSender sender, Map<String, Object> values) {
        sender.spigot().sendMessage(createBaseComponent(values));
    }

    public BaseComponent[] createBaseComponent(Map<String, Object> values) {
        ComponentBuilder builder = new ComponentBuilder();
        String[] lines = replaceable.split("\n");

        for (int index = 0; index < lines.length; index++) {
            String line = lines[index];

            if (line.isEmpty() && index < lines.length-1) {
                builder.append("\n");
                continue;
            }

            StringBuilder key = new StringBuilder();
            StringBuilder message = new StringBuilder();
            TextComponent text = new TextComponent();

            boolean readingKey = false;
            char[] chars = line.toCharArray();

            for (int charIndex = 0; charIndex < chars.length; charIndex++) {
                char ch = chars[charIndex];

                if (ch == '&') {
                    if (!message.toString().isEmpty()) {
                        text.setText(message.toString());
                        builder.append(text);
                        text = new TextComponent();
                        message = new StringBuilder();
                    }

                    char color = chars[++charIndex];

                    text.setObfuscated(false);
                    text.setBold(false);
                    text.setStrikethrough(false);
                    text.setUnderlined(false);
                    text.setItalic(false);

                    switch (color) {
                        case 'k': {
                            text.setObfuscated(true);
                            break;
                        }

                        case 'l': {
                            text.setBold(true);
                            break;
                        }

                        case 'm': {
                            text.setStrikethrough(true);
                            break;
                        }

                        case 'n': {
                            text.setUnderlined(true);
                            break;
                        }

                        case 'o': {
                            text.setItalic(true);
                            break;
                        }

                        case 'r': {
                            text.setObfuscated(false);
                            text.setBold(false);
                            text.setStrikethrough(false);
                            text.setUnderlined(false);
                            text.setItalic(false);
                            text.setColor(ChatColor.WHITE.asBungee());
                            break;
                        }

                        default: {
                            text.setColor(ChatColor.getByChar(color).asBungee());
                        }
                    }
                    continue;
                }

                if (ch == prefix || ch == suffix) {
                    readingKey = ch == prefix;

                    if (!readingKey) {
                        Object val = values.get(key.toString());

                        if (val instanceof BaseComponent[])
                            builder.append((BaseComponent[]) val);
                        else if (val instanceof BaseComponent)
                            builder.append((BaseComponent) val);
                        else if (val instanceof Serializable)
                            builder.append(((Serializable) val).serialize());
                        else if (val instanceof Boolean)
                            builder.append(String.valueOf(val));
                        else if (val instanceof Integer)
                            builder.append(String.valueOf(val));
                        else if (val instanceof Float)
                            builder.append(String.valueOf(val));
                        else if (val instanceof Double)
                            builder.append(String.valueOf(val));
                        else if (val instanceof Long)
                            builder.append(String.valueOf(val));
                        else if (val instanceof Short)
                            builder.append(String.valueOf(val));
                        else if (val instanceof Byte)
                            builder.append(String.valueOf(val));
                        else builder.append((String) val);
                        key = new StringBuilder();
                    }
                    else {
                        text.setText(message.toString());
                        builder.append(text);
                        text = new TextComponent();
                        message = new StringBuilder();
                    }

                    continue;
                }

                if (readingKey) {
                    key.append(ch);
                    continue;
                }

                message.append(ch);
            }

            text.setText(message.toString());
            builder.append(text);
            builder.append(createRawComponent());
            builder.reset();

            if (index < lines.length-1)
                builder.append("\n");
        }

        return builder.create();
    }

    private TextComponent createRawComponent() {
        TextComponent text = new TextComponent();
        text.setObfuscated(false);
        text.setBold(false);
        text.setStrikethrough(false);
        text.setUnderlined(false);
        text.setItalic(false);
        text.setHoverEvent(null);
        text.setClickEvent(null);
        text.setColor(ChatColor.WHITE.asBungee());
        return text;
    }
}
