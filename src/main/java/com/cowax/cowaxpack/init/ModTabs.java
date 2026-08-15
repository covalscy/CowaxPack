package com.cowax.cowaxpack.init;

import com.atsuishio.superbwarfare.init.ModBlockEntities;
import com.atsuishio.superbwarfare.init.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import com.cowax.cowaxpack.CowaxPack;

@SuppressWarnings("unused")
public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CowaxPack.MODID);

    public static ItemStack createVehicleContainer(EntityType<?> entityType) {
        ItemStack stack = new ItemStack(ModItems.CONTAINER.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("EntityType", EntityType.getKey(entityType).toString());
        BlockItem.setBlockEntityData(stack, ModBlockEntities.CONTAINER.get(), tag);
        return stack;
    }

    public static final RegistryObject<CreativeModeTab> BLOCK_TAB = TABS.register("cowaxpack",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group.cowaxpack.title"))
                    .icon(() -> createVehicleContainer(ModEntities.ZENIT_2C6.get()))
                    .displayItems((param, output) -> {
                        output.accept(createVehicleContainer(ModEntities.ZENIT_2C6.get()));
                        output.accept(createVehicleContainer(ModEntities.FV.get()));
                        output.accept(com.cowax.cowaxpack.init.ModItems.PARACHUTE.get());
                    })
                    .build());
}
