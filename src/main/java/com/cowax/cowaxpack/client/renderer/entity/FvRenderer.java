package com.cowax.cowaxpack.client.renderer.entity;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.cowax.cowaxpack.client.model.FvModel;
import com.cowax.cowaxpack.entity.FvEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class FvRenderer extends VehicleRenderer<FvEntity> {

    public FvRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FvModel());
    }
}
