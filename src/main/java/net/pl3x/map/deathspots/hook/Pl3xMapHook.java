package net.pl3x.map.deathspots.hook;

import net.pl3x.map.api.Key;
import net.pl3x.map.api.Pl3xMapProvider;
import net.pl3x.map.api.SimpleLayerProvider;
import net.pl3x.map.deathspots.DeathSpots;
import net.pl3x.map.deathspots.Logger;
import net.pl3x.map.deathspots.configuration.WorldConfig;
import net.pl3x.map.deathspots.task.Pl3xMapTask;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class Pl3xMapHook {
    public static final Key deathSpotIconKey = Key.of("deathspot_icon");

    private final DeathSpots plugin;
    private final Map<UUID, Pl3xMapTask> provider = new HashMap<>();

    public Pl3xMapHook(DeathSpots plugin) {
        this.plugin = plugin;

        try {
            Pl3xMapProvider.get().iconRegistry().register(deathSpotIconKey, ImageIO.read(new File(plugin.getDataFolder(), "icon.png")));
        } catch (IOException e) {
            Logger.log().log(Level.WARNING, "Failed to register deathspot icon", e);
        }

        Pl3xMapProvider.get().mapWorlds().forEach(world -> {
            WorldConfig worldConfig = WorldConfig.get(world.uuid());
            if (worldConfig.ENABLED) {
                SimpleLayerProvider provider = SimpleLayerProvider.builder("DeathSpots")
                        .showControls(worldConfig.ENABLE_CONTROLS)
                        .defaultHidden(worldConfig.CONTROLS_HIDDEN_BY_DEFAULT)
                        .build();
                world.layerRegistry().register(Key.of("deathspots_" + world.uuid()), provider);
                Pl3xMapTask task = new Pl3xMapTask(plugin, world, provider);
                task.runTaskTimerAsynchronously(plugin, 0, 20L * worldConfig.UPDATE_INTERVAL);
                this.provider.put(world.uuid(), task);
            }
        });
    }

    public void disable() {
        provider.values().forEach(Pl3xMapTask::disable);
        provider.clear();
    }
}
