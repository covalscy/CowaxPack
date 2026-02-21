package com.cowax.cowaxpack.client;

import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.client.overlay.ArtilleryIndicatorOverlay;
import com.cowax.cowaxpack.client.overlay.IFFOverlay;
import com.cowax.cowaxpack.client.overlay.VehicleAimCrosshairOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CowaxPack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        // Регистрируем overlay для артиллерийского индикатора
        event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), ArtilleryIndicatorOverlay.ID, new ArtilleryIndicatorOverlay());
        event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), IFFOverlay.ID, new IFFOverlay());
        // Временно отключено: круг-прицел для техники
        // event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), VehicleAimCrosshairOverlay.ID.getPath(), new VehicleAimCrosshairOverlay());

        // Временно отключено: кастомный HUD CowaxPack для техники.
        // Используется штатный HUD SuperbWarfare.
    }
}
