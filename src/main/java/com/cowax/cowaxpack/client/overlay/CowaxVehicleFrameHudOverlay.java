package com.cowax.cowaxpack.client.overlay;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.cowax.cowaxpack.entity.FvEntity;
import com.cowax.cowaxpack.entity.Zenit_2C6Entity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

/**
 * Кастомизируемый HUD с рамками экрана для FV и Zenit
 * Показывает: рамку экрана, компас, скорость, здоровье, иконки состояния
 */
public class CowaxVehicleFrameHudOverlay implements IGuiOverlay {
    
    public static final ResourceLocation ID = new ResourceLocation("cowaxpack", "vehicle_frame_hud");
    
    // Текстуры из SuperbWarfare
    private static final ResourceLocation FRAME = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/tv_frame.png");
    private static final ResourceLocation COMPASS = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/base/compass.png");
    
    // Иконки состояния машины
    private static final ResourceLocation BODY = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/body.png");
    private static final ResourceLocation LEFT_WHEEL = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/left_wheel.png");
    private static final ResourceLocation RIGHT_WHEEL = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/right_wheel.png");
    private static final ResourceLocation ENGINE = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/engine.png");
    
    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        
        // Проверяем что игрок в нашей машине
        if (!(player.getVehicle() instanceof VehicleEntity vehicle)) return;
        if (!(player.getVehicle() instanceof FvEntity) && !(player.getVehicle() instanceof Zenit_2C6Entity)) return;
        
        // Рендерим только для водителя (seat 0)
        if (vehicle.getSeatIndex(player) != 0) return;
        
        // Рендерим только от первого лица или в зуме
        if (!mc.options.getCameraType().isFirstPerson() && !ClientEventHandler.zoomVehicle) return;
        
        renderHud(vehicle, player, guiGraphics, partialTick, screenWidth, screenHeight);
    }
    
    private void renderHud(VehicleEntity vehicle, Player player, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        PoseStack poseStack = guiGraphics.pose();
        
        // Цвет HUD (зелёный по умолчанию, можно кастомизировать)
        int hudColor = getHudColor(vehicle);
        
        poseStack.pushPose();
        
        // Настройка рендера
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        
        // 1. Рамка экрана (TV frame)
        renderFrame(guiGraphics, screenWidth, screenHeight);
        
        // 2. Компас
        renderCompass(guiGraphics, player, screenWidth, screenHeight, hudColor);
        
        // 3. Скорость
        renderSpeed(guiGraphics, vehicle, screenWidth, screenHeight, hudColor);
        
        // 4. Здоровье
        renderHealth(guiGraphics, vehicle, screenWidth, screenHeight, hudColor);
        
        // 5. Иконки состояния машины
        renderVehicleStatus(guiGraphics, vehicle, screenWidth, screenHeight);
        
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }
    
    /**
     * Рендерит рамку экрана
     */
    private void renderFrame(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        int addW = (screenWidth / screenHeight) * 48;
        int addH = (screenWidth / screenHeight) * 27;
        
        guiGraphics.blit(FRAME, 
            -addW / 2, -addH / 2, 
            0, 0, 
            screenWidth + addW, screenHeight + addH, 
            screenWidth + addW, screenHeight + addH);
    }
    
    /**
     * Рендерит компас
     */
    private void renderCompass(GuiGraphics guiGraphics, Player player, int screenWidth, int screenHeight, int color) {
        float yaw = player.getYRot();
        
        // Применяем цвет
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        RenderSystem.setShaderColor(r, g, b, 1.0f);
        
        int compassX = screenWidth / 2 - 128;
        int compassY = 10;
        float compassU = 128 + (64F / 45 * yaw);
        
        guiGraphics.blit(COMPASS, compassX, compassY, compassU, 0, 256, 16, 512, 16);
        
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
    
    /**
     * Рендерит скорость
     */
    private void renderSpeed(GuiGraphics guiGraphics, VehicleEntity vehicle, int screenWidth, int screenHeight, int color) {
        double speed = vehicle.getDeltaMovement().length() * 20 * 3.6; // m/s to km/h
        String speedStr = String.format("%.0f km/h", speed);
        
        guiGraphics.drawString(
            Minecraft.getInstance().font, 
            speedStr, 
            screenWidth / 2 + 160, 
            screenHeight / 2 - 48, 
            color, 
            false
        );
    }
    
    /**
     * Рендерит здоровье
     */
    private void renderHealth(GuiGraphics guiGraphics, VehicleEntity vehicle, int screenWidth, int screenHeight, int color) {
        int healthPercent = (int) (vehicle.getHealth() / vehicle.getMaxHealth() * 100);
        String healthStr = healthPercent + "%";
        
        guiGraphics.drawString(
            Minecraft.getInstance().font, 
            healthStr, 
            screenWidth / 2 - 165, 
            screenHeight / 2 - 46, 
            color, 
            false
        );
    }
    
    /**
     * Рендерит иконки состояния машины
     */
    private void renderVehicleStatus(GuiGraphics guiGraphics, VehicleEntity vehicle, int screenWidth, int screenHeight) {
        int bodyX = screenWidth / 2 + 96;
        int bodyY = screenHeight - 72;
        
        // Можно добавить логику для отображения повреждений разных частей
        guiGraphics.blit(BODY, bodyX, bodyY, 0, 0, 32, 32, 32, 32);
        guiGraphics.blit(LEFT_WHEEL, bodyX, bodyY, 0, 0, 32, 32, 32, 32);
        guiGraphics.blit(RIGHT_WHEEL, bodyX, bodyY, 0, 0, 32, 32, 32, 32);
        guiGraphics.blit(ENGINE, bodyX, bodyY, 0, 0, 32, 32, 32, 32);
    }
    
    /**
     * Получает цвет HUD для конкретной машины
     * Можно кастомизировать для разных машин
     */
    private int getHudColor(VehicleEntity vehicle) {
        if (vehicle instanceof FvEntity) {
            return 0x66FF00; // Зелёный для FV
        } else if (vehicle instanceof Zenit_2C6Entity) {
            return 0x66FF00; // Зелёный для Zenit
        }
        return 0x66FF00; // По умолчанию зелёный
    }
}
