package dev.diamondclass.greattab;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import dev.diamondclass.greattab.utils.CC;
import dev.diamondclass.greattab.modules.frameworks.CommandManager;
import dev.diamondclass.greattab.modules.frameworks.Config;
import dev.diamondclass.greattab.modules.frameworks.animations.AnimationManager;
import dev.diamondclass.greattab.modules.tablist.TabManager;
import lombok.Getter;

/**
 * This is the heart of the plugin.
 * Everything starts here, and it's where we keep track of all the managers
 * that handle the different systems like the tablist and animations.
 */
@Getter
public class Plugin extends JavaPlugin {

    // Global configurations
    private Config settingsConfig;
    private Config languageConfig;

    // Managers for commands, animations, and the tablist itself
    private CommandManager commandManager;
    private AnimationManager animationManager;
    private TabManager tabManager;
    private dev.diamondclass.greattab.modules.tablist.layout.LayoutManager layoutManager;

    @Override
    public void onEnable() {
        // First, we load up the config files so they're ready for the other managers
        this.settingsConfig = new Config(this, "config.yml");
        this.languageConfig = new Config(this, "language.yml");

        // Now we initialize the core logic managers
        this.animationManager = new AnimationManager(this);
        this.tabManager = new TabManager(this);
        this.layoutManager = new dev.diamondclass.greattab.modules.tablist.layout.LayoutManager(this);

        // CommandManager usually goes last so it can access everything we just set up
        this.commandManager = new CommandManager(this);

        this.layoutManager.load();

        // Just a nice little console message to show the plugin is up and running
        Bukkit.getConsoleSender().sendMessage(CC.translate("&7&m-----------------------------"));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&b&lGreatTab"));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&7"));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&7Author: " + this.getDescription().getAuthors()));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&7Version: " + this.getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&7"));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&7The plugin has been &aenabled&7!"));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&7&m-----------------------------"));
    }

    @Override
    public void onDisable() {
        // Clean shutdown message
        Bukkit.getConsoleSender().sendMessage(CC.translate("&7&m-----------------------------"));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&b&lGreatTab"));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&7"));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&7Author: " + this.getDescription().getAuthors()));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&7Version: " + this.getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&7"));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&7The plugin has been &cdisabled&7!"));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&7&m-----------------------------"));
    }
}
