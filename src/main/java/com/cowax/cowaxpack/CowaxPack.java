package com.cowax.cowaxpack;

import com.cowax.cowaxpack.init.CowaxSounds;
import com.cowax.cowaxpack.init.ModEntities;
import com.cowax.cowaxpack.init.ModItems;
import com.cowax.cowaxpack.init.ModTabs;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

import java.util.Optional;

@net.minecraftforge.fml.common.Mod(CowaxPack.MODID)
public class CowaxPack {
    public static final String MODID = "cowaxpack";
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public CowaxPack() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        CowaxSounds.REGISTRY.register(bus);
        ModItems.REGISTRY.register(bus);
        ModEntities.REGISTRY.register(bus);
        ModTabs.TABS.register(bus);

        bus.addListener(this::commonSetup);
        bus.addListener(this::registerContainers);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerContainers(final com.atsuishio.superbwarfare.api.event.RegisterContainersEvent event) {
        event.add(ModEntities.ZENIT_2C6);
        event.add(ModEntities.FV);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("common setup");
        
        int id = 0;
        NETWORK.registerMessage(id++, com.cowax.cowaxpack.network.ParachuteMessage.class,
                (msg, buf) -> {},
                buf -> com.cowax.cowaxpack.network.ParachuteMessage.INSTANCE,
                (msg, ctx) -> com.cowax.cowaxpack.network.ParachuteMessage.handler(ctx),
                Optional.of(net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER));
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }
}
