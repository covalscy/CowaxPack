package com.cowax.cowaxpack.client.renderer.entity;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.cowax.cowaxpack.client.model.Zenit_2C6Model;
import com.cowax.cowaxpack.entity.Zenit_2C6Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Zenit_2C6Renderer extends VehicleRenderer<Zenit_2C6Entity> {

    public Zenit_2C6Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Zenit_2C6Model());
    }
}
