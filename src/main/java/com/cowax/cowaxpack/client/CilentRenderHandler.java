package com.cowax.cowaxpack.client;

import com.cowax.cowaxpack.init.ModItems;
import com.cowax.cowaxpack.client.model.curio.ParachuteModel;
import com.cowax.cowaxpack.client.renderer.curio.ParachuteRenderer;
import com.cowax.cowaxpack.client.renderer.layer.ParachuteLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CilentRenderHandler {
    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        // All overlays removed - only Zenit_2C6 remains
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        CuriosRendererRegistry.register(ModItems.PARACHUTE.get(), ParachuteRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ParachuteModel.LAYER_LOCATION, ParachuteModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        // Добавляем слой парашюта для всех игроков
        for (String skinName : event.getSkins()) {
            LivingEntityRenderer<?, ?> renderer = event.getSkin(skinName);
            if (renderer instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new ParachuteLayer<>(playerRenderer));
            }
        }
    }
}
