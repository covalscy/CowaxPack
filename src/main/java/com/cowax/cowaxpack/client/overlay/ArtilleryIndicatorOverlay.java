package com.cowax.cowaxpack.client.overlay;

import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.tools.EntityFindUtil;
import com.atsuishio.superbwarfare.tools.FormatTool;
import com.atsuishio.superbwarfare.tools.TraceTool;
import com.atsuishio.superbwarfare.tools.VectorUtil;
import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.client.ArtilleryIndicatorClientHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import static com.atsuishio.superbwarfare.client.RenderHelper.preciseBlit;
import static com.atsuishio.superbwarfare.client.overlay.IFFOverlay.FRIENDLY_ARTILLERY;
import static com.cowax.cowaxpack.item.common.ArtilleryIndicator.TAG_CANNON;

@OnlyIn(Dist.CLIENT)
public class ArtilleryIndicatorOverlay implements IGuiOverlay {

    public static final String ID = CowaxPack.MODID + "_artillery_indicator";

    private static final ResourceLocation INDICATOR = CowaxPack.loc("textures/screens/indicator.png");
    private static final ResourceLocation SPYGLASS = CowaxPack.loc("textures/screens/spyglass.png");

    private static float scopeScale = 1;
    private static float lerpHoldArtilleryIndicator;

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = gui.getMinecraft();
        PoseStack poseStack = guiGraphics.pose();
        Player player = gui.getMinecraft().player;

        if (player == null) return;

        ItemStack stack = player.getUseItem();
        
        // Проверяем, использует ли игрок артиллерийский индикатор
        // if (player.isUsingItem() && stack.is(com.cowax.cowaxpack.init.ModItems.ARTILLERY_INDICATOR.get()) && mc.options.getCameraType() == CameraType.FIRST_PERSON) {
        if (false && player.isUsingItem() && mc.options.getCameraType() == CameraType.FIRST_PERSON) {
            poseStack.pushPose();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.setShaderColor(1, 1, 1, 1);

            float deltaFrame = Minecraft.getInstance().getDeltaFrameTime();
            scopeScale = (float) Mth.lerp(0.5F * deltaFrame, scopeScale, 1.35F);
            float f = (float) Math.min(screenWidth, screenHeight);
            float f1 = Math.min((float) screenWidth / f, (float) screenHeight / f) * scopeScale;
            float i = Mth.floor(f * f1);
            float j = Mth.floor(f * f1);
            float k = ((screenWidth - i) / 2);
            float l = ((screenHeight - j) / 2);
            float w = i * 21 / 9;
            preciseBlit(guiGraphics, SPYGLASS, k - (2 * w / 7), l, 0, 0.0F, w, j, w, j);

            double targetX = stack.getOrCreateTag().getDouble("TargetX");
            double targetY = stack.getOrCreateTag().getDouble("TargetY");
            double targetZ = stack.getOrCreateTag().getDouble("TargetZ");

            // Отрисовка маркера цели
            Vec3 pos = new Vec3(targetX, targetY, targetZ);
            Vec3 point = VectorUtil.worldToScreen(pos);
            if (VectorUtil.canSee(pos)) {
                float x = (float) point.x;
                float y = (float) point.y;
                preciseBlit(guiGraphics, INDICATOR, Mth.clamp(x - 6, 0, screenWidth - 12), Mth.clamp(y - 6, 0, screenHeight - 12), 0, 0, 12, 12, 12, 12);
            }

            // Отрисовка маркеров привязанной артиллерии
            ListTag tags = stack.getOrCreateTag().getList(TAG_CANNON, Tag.TAG_COMPOUND);
            for (int m = 0; m < tags.size(); m++) {
                var tag = tags.getCompound(m);
                Entity entity = EntityFindUtil.findEntity(player.level(), tag.getString("UUID"));
                if (entity != null) {
                    Vec3 posF = entity.getBoundingBox().getCenter();
                    Vec3 pointF = VectorUtil.worldToScreen(posF);
                    if (VectorUtil.canSee(posF)) {
                        float xf = (float) pointF.x;
                        float yf = (float) pointF.y;
                        preciseBlit(guiGraphics, FRIENDLY_ARTILLERY, Mth.clamp(xf - 6, 0, screenWidth - 12), Mth.clamp(yf - 6, 0, screenHeight - 12), 0, 0, 12, 12, 12, 12);
                    }
                }
            }

            poseStack.popPose();

            // Индикатор удержания для стрельбы (упрощенная версия без renderCircularRing)
            lerpHoldArtilleryIndicator = Mth.lerp(partialTick, lerpHoldArtilleryIndicator, 0.05f * ArtilleryIndicatorClientHandler.holdArtilleryIndicator);

            // TODO: Добавить визуальный индикатор удержания кнопки стрельбы
            // renderCircularRing не существует в этой версии SuperbWarfare

            // Отображение дальности
            boolean lookAtEntity = false;

            BlockHitResult result = player.level().clip(new ClipContext(player.getEyePosition(), player.getEyePosition().add(player.getViewVector(1).scale(512)),
                    ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
            Vec3 hitPos = result.getLocation();

            double blockRange = player.getEyePosition(1).distanceTo(hitPos);

            double entityRange = 0;
            Entity lookingEntity = TraceTool.findLookingEntity(player, 520);

            if (lookingEntity instanceof VehicleEntity) return;

            if (lookingEntity != null) {
                lookAtEntity = true;
                entityRange = player.distanceTo(lookingEntity);
            }

            if (lookAtEntity) {
                guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("tips.cowaxpack.drone.range")
                                .append(Component.literal(FormatTool.format1D(entityRange, "M ") + lookingEntity.getDisplayName().getString())),
                        screenWidth / 2 + 12, screenHeight / 2 - 28, -1, false);
            } else {
                if (blockRange > 500) {
                    guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("tips.cowaxpack.drone.range")
                            .append(Component.literal("---M")), screenWidth / 2 + 12, screenHeight / 2 - 28, -1, false);
                } else {
                    guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("tips.cowaxpack.drone.range")
                                    .append(Component.literal(FormatTool.format1D(blockRange, "M"))),
                            screenWidth / 2 + 12, screenHeight / 2 - 28, -1, false);
                }
            }
        } else {
            scopeScale = 1;
        }
    }
}
