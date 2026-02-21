package com.cowax.cowaxpack.client.model.entity;

import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.entity.MedicalKitEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MedicalKitModel extends GeoModel<MedicalKitEntity> {

    @Override
    public ResourceLocation getAnimationResource(MedicalKitEntity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(MedicalKitEntity entity) {
        return CowaxPack.loc("geo/medical_kit.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MedicalKitEntity entity) {
        return CowaxPack.loc("textures/entity/medical_kit.png");
    }
}

