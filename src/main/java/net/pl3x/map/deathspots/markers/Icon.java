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

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageIO;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.image.IconImage;
import net.pl3x.map.deathspots.DeathSpots;
import org.jetbrains.annotations.NotNull;

public enum Icon {
    MARKER, SHADOW;

    private final String name;
    private final String key;

    Icon() {
        this.name = name().toLowerCase(Locale.ROOT);
        this.key = String.format("%s_%s", DeathLayer.KEY, this.name);
    }

    public @NotNull String getKey() {
        return this.key;
    }

    public static void register() {
        DeathSpots plugin = DeathSpots.getPlugin(DeathSpots.class);
        for (Icon icon : values()) {
            String filename = String.format("icons%s%s.png", File.separator, icon.name);
            File file = new File(plugin.getDataFolder(), filename);
            if (!file.exists()) {
                plugin.saveResource(filename, false);
            }
            try {
                Pl3xMap.api().getIconRegistry().register(new IconImage(icon.key, ImageIO.read(file), "png"));
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to register icon (" + icon.name + ") " + filename);
                e.printStackTrace();
            }
        }
    }
}
