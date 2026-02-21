package com.cowax.cowaxpack.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.entity.FvEntity;
import net.minecraft.resources.ResourceLocation;

public class FvModel extends VehicleModel<FvEntity> {
    // VehicleModel автоматически загружает модели, текстуры и LOD из конфигурации
    // Файл: assets/cowaxpack/sbw/vehicles/fv.json
    
    @Override
    public ResourceLocation getAnimationResource(FvEntity animatable) {
        // VehicleModel не предоставляет анимации, нужно явно указать путь
        return new ResourceLocation(CowaxPack.MODID, "animations/fv.animation.json");
    }
}
