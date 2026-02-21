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
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;


public class FvEntity extends GeoVehicleEntity {

    public FvEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.FV.get(), world);
    }

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
        // If turning on spot, apply forward velocity to simulate pivoting around the inner track.
        // "It looks perfect when I drive forward and turn" -> So we simulate driving forward.
        if (Math.abs(this.entityData.get(POWER)) < 0.02 && Math.abs(dYaw) > 0.1) {
            // Calculate required forward speed to pivot around track
            // V = Omega * R
            float trackOffset = 1.1f; // Distance from center to track (approx)
            double pivotSpeed = Math.abs(Math.toRadians(dYaw) * trackOffset);
            
            // Clamp speed to avoid instability
            pivotSpeed = Mth.clamp(pivotSpeed, 0, 0.2);
            
            // Set velocity directly (don't add) to maintain constant pivot speed
            Vec3 forward = Vec3.directionFromRotation(0, yRotAfter);
            setDeltaMovement(forward.scale(pivotSpeed).add(0, getDeltaMovement().y, 0));
            
            // Update s0 so visual logic knows we are moving
            s0 = pivotSpeed;
        }

        // --- Visual Correction: Lock Inner Track ---
        // Multipliers
        float speedMult = (float) (1.5 * Math.PI);
        float turnMult = (float) (0.4 * Math.PI);

        // Calculate theoretical raw changes
        float move = (float) (s0 * speedMult);
        float turn = Mth.clamp(turnMult * deltaRot, -5f, 5f);

        float leftChange = -move + turn;
        float rightChange = -move - turn;

        // Apply Pivot Logic (Lock one side)
        if (s0 >= -0.01) { // Forward or Stopped
            if (leftChange > 0) leftChange = 0;
            if (rightChange > 0) rightChange = 0;
        } else { // Reversing
            if (leftChange < 0) leftChange = 0;
            if (rightChange < 0) rightChange = 0;
        }

        // Apply to Tracks
        setLeftTrack(prevLeftTrack + leftChange);
        setRightTrack(prevRightTrack + rightChange);

        // Apply to Wheels
        float wheelScale = 0.3f; 
        setLeftWheelRot(prevLeftWheel + leftChange * wheelScale);
        setRightWheelRot(prevRightWheel + rightChange * wheelScale);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "cannon", 0, this::cannonShootPredicate));
        // data.add(new AnimationController<>(this, "passengerWeaponStation", 0, this::passengerWeaponStationFirePredicate));
    }
}
