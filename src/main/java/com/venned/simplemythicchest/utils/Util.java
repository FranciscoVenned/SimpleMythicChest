package com.venned.simplemythicchest.utils;

import org.bukkit.ChatColor;

import java.util.List;

public class Util {

    public static List<String> replacePlaceholders(List<String> list, String placeholder, String replacement) {
        for (int i = 0; i < list.size(); i++) {
            String line = colorize(list.get(i));
            list.set(i, line.replace(placeholder, replacement));
        }
        return list;
    }

    public static List<String> replacePlaceholders(List<String> list, String[] placeholders, String[] replacements) {
        if (placeholders.length != replacements.length) {
            throw new IllegalArgumentException("Number of placeholders must match number of replacements");
        }

        for (int i = 0; i < list.size(); i++) {
            String line = colorize(list.get(i));
            for (int j = 0; j < placeholders.length; j++) {
                line = line.replace(placeholders[j], replacements[j]);
            }
            list.set(i, line);
        }
        return list;
    }
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
