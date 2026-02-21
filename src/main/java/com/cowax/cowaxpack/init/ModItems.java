package com.cowax.cowaxpack.init;

import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.item.common.MedicalKitItem;
import com.cowax.cowaxpack.item.curio.IffItem;
import com.cowax.cowaxpack.item.curio.ParachuteItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, CowaxPack.MODID);
    public static final RegistryObject<Item> PARACHUTE = REGISTRY.register("parachute", ParachuteItem::new);
    public static final RegistryObject<Item> MEDICAL_KIT = REGISTRY.register("medical_kit", MedicalKitItem::new);
    public static final RegistryObject<Item> IFF = REGISTRY.register("iff", IffItem::new);
}
