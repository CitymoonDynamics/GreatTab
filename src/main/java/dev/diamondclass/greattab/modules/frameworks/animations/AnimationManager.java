package dev.diamondclass.greattab.modules.frameworks.animations;

import dev.diamondclass.greattab.Plugin;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AnimationManager {

    private final Plugin plugin;
    @Getter
    private final Map<String, Animation> animations = new HashMap<>();

    public AnimationManager(Plugin plugin) {
        this.plugin = plugin;
        this.loadAnimations();
    }

    public void loadAnimations() {
        animations.clear();

        File folder = new File(plugin.getDataFolder(), "animations");
        if (!folder.exists()) {
            folder.mkdirs();
            plugin.saveResource("animations/example.yml", false);
        }

        File[] files = folder.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            if (!file.getName().endsWith(".yml"))
                continue;

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (String key : config.getKeys(false)) {
                Animation animation = new Animation(
                        key,
                        config.getStringList(key + ".frames"),
                        config.getInt(key + ".interval"));
                animations.put(key, animation);
            }
        }

        plugin.getLogger().info("Loaded " + animations.size() + " animations.");
    }

    public String parseAnimations(String text) {
        if (text == null)
            return "";

        for (Animation animation : animations.values()) {
            String placeholder = "<animation:" + animation.getName() + ">";
            if (text.contains(placeholder)) {
                text = text.replace(placeholder, animation.getCurrentFrame());
            }
        }
        return text;
    }
}
