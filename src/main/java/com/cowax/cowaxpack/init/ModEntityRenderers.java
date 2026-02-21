package com.cowax.cowaxpack.init;

import com.cowax.cowaxpack.client.model.curio.ParachuteModel;
import com.cowax.cowaxpack.client.renderer.entity.MedicalKitEntityRenderer;
import com.cowax.cowaxpack.client.renderer.entity.FvRenderer;
import com.cowax.cowaxpack.client.renderer.entity.Zenit_2C6Renderer;
import com.cowax.cowaxpack.client.renderer.entity.ZenitCannonShellRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEntityRenderers {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ZENIT_2C6.get(), Zenit_2C6Renderer::new);
        event.registerEntityRenderer(ModEntities.MEDICAL_KIT.get(), MedicalKitEntityRenderer::new);
        event.registerEntityRenderer(ModEntities.ZENIT_CANNON_SHELL.get(), ZenitCannonShellRenderer::new);
        event.registerEntityRenderer(ModEntities.FV.get(), FvRenderer::new);
        }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ParachuteModel.LAYER_LOCATION, ParachuteModel::createBodyLayer);
    }
}

