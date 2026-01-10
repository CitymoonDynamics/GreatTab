package dev.diamondclass.greattab.modules.tablist;

import dev.diamondclass.greattab.Plugin;
import dev.diamondclass.greattab.modules.frameworks.Config;
import lombok.Getter;
import org.bukkit.Bukkit;

/**
 * This manager takes care of the high-level tablist state.
 * It handles the configuration and the background task that keeps the tab
 * update ticking.
 */
public class TabManager {

    private final Plugin plugin;
    @Getter
    private Config tabConfig;
    private TabTask tabTask;

    public TabManager(Plugin plugin) {
        this.plugin = plugin;
        this.load();
    }

    // Loads up the tab.yml file and kicks off the refresh task
    public void load() {
        this.tabConfig = new Config(plugin, "tab.yml");
        this.startTask();
    }

    // Standard reload logic
    public void reload() {
        this.tabConfig.reload();
        this.startTask();
    }

    // Stops any old task and starts a fresh one if the tab is enabled in the config
    private void startTask() {
        if (tabTask != null) {
            tabTask.cancel();
        }

        if (tabConfig.getBoolean("enable")) {
            tabTask = new TabTask(plugin);
            // We run it asynchronously to avoid lagging the main server thread
            tabTask.runTaskTimerAsynchronously(plugin, 0L, tabConfig.getInt("update-interval"));
        }
    }
}
