package dev.diamondclass.greattab;

import org.bukkit.plugin.java.JavaPlugin;
import dev.diamondclass.greattab.utils.CC;
import dev.diamondclass.greattab.modules.frameworks.CommandManager;
import dev.diamondclass.greattab.modules.frameworks.Config;
import dev.diamondclass.greattab.modules.frameworks.animations.AnimationManager;
import lombok.Getter;

@Getter
public class Plugin extends JavaPlugin {

    private Config settingsConfig;
    private Config languageConfig;
    private CommandManager commandManager;
    private AnimationManager animationManager;

    @Override
    public void onEnable() {
        this.settingsConfig = new Config(this, "config.yml");
        this.languageConfig = new Config(this, "language.yml");
        this.animationManager = new AnimationManager(this);
        this.commandManager = new CommandManager(this);

        getLogger().info(CC.translate("&7&m-----------------------------"));
        getLogger().info(CC.translate("&b&lGreatTab"));
        getLogger().info(CC.translate("&7"));
        getLogger().info(CC.translate("&7Author: " + this.getDescription().getAuthors()));
        getLogger().info(CC.translate("&7Version: " + this.getDescription().getVersion()));
        getLogger().info(CC.translate("&7"));
        getLogger().info(CC.translate("&7The plugin has been &aenabled&7!"));
        getLogger().info(CC.translate("&7&m-----------------------------"));
    }

    @Override
    public void onDisable() {
        getLogger().info(CC.translate("&7&m-----------------------------"));
        getLogger().info(CC.translate("&b&lGreatTab"));
        getLogger().info(CC.translate("&7"));
        getLogger().info(CC.translate("&7Author: " + this.getDescription().getAuthors()));
        getLogger().info(CC.translate("&7Version: " + this.getDescription().getVersion()));
        getLogger().info(CC.translate("&7"));
        getLogger().info(CC.translate("&7The plugin has been &cdisabled&7!"));
        getLogger().info(CC.translate("&7&m-----------------------------"));
    }
}
