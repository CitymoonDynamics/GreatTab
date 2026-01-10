package dev.diamondclass.greattab.utils;

import org.bukkit.ChatColor;

public class CC {

    public static String translate(String s) {
        if (s == null)
            return "";

        // Hex color support for 1.16+
        if (org.bukkit.Bukkit.getVersion().contains("1.16") ||
                org.bukkit.Bukkit.getVersion().contains("1.17") ||
                org.bukkit.Bukkit.getVersion().contains("1.18") ||
                org.bukkit.Bukkit.getVersion().contains("1.19") ||
                org.bukkit.Bukkit.getVersion().contains("1.20") ||
                org.bukkit.Bukkit.getVersion().contains("1.21")) {
            // We can use a more robust regex or checking logic, but strictly speaking
            // without the Method in the classpath (since we compile against 1.8)
            // it's harder to use net.md_5.bungee.api.ChatColor.of().
            // However, standard regex replacement often works if the server understands the
            // format.
            // For 1.8 compilation, we can't access ChatColor.of().
            // So we will stick to standard translation for now, but acknowledge 1.21 is
            // supported
            // because 1.8 plugins generally run fine on 1.21.
        }

        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
