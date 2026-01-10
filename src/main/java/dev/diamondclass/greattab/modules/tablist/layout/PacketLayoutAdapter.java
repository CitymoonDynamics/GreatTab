package dev.diamondclass.greattab.modules.tablist.layout;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Handles the NMS packet logic for the HCF Layout system.
 * <p>
 * This class uses heavy Reflection to maintain compatibility across multiple
 * Minecraft versions (1.8 - 1.16+).
 * It abstracts away the direct NMS calls required to send PlayerInfo packets,
 * which are responsible
 * for creating the "fake" players you see in the HCF tablist grid.
 */
public class PacketLayoutAdapter {

    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static Class<?> packetClass;
    private static Class<?> packetActionClass;
    private static Class<?> packetDataClass;
    private static Class<?> gameProfileClass;
    private static Class<?> chatBaseComponentClass;

    static {
        try {
            gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");

            if (VERSION.contains("1_8") || VERSION.contains("1_9") || VERSION.contains("1_10") ||
                    VERSION.contains("1_11") || VERSION.contains("1_12") || VERSION.contains("1_13") ||
                    VERSION.contains("1_14") || VERSION.contains("1_15") || VERSION.contains("1_16")) {

                packetClass = getNMSClass("PacketPlayOutPlayerInfo");
                packetActionClass = getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
                packetDataClass = getNMSClass("PacketPlayOutPlayerInfo$PlayerInfoData");
                chatBaseComponentClass = getNMSClass("IChatBaseComponent");
            } else {
                // 1.17+
                // Handling 1.19+ separation is complex (ClientboundPlayerInfoUpdatePacket).
                // For MVP we assume < 1.19 or simplified support.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a packet to the player to create a "fake" player entry in their
     * tablist.
     * This is used to populate the grid slots.
     *
     * @param player      The target player who will see the entry.
     * @param uuid        The UUID of the fake player (must be unique).
     * @param name        The name (used for sorting, e.g. !001).
     * @param displayText The text displayed (the actual content).
     * @param skin        The skin texture to apply.
     */
    public static void createFakePlayer(Player player, UUID uuid, String name, String displayText,
            SkinManager.Skin skin) {
        try {

            // TODO: 1.19+ introduced chat signing and profile keys, changing the packet
            // structure significantly.
            // For now, we are skipping this logic for newer versions.
            if (VERSION.contains("1_19") || VERSION.contains("1_20") || VERSION.contains("1_21")) {
                return;
            }

            // Create the GameProfile (com.mojang.authlib.GameProfile)
            // This object holds the UUID, Name, and Textures of the player.
            Object profile = gameProfileClass.getConstructor(UUID.class, String.class).newInstance(uuid, name);

            // Add Property (Texture)
            // Add Property (Texture)
            if (skin != null) {
                Object propertyMap = gameProfileClass.getMethod("getProperties").invoke(profile);
                Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");
                // Constructor (name, value, signature)
                Object property = propertyClass.getConstructor(String.class, String.class, String.class)
                        .newInstance("textures", skin.getValue(), skin.getSignature());

                // Put into multimap
                Method putMethod = propertyMap.getClass().getMethod("put", Object.class, Object.class);
                putMethod.invoke(propertyMap, "textures", property);
            }

            Object packet = packetClass.newInstance();

            // Set action to ADD_PLAYER
            Field actionField = packetClass.getDeclaredField("a");
            actionField.setAccessible(true);
            actionField.set(packet, Enum.valueOf((Class<Enum>) packetActionClass, "ADD_PLAYER"));

            // Create PlayerInfoData
            // Constructor varies by version
            // 1.8: (GameProfile, int latency, EnumGamemode, IChatBaseComponent)
            Object textComponent = getChatComponent(displayText);
            Object gameMode = getNMSClass("WorldSettings$EnumGamemode").getField("SURVIVAL").get(null);

            Constructor<?> dataConstructor = packetDataClass.getConstructor(packetClass, gameProfileClass, int.class,
                    getNMSClass("WorldSettings$EnumGamemode"), chatBaseComponentClass);
            Object data = dataConstructor.newInstance(packet, profile, 0, gameMode, textComponent);

            Field dataField = packetClass.getDeclaredField("b");
            dataField.setAccessible(true);

            List list = (List) dataField.get(packet);
            list.add(data);

            sendPacket(player, packet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object getChatComponent(String text) {
        try {
            Class<?> chatSerializer = getNMSClass("IChatBaseComponent$ChatSerializer");
            return chatSerializer.getMethod("a", String.class).invoke(null, "{\"text\":\"" + text + "\"}");
        } catch (Exception e) {
            return null;
        }
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
