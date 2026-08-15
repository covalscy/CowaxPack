package com.cowax.cowaxpack.client;

import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.init.ModItems;
import com.cowax.cowaxpack.client.model.curio.ParachuteModel;
import com.cowax.cowaxpack.client.renderer.curio.ParachuteRenderer;
import com.cowax.cowaxpack.client.renderer.layer.ParachuteLayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@EventBusSubscriber(modid = CowaxPack.MODID, value = Dist.CLIENT)
public class CilentRenderHandler {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> CuriosRendererRegistry.register(ModItems.PARACHUTE.get(), ParachuteRenderer::new));
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ParachuteModel.LAYER_LOCATION, ParachuteModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (var skin : event.getSkins()) {
            EntityRenderer<? extends Player> renderer = event.getSkin(skin);
            if (renderer instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new ParachuteLayer<>(playerRenderer));
            }
        }
    }
}
