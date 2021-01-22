package net.pl3x.map.deathspots.configuration;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class WorldConfig {
    private static final Map<UUID, WorldConfig> configs = new HashMap<>();

    public static void reload() {
        configs.clear();
        Bukkit.getWorlds().forEach(world -> {
            final WorldConfig config = new WorldConfig(world);
            configs.put(world.getUID(), config);
        });
    }

    public static WorldConfig get(World world) {
        return get(world.getUID());
    }

    public static WorldConfig get(UUID uuid) {
        return configs.get(uuid);
    }

    private final String worldName;

    public WorldConfig(World world) {
        this.worldName = world.getName();
        init();
    }

    public void init() {
        Config.readConfig(WorldConfig.class, this);
    }

    private void set(String path, Object val) {
        Config.CONFIG.addDefault("world-settings.default." + path, val);
        Config.CONFIG.set("world-settings.default." + path, val);
        if (Config.CONFIG.get("world-settings." + worldName + "." + path) != null) {
            Config.CONFIG.addDefault("world-settings." + worldName + "." + path, val);
            Config.CONFIG.set("world-settings." + worldName + "." + path, val);
        }
    }

    private boolean getBoolean(String path, boolean def) {
        Config.CONFIG.addDefault("world-settings.default." + path, def);
        return Config.CONFIG.getBoolean("world-settings." + worldName + "." + path, Config.CONFIG.getBoolean("world-settings.default." + path));
    }

    private int getInt(String path, int def) {
        Config.CONFIG.addDefault("world-settings.default." + path, def);
        return Config.CONFIG.getInt("world-settings." + worldName + "." + path, Config.CONFIG.getInt("world-settings.default." + path));
    }

    private String getString(String path, String def) {
        Config.CONFIG.addDefault("world-settings.default." + path, def);
        return Config.CONFIG.getString("world-settings." + worldName + "." + path, Config.CONFIG.getString("world-settings.default." + path));
    }

    public boolean ENABLED = true;
    public int UPDATE_INTERVAL = 5;
    public boolean ENABLE_CONTROLS = true;
    public boolean CONTROLS_HIDDEN_BY_DEFAULT = false;
    public String TOOLTIP = "{name}'s<br/>Death Spot";
    public int REMOVE_MARKER_AFTER = 60 * 5;

    private void worldSettings() {
        ENABLED = getBoolean("enabled", ENABLED);
        UPDATE_INTERVAL = getInt("update-interval", UPDATE_INTERVAL);
        ENABLE_CONTROLS = getBoolean("controls.enabled", ENABLE_CONTROLS);
        CONTROLS_HIDDEN_BY_DEFAULT = getBoolean("controls.hidden-by-default", CONTROLS_HIDDEN_BY_DEFAULT);
        TOOLTIP = getString("marker.tooltip", TOOLTIP);
        REMOVE_MARKER_AFTER = getInt("marker.remove-after", REMOVE_MARKER_AFTER);
    }
}
