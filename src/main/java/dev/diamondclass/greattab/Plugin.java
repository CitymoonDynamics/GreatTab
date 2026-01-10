package dev.diamondclass.greattab;

import org.bukkit.plugin.java.JavaPlugin;
import dev.diamondclass.greattab.utils.CC;

public class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info(CC.translate("&7&m-----------------------------"));
        getLogger().info(CC.translate("&b&lGreatTabs"));
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
        getLogger().info(CC.translate("&b&lGreatTabs"));
        getLogger().info(CC.translate("&7"));
        getLogger().info(CC.translate("&7Author: " + this.getDescription().getAuthors()));
        getLogger().info(CC.translate("&7Version: " + this.getDescription().getVersion()));
        getLogger().info(CC.translate("&7"));
        getLogger().info(CC.translate("&7The plugin has been &cdisabled&7!"));
        getLogger().info(CC.translate("&7&m-----------------------------"));
    }
}
