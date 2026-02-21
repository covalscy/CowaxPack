package com.cowax.cowaxpack.client;

import com.cowax.cowaxpack.CowaxPack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = CowaxPack.MODID, value = Dist.CLIENT)
public class PingHandler {
    private static final ResourceLocation PING_TEXTURE = new ResourceLocation(CowaxPack.MODID, "textures/gui/point.png");
    private static final ResourceLocation APC_TEXTURE = new ResourceLocation(CowaxPack.MODID, "textures/gui/apc.png");
    private static final ResourceLocation HELICOPTER_TEXTURE = new ResourceLocation(CowaxPack.MODID, "textures/gui/helicopter.png");
    private static final ResourceLocation AIRCRAFT_TEXTURE = new ResourceLocation(CowaxPack.MODID, "textures/gui/aircraft.png");
    
    private static final List<PingData> pings = new ArrayList<>();
    private static final int PING_DURATION = 100;
    private static Matrix4f lastModelViewMatrix;
    private static Matrix4f lastProjectionMatrix;

    public static void addPing(double x, double y, double z, int entityId, String vehicleType) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            pings.add(new PingData(new Vec3(x, y, z), mc.level.getGameTime(), entityId, vehicleType));
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(net.minecraftforge.client.event.RenderLevelStageEvent event) {
        if (event.getStage() == net.minecraftforge.client.event.RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            lastModelViewMatrix = new Matrix4f(event.getPoseStack().last().pose());
            lastProjectionMatrix = event.getProjectionMatrix();
        }
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() != VanillaGuiOverlay.VIGNETTE.type()) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || lastModelViewMatrix == null || lastProjectionMatrix == null) return;

        long currentTime = mc.level.getGameTime();
        Iterator<PingData> iterator = pings.iterator();
        
        GuiGraphics guiGraphics = event.getGuiGraphics();
        PoseStack poseStack = guiGraphics.pose();
        
        poseStack.pushPose();
        poseStack.translate(0f, 0f, -pings.size() * 16f);
        
        while (iterator.hasNext()) {
            PingData ping = iterator.next();
            if (currentTime - ping.spawnTime > PING_DURATION) {
                iterator.remove();
                continue;
            }

            if (ping.entityId != -1) {
                net.minecraft.world.entity.Entity entity = mc.level.getEntity(ping.entityId);
                if (entity != null && entity.isAlive()) {
                    ping.pos = entity.position().add(0, entity.getBbHeight() * 0.5, 0);
                }
            }

            ScreenPos screenPos = worldToScreen(ping.pos, lastModelViewMatrix, lastProjectionMatrix, mc.getWindow());
            
            if (screenPos != null) {
                poseStack.translate(0f, 0f, 16f);
                
                if (!screenPos.isBehindCamera()) {
                    renderPing(guiGraphics, screenPos, ping.pos, ping.vehicleType);
                }
            }
        }
        
        poseStack.popPose();
    }

    private static void renderPing(GuiGraphics guiGraphics, ScreenPos screenPos, Vec3 worldPos, String vehicleType) {
        Minecraft mc = Minecraft.getInstance();
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        double dist = worldPos.distanceTo(cameraPos);
        
        // Масштабирование как в Ping Wheel
        double scale = 2.0 / Math.pow(dist, 0.3);
        scale = Math.max(1.0, scale) * 0.5;
        
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(screenPos.x, screenPos.y, 0);
        poseStack.scale((float)scale, (float)scale, 1f);
        
        // Выбор текстуры
        ResourceLocation texture = PING_TEXTURE;
        int textureSize = 1;
        
        if (vehicleType != null) {
            switch (vehicleType) {
                case "TANK":
                case "APC":
                case "AA":
                    texture = APC_TEXTURE;
                    textureSize = 64;
                    break;
                case "HELICOPTER":
                    texture = HELICOPTER_TEXTURE;
                    textureSize = 64;
                    break;
                case "AIRPLANE":
                    texture = AIRCRAFT_TEXTURE;
                    textureSize = 64;
                    break;
            }
        }
        
        // Рендер текстуры
        renderTexture(guiGraphics, texture, 12, textureSize);
        
        // Рендер дистанции
        String distStr = String.format("%.1fm", dist);
        int textWidth = mc.font.width(distStr);
        guiGraphics.drawString(mc.font, distStr, -textWidth / 2, 8, 0xFFFFFFFF, true);
        
        poseStack.popPose();
    }
    
    private static void renderTexture(GuiGraphics guiGraphics, ResourceLocation texture, int displaySize, int textureSize) {
        int offset = displaySize / -2;
        
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();
        guiGraphics.blit(
            texture,
            offset,
            offset,
            0,
            0,
            0,
            displaySize,
            displaySize,
            textureSize,
            textureSize
        );
        RenderSystem.disableBlend();
    }

    private static ScreenPos worldToScreen(Vec3 worldPos, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, com.mojang.blaze3d.platform.Window window) {
        Minecraft mc = Minecraft.getInstance();
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        
        // Как в Ping Wheel: camera.getPosition().reverse().add(worldPos)
        Vec3 relativePos = cameraPos.reverse().add(worldPos);
        Vector4f worldPosRel = new Vector4f((float)relativePos.x, (float)relativePos.y, (float)relativePos.z, 1f);
        
        worldPosRel.mul(modelViewMatrix);
        worldPosRel.mul(projectionMatrix);

        float depth = worldPosRel.w;
        
        if (depth != 0) {
            worldPosRel.div(depth);
        }

        return new ScreenPos(
            window.getGuiScaledWidth() * (0.5f + worldPosRel.x * 0.5f),
            window.getGuiScaledHeight() * (0.5f - worldPosRel.y * 0.5f),
            depth
        );
    }

    private static class ScreenPos {
        float x, y, depth;
        
        ScreenPos(float x, float y, float depth) {
            this.x = x;
            this.y = y;
            this.depth = depth;
        }
        
        boolean isBehindCamera() {
            return depth <= 0;
        }
    }

    private static class PingData {
        Vec3 pos;
        long spawnTime;
        int entityId;
        String vehicleType;

        public PingData(Vec3 pos, long spawnTime, int entityId, String vehicleType) {
            this.pos = pos;
            this.spawnTime = spawnTime;
            this.entityId = entityId;
            this.vehicleType = vehicleType;
        }
    }
}
