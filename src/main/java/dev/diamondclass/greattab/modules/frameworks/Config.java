package dev.diamondclass.greattab.modules.frameworks;

import dev.diamondclass.greattab.Plugin;
import dev.diamondclass.greattab.utils.CC;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class handles everything related to .yml files.
 * It doesn't just load data; it also automatically handles global placeholders,
 * prefixes, and animations whenever you request a string.
 */
@Getter
public class Config {

    private final Plugin plugin;
    private final String fileName;
    private final File file;
    private FileConfiguration configuration;

    public Config(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.file = new File(plugin.getDataFolder(), fileName);
        this.createFile();
    }

    // If the file doesn't exist, we'll create it using the resource bundled in the
    // .jar
    private void createFile() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(fileName, false);
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    // Saves any changes we've made in memory back to the physical file
    public void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Reloads the file from disk to refresh the memory with new data
    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Gets a string and applies all the "magic" processing:
     * - Replaces %prefix% with the global prefix.
     * - Replaces custom variables defined in config.yml.
     * - Processes animations if the AnimationManager is ready.
     * - Translates colors (including Hex).
     */
    public String getString(String path) {
        String value = configuration.getString(path, "");
        if (value.contains("%prefix%")) {
            value = value.replace("%prefix%", plugin.getSettingsConfig().getConfiguration().getString("prefix", ""));
        }

        if (plugin.getSettingsConfig().getConfiguration().contains("variables")) {
            for (String key : plugin.getSettingsConfig().getConfiguration().getConfigurationSection("variables")
                    .getKeys(false)) {
                if (value.contains(key)) {
                    value = value.replace(key,
                            plugin.getSettingsConfig().getConfiguration().getString("variables." + key));
                }
            }
        }

        if (plugin.getAnimationManager() != null) {
            value = plugin.getAnimationManager().parseAnimations(value);
        }

        return CC.translate(value);
    }

    public int getInt(String path) {
        return configuration.getInt(path);
    }

    public boolean getBoolean(String path) {
        return configuration.getBoolean(path);
    }

    // Same as getString, but for lists of text (handy for tablist content)
    public List<String> getStringList(String path) {
        return configuration.getStringList(path).stream()
                .map(line -> {
                    if (line.contains("%prefix%")) {
                        line = line.replace("%prefix%",
                                plugin.getSettingsConfig().getConfiguration().getString("prefix", ""));
                    }

                    if (plugin.getSettingsConfig().getConfiguration().contains("variables")) {
                        for (String key : plugin.getSettingsConfig().getConfiguration()
                                .getConfigurationSection("variables").getKeys(false)) {
                            if (line.contains(key)) {
                                line = line.replace(key, plugin.getSettingsConfig().getConfiguration()
                                        .getString("variables." + key));
                            }
                        }
                    }

                    if (plugin.getAnimationManager() != null) {
                        line = plugin.getAnimationManager().parseAnimations(line);
                    }

                    return CC.translate(line);
                })
                .collect(Collectors.toList());
    }

}
