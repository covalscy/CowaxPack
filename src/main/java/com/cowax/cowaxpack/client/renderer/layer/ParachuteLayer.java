package com.cowax.cowaxpack.client.renderer.layer;

import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.client.model.curio.ParachuteModel;
import com.cowax.cowaxpack.item.curio.ParachuteItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class ParachuteLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = CowaxPack.loc("textures/curio/parachute.png");
    private ParachuteModel model;

    public ParachuteLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!ParachuteItem.isParachuteOpen(livingEntity)) return;

        if (!(livingEntity instanceof net.minecraft.world.entity.player.Player player)) return;

        boolean inInventory = player.getInventory().items.stream()
                .anyMatch(stack -> stack.getItem() == com.cowax.cowaxpack.init.ModItems.PARACHUTE.get() &&
                        ParachuteItem.isOpen(stack));

        if (!inInventory) return;

        if (model == null) {
            model = new ParachuteModel(Minecraft.getInstance().getEntityModels().bakeLayer(ParachuteModel.LAYER_LOCATION));
        }

        poseStack.pushPose();

        poseStack.translate(0, 0.5, 0);
        poseStack.scale(0.5f, 0.5f, 0.5f);

        model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTick);
        model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.armorCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);

        poseStack.popPose();
    }
}
