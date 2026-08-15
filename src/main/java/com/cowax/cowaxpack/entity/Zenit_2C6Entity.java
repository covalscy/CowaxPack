package com.cowax.cowaxpack.entity;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class Zenit_2C6Entity extends GeoVehicleEntity {

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

    @Override
    public net.minecraft.world.InteractionResult interact(net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        net.minecraft.world.InteractionResult result = super.interact(player, hand);
        if (result == net.minecraft.world.InteractionResult.PASS && !player.isShiftKeyDown() && this.canAddPassenger(player)) {
            if (player.level().isClientSide()) {
                return net.minecraft.world.InteractionResult.sidedSuccess(true);
            }
        }
        return result;
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
    }
}
