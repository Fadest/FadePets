package dev.fadest.pets.util;

import org.bukkit.ChatColor;

public class StringUtils {

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}
