package com.cowax.cowaxpack.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.entity.Zenit_2C6Entity;
import net.minecraft.resources.ResourceLocation;

public class Zenit_2C6Model extends VehicleModel<Zenit_2C6Entity> {
    @Override
    public ResourceLocation getAnimationResource(Zenit_2C6Entity animatable) {
        return CowaxPack.loc("animations/2c6.animation.json");
    }
}
