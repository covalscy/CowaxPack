package com.cowax.cowaxpack.client.model;

import com.cowax.cowaxpack.entity.projectile.ZenitCannonShellEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class ZenitCannonShellModel extends GeoModel<ZenitCannonShellEntity> {

    @Override
    public ResourceLocation getAnimationResource(ZenitCannonShellEntity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(ZenitCannonShellEntity entity) {
        return new ResourceLocation("superbwarfare", "geo/small_cannon_shell.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ZenitCannonShellEntity entity) {
        return new ResourceLocation("superbwarfare", "textures/entity/small_cannon_shell.png");
    }

    @Override
    public void setCustomAnimations(ZenitCannonShellEntity animatable, long instanceId, AnimationState<ZenitCannonShellEntity> animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("bone");
        if (bone != null) {
            bone.setScaleY((float) (1 + 2 * animatable.getDeltaMovement().length()));
        }
    }
}
