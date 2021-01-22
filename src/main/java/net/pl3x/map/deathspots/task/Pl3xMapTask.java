package net.pl3x.map.deathspots.task;

import net.pl3x.map.api.Key;
import net.pl3x.map.api.MapWorld;
import net.pl3x.map.api.Point;
import net.pl3x.map.api.SimpleLayerProvider;
import net.pl3x.map.api.marker.Icon;
import net.pl3x.map.api.marker.Marker;
import net.pl3x.map.api.marker.MarkerOptions;
import net.pl3x.map.deathspots.DeathSpots;
import net.pl3x.map.deathspots.configuration.WorldConfig;
import net.pl3x.map.deathspots.hook.Pl3xMapHook;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Pl3xMapTask extends BukkitRunnable {
    private final DeathSpots plugin;
    private final MapWorld world;
    private final SimpleLayerProvider provider;
    private final WorldConfig worldConfig;

    private boolean stop;

    public Pl3xMapTask(DeathSpots plugin, MapWorld world, SimpleLayerProvider provider) {
        this.plugin = plugin;
        this.world = world;
        this.provider = provider;
        this.worldConfig = WorldConfig.get(world.uuid());
    }

    @Override
    public void run() {
        if (stop) {
            cancel();
        }

        provider.clearMarkers();

        plugin.getDeathSpots().forEach((uuid, pair) -> {
            if (pair.right().getWorld().getUID().equals(this.world.uuid())) {
                this.handle(uuid, pair.left(), pair.right());
            }
        });
    }

    private void handle(UUID uuid, String name, Location location) {
        String worldName = location.getWorld().getName();

        Icon icon = Marker.icon(Point.fromLocation(location), Pl3xMapHook.deathSpotIconKey, 16);

        icon.markerOptions(MarkerOptions.builder()
                .hoverTooltip(worldConfig.TOOLTIP
                        .replace("{name}", name)));

        String markerid = "deathspots_" + worldName + "_player_" + uuid;
        this.provider.addMarker(Key.of(markerid), icon);
    }

    public void disable() {
        cancel();
        this.stop = true;
        this.provider.clearMarkers();
    }
}
