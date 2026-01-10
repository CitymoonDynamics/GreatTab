package dev.diamondclass.greattab.utils;

import org.bukkit.ChatColor;

public class CC {

    public static String translate(String s) {
        if (s == null)
            return "";
        try {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("&#[a-fA-F0-9]{6}");
            java.util.regex.Matcher matcher = pattern.matcher(s);

            while (matcher.find()) {
                String hexCode = s.substring(matcher.start(), matcher.end());
                String replaceSharp = hexCode.replace("&#", "#");
                Class<?> chatColorClass = Class.forName("net.md_5.bungee.api.ChatColor");
                java.lang.reflect.Method ofMethod = chatColorClass.getMethod("of", String.class);
                Object color = ofMethod.invoke(null, replaceSharp);

                s = s.replace(hexCode, color.toString());
                matcher = pattern.matcher(s);
            }
        } catch (Exception ignored) {
        }

        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
