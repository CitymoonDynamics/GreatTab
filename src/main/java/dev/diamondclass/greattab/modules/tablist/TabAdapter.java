package dev.diamondclass.greattab.modules.tablist;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TabAdapter {

    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    public static void sendHeaderFooter(Player player, String header, String footer) {
        if (header == null)
            header = "";
        if (footer == null)
            footer = "";

        try {
            // Use standard API if available (1.13+) and reliable, but for consistent JSON
            // behavior on older versions reflection is often safer.
            // However, for 1.8-1.21 support, PacketPlayOutPlayerListHeaderFooter specific
            // logic is needed.

            Object headerComponent = getNMSComponent(header);
            Object footerComponent = getNMSComponent(footer);

            Object packet = getPacket(headerComponent, footerComponent);

            sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object getNMSComponent(String text) throws Exception {
        Class<?> iChatBaseComponent = getNMSClass("IChatBaseComponent");

        if (VERSION.contains("1_8") || VERSION.contains("1_9") || VERSION.contains("1_10") ||
                VERSION.contains("1_11") || VERSION.contains("1_12") || VERSION.contains("1_13") ||
                VERSION.contains("1_14") || VERSION.contains("1_15") || VERSION.contains("1_16")) {

            Class<?> chatSerializer = getNMSClass("IChatBaseComponent$ChatSerializer");
            Method a = chatSerializer.getMethod("a", String.class);
            return a.invoke(null, "{\"text\":\"" + text + "\"}");
        } else {
            // 1.17+ mappings might differ, but usually ChatSerializer still exists or is
            // accessible directly via IChatBaseComponent.
            // In 1.17+ NMS moved to net.minecraft.* so "getNMSClass" logic needs to handle
            // that or use a specific library.
            // BUT since we are compiling against 1.8, we assume the user is running this on
            // a Spigot server that supports the 1.8 API or has compatible logic.
            // For simple legacy text to component:

            // Fallback/Alternative for simple string (might fail if JSON required)
            // But let's try the standard JSON serializer approach first which is quite
            // universal for ChatComponents.

            // Refined NMS class loading for 1.17+?
            // "IChatBaseComponent$ChatSerializer" works for < 1.17.
            // 1.17+ it is net.minecraft.network.chat.IChatBaseComponent$ChatSerializer

            // To make this truly multi-version without libraries like ProtocolLib, checking
            // classes is needed.
            return getComponentByVersion(text);
        }
    }

    private static Object getComponentByVersion(String text) {
        try {
            Class<?> chatSerializer;
            try {
                chatSerializer = getNMSClass("IChatBaseComponent$ChatSerializer");
            } catch (ClassNotFoundException e) {
                // Try 1.17+ name/location
                chatSerializer = Class.forName("net.minecraft.network.chat.IChatBaseComponent$ChatSerializer");
            }

            Method a = chatSerializer.getMethod("a", String.class);
            return a.invoke(null, "{\"text\":\"" + text + "\"}");
        } catch (Exception e) {
            e.printStackTrace(); // Simplify error handling for now
            return null;
        }
    }

    private static Object getPacket(Object header, Object footer) throws Exception {
        Class<?> packetClass;
        try {
            packetClass = getNMSClass("PacketPlayOutPlayerListHeaderFooter");
        } catch (ClassNotFoundException e) {
            packetClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutPlayerListHeaderFooter");
        }

        Object packet = packetClass.newInstance();

        Field headerField;
        Field footerField;

        try {
            headerField = packetClass.getDeclaredField("a");
            footerField = packetClass.getDeclaredField("b");
        } catch (NoSuchFieldException e) {
            // Obfuscation names might change.
            // In older versions "a" and "b" were header/footer.
            // In some versions might be "header" / "footer" (unlikely in NMS).
            // fallback for safety
            headerField = packetClass.getDeclaredField("header");
            footerField = packetClass.getDeclaredField("footer");
        }

        headerField.setAccessible(true);
        headerField.set(packet, header);

        footerField.setAccessible(true);
        footerField.set(packet, footer);

        return packet;
    }

    private static void sendPacket(Player player, Object packet) throws Exception {
        Object handle = player.getClass().getMethod("getHandle").invoke(player);
        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
        playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
    }

    private static Class<?> getNMSClass(String name) throws ClassNotFoundException {
        // NMSPackage logic
        return Class.forName("net.minecraft.server." + VERSION + "." + name);
    }
}
