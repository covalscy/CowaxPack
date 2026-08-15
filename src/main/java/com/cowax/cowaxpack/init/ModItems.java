package com.cowax.cowaxpack.init;

import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.item.curio.ParachuteItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM, CowaxPack.MODID);
    public static final DeferredHolder<Item, Item> PARACHUTE = REGISTRY.register("parachute", ParachuteItem::new);
}
