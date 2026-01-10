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

    private void createFile() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(fileName, false);
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(file);
    }

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
