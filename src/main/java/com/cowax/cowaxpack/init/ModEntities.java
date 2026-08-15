package com.cowax.cowaxpack.init;

import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.entity.FvEntity;
import com.cowax.cowaxpack.entity.Zenit_2C6Entity;
import com.cowax.cowaxpack.entity.projectile.ZenitCannonShellEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, CowaxPack.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<Zenit_2C6Entity>> ZENIT_2C6 = register("zenit_2c6",
            EntityType.Builder.<Zenit_2C6Entity>of(Zenit_2C6Entity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .fireImmune()
                    .sized(4.0f, 2.9f)
    );

    public static final DeferredHolder<EntityType<?>, EntityType<FvEntity>> FV = register("fv",
            EntityType.Builder.<FvEntity>of(FvEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .fireImmune()
                    .sized(4.0f, 2.9f)
    );

    public static final DeferredHolder<EntityType<?>, EntityType<ZenitCannonShellEntity>> ZENIT_CANNON_SHELL = register("zenit_cannon_shell",
            EntityType.Builder.<ZenitCannonShellEntity>of(ZenitCannonShellEntity::new, MobCategory.MISC)
                    .setTrackingRange(64)
                    .setUpdateInterval(1)
                    .noSave()
                    .sized(0.25f, 0.25f));

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
        return REGISTRY.register(name, () -> entityTypeBuilder.build(name));
    }
}
