package com.cowax.cowaxpack.client;

import com.cowax.cowaxpack.entity.Zenit_2C6Entity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import com.atsuishio.superbwarfare.event.ClientEventHandler;

public class ZenitHudOverlay implements IGuiOverlay {
    
    public static final ResourceLocation ID = new ResourceLocation("cowaxpack", "zenit_hud");
    
    private static final ResourceLocation FRAME = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/tv_frame.png");
    private static final ResourceLocation LINE = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/line.png");
    private static final ResourceLocation COMPASS = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/base/compass.png");
    private static final ResourceLocation ROLL_IND = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/helicopter/roll_ind.png");
    
    // Icons
    private static final ResourceLocation BODY = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/body.png");
    private static final ResourceLocation LEFT_WHEEL = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/left_wheel.png");
    private static final ResourceLocation RIGHT_WHEEL = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/right_wheel.png");
    private static final ResourceLocation ENGINE = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/engine.png");
    private static final ResourceLocation BARREL = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/line.png"); // Placeholder as in original

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        
        if (player.getVehicle() instanceof Zenit_2C6Entity vehicle) {
            // Render only for driver (seat 0)
            if (vehicle.getSeatIndex(player) != 0) return;
            
            // Basic check to avoid rendering in 3rd person if not zoomed (optional, matching style)
            if (!mc.options.getCameraType().isFirstPerson() && !ClientEventHandler.zoomVehicle) return;

            renderHud(vehicle, player, guiGraphics, partialTick, screenWidth, screenHeight);
        }
    }

    private void renderHud(Zenit_2C6Entity vehicle, Player player, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        PoseStack poseStack = guiGraphics.pose();
        int color = 0x66FF00; // Green HUD color

        poseStack.pushPose();
        
        // Setup render state
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // 1. Frame
        int addW = (screenWidth / screenHeight) * 48;
        int addH = (screenWidth / screenHeight) * 27;
        // blit(texture, x, y, u, v, width, height, textureWidth, textureHeight)
        // Using a helper or direct blit. Let's use direct blit with scaling if needed.
        // The original uses preciseBlit which allows float coordinates. We'll use standard blit for simplicity or try to match.
        
        // Draw Frame (simplified)
        guiGraphics.blit(FRAME, -addW / 2, -addH / 2, 0, 0, screenWidth + addW, screenHeight + addH, screenWidth + addW, screenHeight + addH);

        // 2. Compass
        // Calculate yaw
        float yaw = player.getYRot();
        // Texture logic: 128 + (64F / 45 * yaw)
        // Compass texture is likely wide and we pick a window.
        // Original: blit(poseStack, COMPASS, screenWidth / 2 - 128, 10F, 128 + (64F / 45 * player.getYRot()), 0, 256, 16, 512, 16, color);
        // We need to handle color. Standard blit doesn't colorize easily without shaders or manual coloring.
        // For now, let's just draw white or use setShaderColor.
        
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        RenderSystem.setShaderColor(r, g, b, 1.0f);

        // Compass
        int compassX = screenWidth / 2 - 128;
        int compassY = 10;
        float compassU = 128 + (64F / 45 * yaw);
        guiGraphics.blit(COMPASS, compassX, compassY, compassU, 0, 256, 16, 512, 16);

        // 3. Speed
        double speed = vehicle.getDeltaMovement().length() * 20 * 3.6; // m/s to km/h approx
        String speedStr = String.format("%.0f km/h", speed);
        guiGraphics.drawString(Minecraft.getInstance().font, speedStr, screenWidth / 2 + 160, screenHeight / 2 - 48, color, false);

        // 4. Vehicle Status (Body)
        // Simplified status rendering
        int bodyX = screenWidth / 2 + 96;
        int bodyY = screenHeight - 72;
        
        guiGraphics.blit(BODY, bodyX, bodyY, 0, 0, 32, 32, 32, 32);
        guiGraphics.blit(LEFT_WHEEL, bodyX, bodyY, 0, 0, 32, 32, 32, 32);
        guiGraphics.blit(RIGHT_WHEEL, bodyX, bodyY, 0, 0, 32, 32, 32, 32);
        guiGraphics.blit(ENGINE, bodyX, bodyY, 0, 0, 32, 32, 32, 32);

        // 5. Health Text
        int healthPercent = (int) (vehicle.getHealth() / vehicle.getMaxHealth() * 100);
        String healthStr = healthPercent + "%";
        guiGraphics.drawString(Minecraft.getInstance().font, healthStr, screenWidth / 2 - 165, screenHeight / 2 - 46, color, false);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }
}
