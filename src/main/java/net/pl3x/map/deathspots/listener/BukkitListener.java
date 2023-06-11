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

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.pl3x.map.deathspots.DeathSpots;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

public class BukkitListener implements Listener {
    private final DeathSpots plugin;

    public BukkitListener(DeathSpots plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.hasPermission("deathmap.on.death")) {
            ItemStack deathMap = getDeathMap(player);
            Bukkit.getScheduler().runTaskLater(this.plugin, () ->
                    player.getInventory().addItem(deathMap), 1L);
        }
    }

    private ItemStack getDeathMap(Player player) {
        Location loc = player.getLocation();
        BlockPos pos = new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        ServerLevel level = ((CraftWorld) loc.getWorld()).getHandle();

        net.minecraft.world.item.ItemStack nmsMap = MapItem.create(level, pos.getX(), pos.getZ(), (byte) MapView.Scale.CLOSEST.ordinal(), true, true);
        MapItem.renderBiomePreviewMap(level, nmsMap);
        MapItemSavedData.addTargetDecoration(nmsMap, pos, "Death", MapDecoration.Type.RED_X);

        CompoundTag displayTag = nmsMap.getOrCreateTagElement("display");
        ListTag lore = new ListTag();
        lore.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("World: " + loc.getWorld().getName()).withStyle(ChatFormatting.GRAY))));
        displayTag.put("Lore", lore);
        displayTag.putString("Name", Component.Serializer.toJson(Component.literal("Death Map").withStyle(ChatFormatting.RED)));

        return CraftItemStack.asCraftMirror(nmsMap);
    }
}
