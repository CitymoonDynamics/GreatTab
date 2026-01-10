package dev.diamondclass.greattab.modules.tablist.layout;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;

public class ScoreboardTeamAdapter {

    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static Class<?> packetClass;

    static {
        try {
            packetClass = getNMSClass("PacketPlayOutScoreboardTeam");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTeam(Player player, String teamName, String prefix, String suffix,
            Collection<String> entries, int mode) {
        try {
            Object packet = packetClass.newInstance();

            setField(packet, "a", teamName);
            setField(packet, "h", mode);

            if (mode == 0 || mode == 2) {
                setField(packet, "b", teamName);
                setField(packet, "c", prefix);
                setField(packet, "d", suffix);
                setField(packet, "e", "always");
                setField(packet, "f", 0);
            }

            if (mode == 0 || mode == 3 || mode == 4) {
                setField(packet, "g", entries);
            }

            sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    private static void sendPacket(Player player, Object packet) throws Exception {
        Object handle = player.getClass().getMethod("getHandle").invoke(player);
        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
        playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
    }

    private static Class<?> getNMSClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + VERSION + "." + name);
    }
}
