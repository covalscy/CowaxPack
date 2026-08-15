package com.cowax.cowaxpack.entity;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class FvEntity extends GeoVehicleEntity {

    public FvEntity(EntityType<FvEntity> pEntityType, Level pLevel) {
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

    private PlayState cannonShootPredicate(AnimationState<FvEntity> event) {
        if (getShootAnimationTimer(0, 0) > 0) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.fv.fire"));
        }
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.fv.idle"));
    }

    @Override
    public void travel() {
        // Capture previous state
        float prevLeftTrack = getLeftTrack();
        float prevRightTrack = getRightTrack();
        float prevLeftWheel = getLeftWheelRot();
        float prevRightWheel = getRightWheelRot();
        float yRotBefore = getYRot();

        super.travel();

        float yRotAfter = getYRot();
        float dYaw = Mth.wrapDegrees(yRotAfter - yRotBefore);

        double s0 = getDeltaMovement().dot(this.getViewVector(1));
        float deltaRot = this.entityData.get(DELTA_ROT);

        // --- Physics Correction: Pivot Turn (Move Center) ---
        if (Math.abs(this.entityData.get(POWER)) < 0.02 && Math.abs(dYaw) > 0.1) {
            float trackOffset = 1.1f;
            double pivotSpeed = Math.abs(Math.toRadians(dYaw) * trackOffset);
            pivotSpeed = Mth.clamp(pivotSpeed, 0, 0.2);
            Vec3 forward = Vec3.directionFromRotation(0, yRotAfter);
            setDeltaMovement(forward.scale(pivotSpeed).add(0, getDeltaMovement().y, 0));
            s0 = pivotSpeed;
        }

        // --- Visual Correction: Lock Inner Track ---
        float speedMult = (float) (1.5 * Math.PI);
        float turnMult = (float) (0.4 * Math.PI);

        float move = (float) (s0 * speedMult);
        float turn = Mth.clamp(turnMult * deltaRot, -5f, 5f);

        float leftChange = -move + turn;
        float rightChange = -move - turn;

        if (s0 >= -0.01) {
            if (leftChange > 0) leftChange = 0;
            if (rightChange > 0) rightChange = 0;
        } else {
            if (leftChange < 0) leftChange = 0;
            if (rightChange < 0) rightChange = 0;
        }

        setLeftTrack(prevLeftTrack + leftChange);
        setRightTrack(prevRightTrack + rightChange);

        float wheelScale = 0.3f;
        setLeftWheelRot(prevLeftWheel + leftChange * wheelScale);
        setRightWheelRot(prevRightWheel + rightChange * wheelScale);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "cannon", 0, this::cannonShootPredicate));
    }
}
