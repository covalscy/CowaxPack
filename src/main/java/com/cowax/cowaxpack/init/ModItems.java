package com.cowax.cowaxpack.init;

import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.item.VehicleSpawnItem;
import com.cowax.cowaxpack.item.curio.ParachuteItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM, CowaxPack.MODID);

    public static final DeferredHolder<Item, Item> PARACHUTE = REGISTRY.register("parachute", ParachuteItem::new);

    public static final DeferredHolder<Item, ? extends Item> ZENIT_2C6_SPAWN_ITEM = REGISTRY.register(
            "zenit_2c6_spawn_item",
            () -> new VehicleSpawnItem<>(ModEntities.ZENIT_2C6, new Item.Properties().stacksTo(1))
    );

    public static final DeferredHolder<Item, ? extends Item> FV_SPAWN_ITEM = REGISTRY.register(
            "fv_spawn_item",
            () -> new VehicleSpawnItem<>(ModEntities.FV, new Item.Properties().stacksTo(1))
    );
}
