package com.cowax.cowaxpack.init;

import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.client.renderer.entity.FvRenderer;
import com.cowax.cowaxpack.client.renderer.entity.Zenit_2C6Renderer;
import com.cowax.cowaxpack.client.renderer.entity.ZenitCannonShellRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = CowaxPack.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEntityRenderers {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ZENIT_2C6.get(), Zenit_2C6Renderer::new);
        event.registerEntityRenderer(ModEntities.ZENIT_CANNON_SHELL.get(), ZenitCannonShellRenderer::new);
        event.registerEntityRenderer(ModEntities.FV.get(), FvRenderer::new);
    }
}
