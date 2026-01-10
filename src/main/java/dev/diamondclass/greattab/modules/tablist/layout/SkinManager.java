package dev.diamondclass.greattab.modules.tablist.layout;

import dev.diamondclass.greattab.modules.frameworks.Config;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class SkinManager {

    private static final Map<String, Skin> skins = new HashMap<>();

    public static void load(Config config) {
        skins.clear();

        // We load the 'SKINS' section from the new layout in tab.yml
        if (config.getConfiguration().contains("layout.SKINS")) {
            for (String key : config.getConfiguration().getConfigurationSection("layout.SKINS").getKeys(false)) {
                String value = config.getConfiguration().getString("layout.SKINS." + key + ".VALUE");
                String signature = config.getConfiguration().getString("layout.SKINS." + key + ".SIGNATURE");
                skins.put(key.toUpperCase(), new Skin(value, signature));
            }
        }
    }

    public static Skin getSkin(String name) {
        // We default to LIGHT_GRAY since that's what we provided in the config
        return skins.getOrDefault(name.toUpperCase(), skins.get("LIGHT_GRAY"));
    }

    @Getter
    @AllArgsConstructor
    public static class Skin {
        private String value;
        private String signature;
    }
}
