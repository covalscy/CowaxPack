package com.cowax.cowaxpack;

import com.cowax.cowaxpack.init.CowaxSounds;
import com.cowax.cowaxpack.init.ModEntities;
import com.cowax.cowaxpack.init.ModItems;
import com.cowax.cowaxpack.init.ModTabs;
import com.cowax.cowaxpack.network.ParachuteMessage;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

@Mod(CowaxPack.MODID)
public class CowaxPack {
    public static final String MODID = "cowaxpack";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CowaxPack(IEventBus bus, ModContainer container) {
        CowaxSounds.REGISTRY.register(bus);
        ModItems.REGISTRY.register(bus);
        ModEntities.REGISTRY.register(bus);
        ModTabs.TABS.register(bus);

        bus.addListener(this::commonSetup);
        bus.addListener(this::registerPayloads);

        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("CowaxPack common setup");
    }

    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                ParachuteMessage.TYPE,
                ParachuteMessage.STREAM_CODEC,
                ParachuteMessage::handle
        );
    }

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
