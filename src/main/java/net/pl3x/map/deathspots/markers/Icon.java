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
