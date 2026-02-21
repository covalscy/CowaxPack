package com.cowax.cowaxpack.client;

import com.cowax.cowaxpack.init.ModItems;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ArtilleryIndicatorMouseHandler {

    /**
     * Изменяет чувствительность мыши при использовании артиллерийского индикатора
     * Вызывается из mixin или события
     */
    public static double changeSensitivity(double original) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) return original;

        // Уменьшаем чувствительность при использовании артиллерийского индикатора
        // if (player.isUsingItem() && player.getUseItem().is(ModItems.ARTILLERY_INDICATOR.get()) && mc.options.getCameraType() == CameraType.FIRST_PERSON) {
        if (false && player.isUsingItem() && mc.options.getCameraType() == CameraType.FIRST_PERSON) {
            return original / Math.max(1 + 0.2 * ArtilleryIndicatorClientHandler.artilleryIndicatorZoom, 0.1);
        }

        return original;
    }
}
