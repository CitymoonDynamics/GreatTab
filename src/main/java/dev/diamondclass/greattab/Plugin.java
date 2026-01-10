package dev.diamondclass.greattab;

import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("GreatTabs has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("GreatTabs has been disabled!");
    }
}
