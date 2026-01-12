package dev.diamondclass.greattab.modules.tablist.layout;

import dev.diamondclass.greattab.Plugin;
import dev.diamondclass.greattab.modules.frameworks.Config;
import dev.diamondclass.greattab.utils.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This is where the magic for the custom 80-slot tablist happens.
 * It manages a grid of "fake" players to create that classic HCF/competitive
 * look.
 * We handle mapping the columns (LEFT, MIDDLE, etc.) from the config to actual
 * slots (1-80).
 */
@RequiredArgsConstructor
public class LayoutManager {

    private final Plugin plugin;
    // We cache the slots for each player so we don't have to recreate them
    // constantly
    private final Map<UUID, FakeSlot[]> viewCache = new HashMap<>();
    private boolean isEnabled;
    private org.bukkit.scheduler.BukkitTask task;

    /**
     * Initializes the system, loads skins, and starts the background update task
     * if the layout is enabled in the config.
     */
    public void load() {
        if (task != null) {
            task.cancel();
            task = null;
        }

        this.isEnabled = plugin.getTabManager().getTabConfig().getBoolean("layout.enabled");
        if (isEnabled) {
            SkinManager.load(plugin.getTabManager().getTabConfig());
            // Every second (20 ticks), we refresh what players see on their tab
            this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::tick, 20L, 20L);
        } else {
            // If it was enabled and now it's not, we should probably clear the tab for
            // players
            // but for simplicity we'll just stop the task for now.
            viewCache.clear();
        }
    }

    public void reload() {
        this.load();
    }

    // Just loops through everyone online and updates their view
    private void tick() {
        if (Bukkit.getOnlinePlayers().isEmpty())
            return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerView(player);
            // We remove real players from the tablist so only the 80 fake slots are visible
            removeRealPlayers(player);
        }
    }

    /**
     * The main logic to populate a player's tablist.
     * We map the four columns from the config to their specific slot ranges.
     */
    private void updatePlayerView(Player player) {
        if (!viewCache.containsKey(player.getUniqueId())) {
            initializeView(player);
        }

        Config config = plugin.getTabManager().getTabConfig();

        // Each column has 20 slots. We start at 1, 21, 41, and 61.
        updateColumn(player, config.getStringList("layout.LEFT"), 1);
        updateColumn(player, config.getStringList("layout.MIDDLE"), 21);
        updateColumn(player, config.getStringList("layout.RIGHT"), 41);
        updateColumn(player, config.getStringList("layout.FAR_RIGHT"), 61);
    }

    /**
     * Fills a specific column with text and skins.
     */
    private void updateColumn(Player player, java.util.List<String> entries, int startSlot) {
        for (int i = 0; i < 20; i++) {
            int slot = startSlot + i;
            // Default to a gray placeholder if the config doesn't have enough lines
            String entry = (i < entries.size()) ? entries.get(i) : "LIGHT_GRAY;";

            // The format in the config is "SKIN;TEXT"
            String[] parts = entry.split(";", 2);
            String skinName = parts[0];
            String text = (parts.length > 1) ? parts[1] : "";

            // If PlaceholderAPI is installed, we process placeholders like %player_name%
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                text = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
            }
            text = plugin.getAnimationManager().parseAnimations(text);
            text = CC.translate(text);

            updateSlot(player, slot, text, SkinManager.getSkin(skinName));
        }
    }

    /**
     * When a player first joins, we need to send them 80 fake player packets
     * to populate their tablist grid.
     */
    private void initializeView(Player player) {
        for (int i = 1; i <= 80; i++) {
            String teamName = getTeamName(i);
            String entryName = getEntryName(i);

            // We create a fake player packet with a unique name and a neutral skin
            PacketLayoutAdapter.createFakePlayer(player, UUID.randomUUID(), entryName, "",
                    SkinManager.getSkin("LIGHT_GRAY"));

            // We use Scoreboard Teams to handle the text display (Prefix/Suffix)
            ScoreboardTeamAdapter.updateTeam(player, teamName, "", "", java.util.Collections.singletonList(entryName),
                    0);
        }

        viewCache.put(player.getUniqueId(), new FakeSlot[80]);
    }

    /**
     * Sends the packet to remove real players from the tablist for a specific
     * viewer.
     */
    private void removeRealPlayers(Player viewer) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PacketLayoutAdapter.removePlayer(viewer, player.getUniqueId());
        }
    }

    /**
     * Updates the text for a specific slot by modifying the Scoreboard Team
     * prefix/suffix.
     */
    private void updateSlot(Player player, int slot, String text, SkinManager.Skin skin) {
        if (slot < 1 || slot > 80)
            return;

        String teamName = getTeamName(slot);

        // Minecraft 1.8 has a 16-character limit for prefixes and suffixes.
        // We split long strings so they can display up to 32 characters total.
        String prefix = text;
        String suffix = "";

        if (text.length() > 16) {
            prefix = text.substring(0, 16);
            if (prefix.endsWith("\u00a7")) { // Don't split in the middle of a color code
                prefix = prefix.substring(0, 15);
                suffix = text.substring(15);
            } else {
                suffix = text.substring(16);
            }

            // In 1.8, the color from the prefix doesn't carry over to the suffix
            // automatically.
            // We need to find the last color code in the prefix and prepend it to the
            // suffix.
            String lastColors = org.bukkit.ChatColor.getLastColors(prefix);
            suffix = lastColors + suffix;

            if (suffix.length() > 16) {
                suffix = suffix.substring(0, 16); // Hard cap for compatibility
            }
        }

        ScoreboardTeamAdapter.updateTeam(player, teamName, prefix, suffix, null, 2);
    }

    // Generates a sorted team name (like !001, !002) to keep the tab list ordered
    private String getTeamName(int slot) {
        return String.format("!%03d", slot);
    }

    // Generates a unique invisible "player name" using color codes
    private String getEntryName(int slot) {
        return org.bukkit.ChatColor.values()[slot % 10].toString()
                + org.bukkit.ChatColor.values()[(slot / 10) % 10].toString() + org.bukkit.ChatColor.RESET;
    }

    // Just a container for slot data if we need to expand it later
    public static class FakeSlot {
        String originalText;
        UUID uuid;
    }
}
