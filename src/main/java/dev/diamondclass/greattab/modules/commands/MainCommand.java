package dev.diamondclass.greattab.modules.commands;

import dev.diamondclass.greattab.Plugin;
import dev.diamondclass.greattab.modules.commands.subcommands.Reload;
import dev.diamondclass.greattab.utils.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommand implements CommandExecutor {

    private final Plugin plugin;
    private final Reload reloadSubcommand;

    public MainCommand(Plugin plugin) {
        this.plugin = plugin;
        this.reloadSubcommand = new Reload(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            plugin.getLanguageConfig().getStringList("help").forEach(sender::sendMessage);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            reloadSubcommand.execute(sender);
            return true;
        }

        sender.sendMessage(plugin.getLanguageConfig().getString("unknown-subcommand"));
        return true;

    }
}
