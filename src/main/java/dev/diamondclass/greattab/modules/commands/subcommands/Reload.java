package dev.diamondclass.greattab.modules.commands.subcommands;

import dev.diamondclass.greattab.Plugin;
import dev.diamondclass.greattab.utils.CC;
import org.bukkit.command.CommandSender;

public class Reload {

    private final Plugin plugin;

    public Reload(Plugin plugin) {
        this.plugin = plugin;
    }

    public void execute(CommandSender sender) {
        if (!sender.hasPermission("greattab.admin")) {
            sender.sendMessage(plugin.getLanguageConfig().getString("no-permission"));
            return;
        }

        plugin.getSettingsConfig().reload();
        plugin.getLanguageConfig().reload();
        plugin.getAnimationManager().loadAnimations();
        plugin.getTabManager().reload();
        plugin.getLayoutManager().reload();

        sender.sendMessage(plugin.getLanguageConfig().getString("reload.success"));
    }
}
