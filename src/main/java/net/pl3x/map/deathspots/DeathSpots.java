package net.pl3x.map.deathspots;

import net.pl3x.map.api.Pair;
import net.pl3x.map.deathspots.configuration.Config;
import net.pl3x.map.deathspots.hook.Pl3xMapHook;
import net.pl3x.map.deathspots.listener.PlayerListener;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DeathSpots extends JavaPlugin {
    private static DeathSpots instance;
    private final Map<UUID, Pair<String, Location>> deathSpots = new HashMap<>();
    private Pl3xMapHook pl3xmapHook;

    public DeathSpots() {
        instance = this;
    }

    @Override
    public void onEnable() {
        Config.reload();

        if (!new File(getDataFolder(), "icon.png").exists()) {
            saveResource("icon.png", false);
        }

        if (!getServer().getPluginManager().isPluginEnabled("Pl3xMap")) {
            Logger.severe("Pl3xMap not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        pl3xmapHook = new Pl3xMapHook(this);
    }

    @Override
    public void onDisable() {
        if (pl3xmapHook != null) {
            pl3xmapHook.disable();
        }
        deathSpots.clear();
    }

    public static DeathSpots getInstance() {
        return instance;
    }

    public Map<UUID, Pair<String, Location>> getDeathSpots() {
        return deathSpots;
    }
}
