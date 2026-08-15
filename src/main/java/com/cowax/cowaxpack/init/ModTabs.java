package com.cowax.cowaxpack.init;

import com.atsuishio.superbwarfare.item.container.ContainerBlockItem;
import com.cowax.cowaxpack.CowaxPack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CowaxPack.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLOCK_TAB = TABS.register("cowaxpack",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group.cowaxpack.title"))
                    .icon(() -> ContainerBlockItem.createInstance(ModEntities.ZENIT_2C6.get()))
                    .displayItems((param, output) -> {
                                output.accept(ContainerBlockItem.createInstance(ModEntities.ZENIT_2C6.get()));
                                output.accept(ContainerBlockItem.createInstance(ModEntities.FV.get()));
                                output.accept(ModItems.PARACHUTE.get());
                            }
                    )
                    .build());
}
