package dev.diamondclass.greattab.modules.frameworks;

import dev.diamondclass.greattab.Plugin;
import dev.diamondclass.greattab.modules.commands.MainCommand;
import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

@Getter
public class CommandManager {

    private final Plugin plugin;

    public CommandManager(Plugin plugin) {
        this.plugin = plugin;
        this.registerCommands();
    }

    private void registerCommands() {
        this.registerCommand("greattab", new MainCommand(plugin));
    }

    private void registerCommand(String name, CommandExecutor executor) {
        PluginCommand command = plugin.getCommand(name);
        if (command != null) {
            command.setExecutor(executor);
        }
    }
}
