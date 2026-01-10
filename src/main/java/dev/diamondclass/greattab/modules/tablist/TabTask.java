package dev.diamondclass.greattab.modules.tablist;

import dev.diamondclass.greattab.Plugin;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class TabTask extends BukkitRunnable {

    private final Plugin plugin;

    public TabTask(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (Bukkit.getOnlinePlayers().isEmpty())
            return;

        List<String> headerList = plugin.getTabManager().getTabConfig().getStringList("header");
        List<String> footerList = plugin.getTabManager().getTabConfig().getStringList("footer");

        if (headerList.isEmpty() && footerList.isEmpty())
            return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            StringBuilder headerBuilder = new StringBuilder();
            for (int i = 0; i < headerList.size(); i++) {
                headerBuilder.append(headerList.get(i));
                if (i < headerList.size() - 1) {
                    headerBuilder.append("\n");
                }
            }

            StringBuilder footerBuilder = new StringBuilder();
            for (int i = 0; i < footerList.size(); i++) {
                footerBuilder.append(footerList.get(i));
                if (i < footerList.size() - 1) {
                    footerBuilder.append("\n");
                }
            }

            String header = headerBuilder.toString();
            String footer = footerBuilder.toString();

            // PAPI is already handled in getStringList if using Config.java correctly?
            // Wait, Config.java handles variables and animations, but mostly translation.
            // It parses variables/animations but NOT PAPI if not explicitly added.
            // Let's check Config.java again or just apply PAPI here.

            // Applying PAPI
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                header = PlaceholderAPI.setPlaceholders(player, header);
                footer = PlaceholderAPI.setPlaceholders(player, footer);
            }

            TabAdapter.sendHeaderFooter(player, header, footer);
        }
    }
}
