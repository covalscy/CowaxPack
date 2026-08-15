package com.cowax.cowaxpack.client.renderer.entity;

import com.cowax.cowaxpack.client.layer.ZenitCannonShellLayer;
import com.cowax.cowaxpack.client.model.ZenitCannonShellModel;
import com.cowax.cowaxpack.entity.projectile.ZenitCannonShellEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ZenitCannonShellRenderer extends GeoEntityRenderer<ZenitCannonShellEntity> {
    public ZenitCannonShellRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ZenitCannonShellModel());
        this.addRenderLayer(new ZenitCannonShellLayer(this));
    }

    @Override
    public RenderType getRenderType(ZenitCannonShellEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void preRender(PoseStack poseStack, ZenitCannonShellEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int color) {
        if (entity.tickCount > 0) {
            super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
        }
    }

    @Override
    public void render(ZenitCannonShellEntity entityIn, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        if (entityIn.tickCount > 0) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(90 + Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
            super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
            poseStack.popPose();
        }
    }
}
