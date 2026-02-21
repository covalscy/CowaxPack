package com.cowax.cowaxpack.client.renderer.curio;

import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.client.model.curio.ParachuteModel;
import com.cowax.cowaxpack.item.curio.ParachuteItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ParachuteRenderer implements ICurioRenderer {

    private static ParachuteModel firstPersonModel;
    private static final ResourceLocation TEXTURE = CowaxPack.loc("textures/curio/parachute.png");

    private final ParachuteModel model;

    public ParachuteRenderer() {
        model = new ParachuteModel(Minecraft.getInstance().getEntityModels().bakeLayer(ParachuteModel.LAYER_LOCATION));
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStack.pushPose();

        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.translate(0, 0.25, 0);

        if (stack.getOrCreateTag().getBoolean(ParachuteItem.TAG_OPEN)) {
            LivingEntity entity = slotContext.entity();
            this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
            this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(TEXTURE), false, stack.hasFoil());

            model.renderToBuffer(matrixStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        matrixStack.popPose();
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        RenderBuffers buffers = Minecraft.getInstance().renderBuffers();
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        if (!ParachuteItem.isParachuteOpen(player)) return;
        
        // Проверяем, что парашют НЕ в Curios слоте (чтобы избежать двойного рендеринга)
        boolean inCurios = CuriosApi.getCuriosInventory(player)
                .map(c -> c.findFirstCurio(com.cowax.cowaxpack.init.ModItems.PARACHUTE.get())
                        .map(slotResult -> slotResult.stack().getOrCreateTag().getBoolean(ParachuteItem.TAG_OPEN))
                        .orElse(false)
                ).orElse(false);
        
        if (inCurios) return; // Если в Curios, не рендерим здесь
        
        // Проверяем, что парашют в инвентаре и открыт
        boolean inInventory = player.getInventory().items.stream()
                .anyMatch(stack -> stack.getItem() == com.cowax.cowaxpack.init.ModItems.PARACHUTE.get() && 
                                 stack.getOrCreateTag().getBoolean(ParachuteItem.TAG_OPEN));
        
        if (!inInventory) return;
        
        PoseStack stack = event.getPoseStack();

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            stack.pushPose();

            if (firstPersonModel == null) {
                firstPersonModel = new ParachuteModel(Minecraft.getInstance().getEntityModels().bakeLayer(ParachuteModel.LAYER_LOCATION));
            }

            // Рендерим парашют для первого лица
            if (Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
                // Позиционируем парашют в мировых координатах над игроком
                stack.translate(player.getX() - event.getCamera().getPosition().x,
                               player.getY() - event.getCamera().getPosition().y + 3.0,
                               player.getZ() - event.getCamera().getPosition().z);
                // Переворачиваем парашют правильной стороной вверх
                stack.mulPose(Axis.ZP.rotationDegrees(180));
                stack.scale(0.5f, 0.5f, 0.5f);
                
                firstPersonModel.prepareMobModel(player, 0, 0, event.getPartialTick());
                firstPersonModel.setupAnim(player, 0, 0, player.tickCount, 0, 0);
                firstPersonModel.renderToBuffer(stack, buffers.bufferSource().getBuffer(RenderType.armorCutoutNoCull(TEXTURE)), 0xFFFFFF, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }

            stack.popPose();
        }
    }
}
