package com.cowax.cowaxpack.init;

import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.entity.MedicalKitEntity;
import com.cowax.cowaxpack.entity.Zenit_2C6Entity;
import com.cowax.cowaxpack.entity.FvEntity;
import com.cowax.cowaxpack.entity.projectile.ZenitCannonShellEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CowaxPack.MODID);

    public static final RegistryObject<EntityType<Zenit_2C6Entity>> ZENIT_2C6 = register("zenit_2c6",
            EntityType.Builder.<Zenit_2C6Entity>of(Zenit_2C6Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(Zenit_2C6Entity::new)
                    .fireImmune()
                    .sized(4.0f, 2.9f)
    );
public static final RegistryObject<EntityType<FvEntity>> FV = register("fv",
            EntityType.Builder.<FvEntity>of(FvEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setCustomClientFactory(FvEntity::new)
                    .fireImmune()
                    .sized(4.0f, 2.9f)
    );
    public static final RegistryObject<EntityType<MedicalKitEntity>> MEDICAL_KIT = register("medical_kit",
            EntityType.Builder.of(MedicalKitEntity::new, MobCategory.MISC).setTrackingRange(64).setUpdateInterval(1).sized(0.4f, 0.2f));

    public static final RegistryObject<EntityType<ZenitCannonShellEntity>> ZENIT_CANNON_SHELL = register("zenit_cannon_shell",
            EntityType.Builder.<ZenitCannonShellEntity>of(ZenitCannonShellEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(false)
                    .noSave()
                    .sized(0.25f, 0.25f));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
        return REGISTRY.register(name, () -> entityTypeBuilder.build(name));
    }
}
