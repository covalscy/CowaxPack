package com.cowax.cowaxpack.init;

import com.atsuishio.superbwarfare.init.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import com.cowax.cowaxpack.CowaxPack;

@SuppressWarnings("unused")
public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CowaxPack.MODID);

    public static final RegistryObject<CreativeModeTab> BLOCK_TAB = TABS.register("cowaxpack",
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
