package com.cowax.cowaxpack.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.entity.FvEntity;
import net.minecraft.resources.ResourceLocation;

public class FvModel extends VehicleModel<FvEntity> {
    @Override
    public ResourceLocation getAnimationResource(FvEntity animatable) {
        return CowaxPack.loc("animations/fv.animation.json");
    }
}
