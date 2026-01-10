package dev.diamondclass.greattab.utils;

import org.bukkit.ChatColor;

/**
 * 'CC' stands for 'ChatColor'. It's just a quick utility so we don't
 * have to type 'ChatColor.translateAlternateColorCodes' every single time.
 */
public class CC {

    /**
     * Translates classic color codes (&a, &l, etc.) and also supports Hex
     * (&#RRGGBB).
     * 
     * @param s The text to translate.
     * @return The text with actual Minecraft colors.
     */
    public static String translate(String s) {
        if (s == null)
            return "";

        try {
            // We search for patterns like &#FFFFFF for modern Hex colors (1.16+)
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("&#[a-fA-F0-9]{6}");
            java.util.regex.Matcher matcher = pattern.matcher(s);

            while (matcher.find()) {
                String hexCode = s.substring(matcher.start(), matcher.end());
                String replaceSharp = hexCode.replace("&#", "#");

                // Using reflection so it still compiles on old versions but works on new ones
                Class<?> chatColorClass = Class.forName("net.md_5.bungee.api.ChatColor");
                java.lang.reflect.Method ofMethod = chatColorClass.getMethod("of", String.class);
                Object color = ofMethod.invoke(null, replaceSharp);

                s = s.replace(hexCode, color.toString());
                matcher = pattern.matcher(s);
            }
        } catch (Exception ignored) {
            // If Hex fails (like on an old Spigot version), we just keep going
        }

        // Finally, translate the traditional '&' codes
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
