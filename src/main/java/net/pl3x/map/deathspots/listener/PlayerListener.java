package net.pl3x.map.deathspots.listener;

import net.pl3x.map.api.Pair;
import net.pl3x.map.deathspots.DeathSpots;
import net.pl3x.map.deathspots.configuration.WorldConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PlayerListener implements Listener {
    private final DeathSpots plugin;

    public PlayerListener(DeathSpots plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final UUID uuid = player.getUniqueId();
        final Location location = player.getLocation();

        WorldConfig worldConfig = WorldConfig.get(location.getWorld());
        if (!worldConfig.ENABLED) {
            return;
        }

        plugin.getDeathSpots().put(uuid, Pair.of(player.getName(), location));

        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getDeathSpots().remove(uuid);
            }
        }.runTaskLater(plugin, 20L * worldConfig.REMOVE_MARKER_AFTER);
    }
}
