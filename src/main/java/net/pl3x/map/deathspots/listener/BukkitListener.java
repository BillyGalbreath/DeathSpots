/*
 * MIT License
 *
 * Copyright (c) 2020-2023 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.pl3x.map.deathspots.listener;

import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.event.EventHandler;
import net.pl3x.map.core.event.EventListener;
import net.pl3x.map.core.event.server.Pl3xMapEnabledEvent;
import net.pl3x.map.core.event.server.ServerLoadedEvent;
import net.pl3x.map.core.event.world.WorldLoadedEvent;
import net.pl3x.map.core.event.world.WorldUnloadedEvent;
import net.pl3x.map.core.registry.Registry;
import net.pl3x.map.core.world.World;
import net.pl3x.map.deathspots.configuration.WorldConfig;
import net.pl3x.map.deathspots.markers.DeathLayer;
import net.pl3x.map.deathspots.markers.DeathSpot;
import net.pl3x.map.deathspots.markers.Icon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

public class BukkitListener implements EventListener, Listener {
    public static final Registry<DeathSpot> deathSpots = new Registry<>();

    public BukkitListener() {
        Pl3xMap.api().getEventRegistry().register(this);
    }

    @org.bukkit.event.EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();

        World world = Pl3xMap.api().getWorldRegistry().get(player.getWorld().getName());
        if (world == null || !world.isEnabled()) {
            return;
        }

        DeathLayer layer = (DeathLayer) world.getLayerRegistry().get(DeathLayer.KEY);
        if (layer == null) {
            return;
        }

        deathSpots.register(new DeathSpot(player));
    }

    @EventHandler
    public void onPl3xMapEnabled(@NotNull Pl3xMapEnabledEvent event) {
        Icon.register();
    }

    @EventHandler
    public void onServerLoaded(@NotNull ServerLoadedEvent event) {
        Icon.register();
        Pl3xMap.api().getWorldRegistry().forEach(this::registerWorld);
    }

    @EventHandler
    public void onWorldLoaded(@NotNull WorldLoadedEvent event) {
        registerWorld(event.getWorld());
    }

    @EventHandler
    public void onWorldUnloaded(@NotNull WorldUnloadedEvent event) {
        try {
            event.getWorld().getLayerRegistry().unregister(DeathLayer.KEY);
        } catch (Throwable ignore) {
        }
    }

    private void registerWorld(@NotNull World world) {
        world.getLayerRegistry().register(new DeathLayer(new WorldConfig(world)));
    }
}
