package com.cowax.cowaxpack.client.model;

import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.entity.Zenit_2C6Entity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class Zenit_2C6Model extends GeoModel<Zenit_2C6Entity> {
    @Override
    public ResourceLocation getModelResource(Zenit_2C6Entity animatable) {
        return CowaxPack.loc("geo/2c6.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Zenit_2C6Entity animatable) {
        return CowaxPack.loc("textures/entity/2c6.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Zenit_2C6Entity animatable) {
        return CowaxPack.loc("animations/2c6.animation.json");
    }
}
