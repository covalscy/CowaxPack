package com.cowax.cowaxpack.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.entity.Zenit_2C6Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;

public class Zenit_2C6Model extends VehicleModel<Zenit_2C6Entity> {

    @Override
    public ResourceLocation getModelResource(Zenit_2C6Entity animatable) {
        return new ResourceLocation(CowaxPack.MODID, "geo/2c6.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Zenit_2C6Entity animatable) {
        return new ResourceLocation(CowaxPack.MODID, "textures/entity/2c6.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Zenit_2C6Entity animatable) {
        return new ResourceLocation(CowaxPack.MODID, "animations/2c6.animation.json");
    }

    @Override
    public @Nullable TransformContext<Zenit_2C6Entity> collectTransform(String boneName) {
        if ("cannon".equals(boneName) || "turret".equals(boneName)) {
            return (bone, vehicle, state) -> {
                float yaw = vehicle.getTurretYaw(state.getPartialTick());
                bone.setRotY(yaw * Mth.DEG_TO_RAD);
                CoreGeoBone turretLaser = getAnimationProcessor().getBone("turretLaser");
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
                CoreGeoBone barrelLaser = getAnimationProcessor().getBone("barrelLaser");
                if (barrelLaser != null) {
                    barrelLaser.setRotX(bone.getRotX());
                }
            };
        }
        if ("aim".equals(boneName)) {
            return (bone, vehicle, state) -> {
                float aimRot = vehicle.getGunYRot(state.getPartialTick());
                float turretYaw = vehicle.getTurretYaw(state.getPartialTick());
                bone.setRotY((aimRot - turretYaw) * Mth.DEG_TO_RAD);
            };
        }
        if ("aim_cannon".equals(boneName)) {
            return (bone, vehicle, state) -> {
                bone.setRotX(-vehicle.getGunXRot(state.getPartialTick()) * Mth.DEG_TO_RAD);
            };
        }
        return super.collectTransform(boneName);
    }
}
