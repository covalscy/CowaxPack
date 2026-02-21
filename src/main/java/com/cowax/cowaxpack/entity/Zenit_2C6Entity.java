package com.cowax.cowaxpack.entity;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.cowax.cowaxpack.init.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PlayMessages;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class Zenit_2C6Entity extends GeoVehicleEntity {

    public Zenit_2C6Entity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.ZENIT_2C6.get(), world);
    }

    public Zenit_2C6Entity(EntityType<Zenit_2C6Entity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> getSourceAngle(source, 0.4f) * damage);
    }

    @Override
    public void baseTick() {
        super.baseTick();
    }

    private PlayState cannonShootPredicate(AnimationState<Zenit_2C6Entity> event) {
        if (getShootAnimationTimer(0, 0) > 0) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.2c6.fire"));
        }
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.2c6.idle"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "cannon", 0, this::cannonShootPredicate));
        // data.add(new AnimationController<>(this, "passengerWeaponStation", 0, this::passengerWeaponStationFirePredicate));
    }
}
