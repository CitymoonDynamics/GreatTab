package dev.diamondclass.greattab.modules.frameworks.animations;

import dev.diamondclass.greattab.Plugin;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This manager takes care of custom animations defined in the 'animations'
 * folder.
 * It lets you create dynamic, scrolling, or blinking text for your tablist.
 */
public class AnimationManager {

    private final Plugin plugin;
    // Stores all loaded animations, mapped by their unique name.
    @Getter
    private final Map<String, Animation> animations = new HashMap<>();

    /**
     * Constructs a new AnimationManager.
     * 
     * @param plugin The main plugin instance.
     */
    public AnimationManager(Plugin plugin) {
        this.plugin = plugin;
        // Immediately load all animations when the manager is created.
        this.loadAnimations();
    }

    /**
     * Scans the 'animations' directory for .yml files and loads them.
     * Each key in the YAML becomes the name of an animation.
     */
    public void loadAnimations() {
        // Clear existing animations to prepare for a fresh load (e.g., on reload).
        animations.clear();

        // Define the folder where animation configuration files are stored.
        File folder = new File(plugin.getDataFolder(), "animations");
        if (!folder.exists()) {
            folder.mkdirs();
            // We provide a default example so users know how it works
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

    /**
     * Replaces animation tags like '<animation:title>' with the current frame of
     * that animation.
     */
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
