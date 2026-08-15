package com.cowax.cowaxpack.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.entity.Zenit_2C6Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;

public class Zenit_2C6Model extends VehicleModel<Zenit_2C6Entity> {
    @Override
    public ResourceLocation getAnimationResource(Zenit_2C6Entity animatable) {
        return CowaxPack.loc("animations/2c6.animation.json");
    }

    @Override
    public @Nullable TransformContext<Zenit_2C6Entity> collectTransform(String boneName) {
        if ("cannon".equals(boneName) || "turret".equals(boneName)) {
            return (bone, vehicle, state) -> {
                float yaw = vehicle.getTurretYaw(state.getPartialTick());
                bone.setRotY(yaw * Mth.DEG_TO_RAD);
                GeoBone turretLaser = getAnimationProcessor().getBone("turretLaser");
                if (turretLaser != null) {
                    turretLaser.setRotY(bone.getRotY());
                }
                bone.setHidden(vehicle.isWreck() && vehicle.hasTurret() && vehicle.getSympatheticDetonated());
            };
        }
        if ("barrel".equals(boneName)) {
            return (bone, vehicle, state) -> {
                float pitch = vehicle.getTurretPitch(state.getPartialTick());
                bone.setRotX(Mth.clamp(-pitch, vehicle.getTurretMinPitch(), vehicle.getTurretMaxPitch()) * Mth.DEG_TO_RAD);
                GeoBone barrelLaser = getAnimationProcessor().getBone("barrelLaser");
                if (barrelLaser != null) {
                    barrelLaser.setRotX(bone.getRotX());
                }
            };
        }
        return super.collectTransform(boneName);
    }
}
