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
package net.pl3x.map.deathspots.markers;

import java.util.Objects;
import java.util.UUID;
import net.pl3x.map.core.Keyed;
import net.pl3x.map.core.markers.Point;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeathSpot extends Keyed {
    private final UUID uuid;
    private final Point point;
    private final long time;

    public DeathSpot(@NotNull Player player) {
        super(player.getName());

        Location loc = player.getLocation();
        this.uuid = player.getUniqueId();
        this.point = Point.of(loc.getX(), loc.getZ());
        this.time = System.currentTimeMillis();
    }

    public @NotNull UUID getUUID() {
        return this.uuid;
    }

    public @NotNull String getName() {
        return getKey();
    }

    public @NotNull Point getPoint() {
        return this.point;
    }

    public long getTime() {
        return this.time;
    }

    public boolean expired(int seconds) {
        return getTime() + (seconds * 1000L) < System.currentTimeMillis();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        DeathSpot other = (DeathSpot) o;
        return getUUID().equals(other.getUUID()) &&
                getName().equals(other.getName()) &&
                getPoint().equals(other.getPoint());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUUID(), getName(), getPoint());
    }

    @Override
    public @NotNull String toString() {
        return "DeathSpot{"
                + "uuid=" + getUUID()
                + ",name=" + getName()
                + ",point=" + getPoint()
                + "}";
    }
}
