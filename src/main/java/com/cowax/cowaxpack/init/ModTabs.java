package com.cowax.cowaxpack.init;

import com.atsuishio.superbwarfare.init.ModItems;
import com.cowax.cowaxpack.CowaxPack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CowaxPack.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLOCK_TAB = TABS.register("cowaxpack",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group.cowaxpack.title"))
                    .icon(() -> new ItemStack(ModItems.CONTAINER.get()))
                    .displayItems((param, output) -> {
                                output.accept(ModItems.CONTAINER.get());
                                output.accept(com.cowax.cowaxpack.init.ModItems.PARACHUTE.get());
                            }
                    )
                    .build());
}
