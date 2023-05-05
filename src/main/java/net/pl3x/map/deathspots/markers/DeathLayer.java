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

import java.util.Collection;
import java.util.stream.Collectors;
import libs.org.checkerframework.checker.nullness.qual.NonNull;
import net.pl3x.map.core.markers.layer.WorldLayer;
import net.pl3x.map.core.markers.marker.Icon;
import net.pl3x.map.core.markers.marker.Marker;
import net.pl3x.map.core.markers.option.Options;
import net.pl3x.map.core.registry.Registry;
import net.pl3x.map.deathspots.DeathSpots;
import net.pl3x.map.deathspots.configuration.WorldConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DeathLayer extends WorldLayer {
    public static final String KEY = "deathspots";

    private final WorldConfig config;
    private final Options options;

    private final Registry<DeathSpot> spots = new Registry<>();

    public DeathLayer(@NonNull WorldConfig config) {
        super(KEY, config.getWorld(), () -> config.LAYER_LABEL);
        this.config = config;

        setShowControls(config.LAYER_SHOW_CONTROLS);
        setDefaultHidden(config.LAYER_DEFAULT_HIDDEN);
        setUpdateInterval(config.LAYER_UPDATE_INTERVAL);
        setPriority(config.LAYER_PRIORITY);
        setZIndex(config.LAYER_ZINDEX);

        this.options = new Options.Builder()
                .tooltipPane(config.ICON_TOOLTIP_PANE)
                .tooltipOffset(config.ICON_TOOLTIP_OFFSET)
                .tooltipDirection(config.ICON_TOOLTIP_DIRECTION)
                .tooltipPermanent(config.ICON_TOOLTIP_PERMANENT)
                .tooltipSticky(config.ICON_TOOLTIP_STICKY)
                .tooltipOpacity(config.ICON_TOOLTIP_OPACITY)
                .popupPane(config.ICON_POPUP_PANE)
                .popupOffset(config.ICON_POPUP_OFFSET)
                .popupMaxWidth(config.ICON_POPUP_MAX_WIDTH)
                .popupMinWidth(config.ICON_POPUP_MIN_WIDTH)
                .popupMaxHeight(config.ICON_POPUP_MAX_HEIGHT)
                .popupShouldAutoPan(config.ICON_POPUP_SHOULD_AUTO_PAN)
                .popupAutoPanPaddingTopLeft(config.ICON_POPUP_AUTO_PAN_PADDING_TOP_LEFT)
                .popupAutoPanPaddingBottomRight(config.ICON_POPUP_AUTO_PAN_PADDING_BOTTOM_RIGHT)
                .popupAutoPanPadding(config.ICON_POPUP_AUTO_PAN_PADDING)
                .popupShouldKeepInView(config.ICON_POPUP_SHOULD_KEEP_IN_VIEW)
                .popupCloseButton(config.ICON_POPUP_CLOSE_BUTTON)
                .popupShouldAutoClose(config.ICON_POPUP_SHOULD_AUTO_CLOSE)
                .popupShouldCloseOnEscapeKey(config.ICON_POPUP_SHOULD_CLOSE_ON_ESCAPE_KEY)
                .popupShouldCloseOnClick(config.ICON_POPUP_SHOULD_CLOSE_ON_CLICK)
                .build();

        Bukkit.getScheduler().runTaskTimerAsynchronously(DeathSpots.getPlugin(DeathSpots.class), () ->
                this.spots.values().removeIf(next -> next.expired(config.SECONDS_TO_SHOW)), 20L, 20L);
    }

    @Override
    public @NonNull Collection<@NonNull Marker<@NonNull ?>> getMarkers() {
        return this.spots.values().stream().map(spot -> {
            Icon icon = Marker.icon(KEY + "_" + spot.getName(), spot.getPoint(), KEY + "marker", this.config.ICON_SIZE)
                    .setAnchor(config.ICON_ANCHOR)
                    .setRotationAngle(config.ICON_ROTATION_ANGLE)
                    .setRotationOrigin(config.ICON_ROTATION_ORIGIN)
                    .setShadow(KEY + "shadow")
                    .setShadowSize(config.ICON_SHADOW_SIZE)
                    .setShadowAnchor(config.ICON_SHADOW_ANCHOR);
            Options.Builder builder = this.options.asBuilder();
            if (config.ICON_POPUP_CONTENT != null) {
                builder.popupContent(config.ICON_POPUP_CONTENT.replace("<player>", spot.getName()));
            }
            if (config.ICON_TOOLTIP_CONTENT != null) {
                builder.tooltipContent(config.ICON_TOOLTIP_CONTENT.replace("<player>", spot.getName()));
            }
            return icon.setOptions(builder.build());
        }).collect(Collectors.toList());
    }

    public void addDeath(Player player) {
        this.spots.register(new DeathSpot(player));
    }
}
