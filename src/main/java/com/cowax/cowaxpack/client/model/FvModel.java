package com.cowax.cowaxpack.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.entity.FvEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;

public class FvModel extends VehicleModel<FvEntity> {
    @Override
    public ResourceLocation getAnimationResource(FvEntity animatable) {
        return CowaxPack.loc("animations/fv.animation.json");
    }

    @Override
    public @Nullable TransformContext<FvEntity> collectTransform(String boneName) {
        if ("gun".equals(boneName) || "barrel".equals(boneName)) {
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
