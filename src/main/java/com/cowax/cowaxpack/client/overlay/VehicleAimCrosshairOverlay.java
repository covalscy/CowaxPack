package com.cowax.cowaxpack.client.overlay;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

/**
 * Общий прицел для всех машин из SuperbWarfare
 * Показывает куда смотрит камера игрока (куда будет поворачиваться башня)
 * Как в War Thunder / World of Tanks
 */
public class VehicleAimCrosshairOverlay implements IGuiOverlay {
    
    public static final ResourceLocation ID = new ResourceLocation("cowaxpack", "vehicle_aim_crosshair");
    
    // Размер крестика
    private static final int CROSSHAIR_SIZE = 20;
    private static final int LINE_THICKNESS = 2;
    
    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        
        // Debug: проверяем есть ли вообще vehicle
        if (player.getVehicle() != null && mc.player.tickCount % 100 == 0) {
            // Закомментировано для релиза
            // mc.player.displayClientMessage(net.minecraft.network.chat.Component.literal("§e[DEBUG] In vehicle: " + player.getVehicle().getClass().getSimpleName()), false);
        }
        
        // Проверяем, что игрок в машине (любой)
        if (player.getVehicle() == null) return;
        
        // Проверяем конкретные типы машин
        boolean isCowaxVehicle = player.getVehicle() instanceof com.cowax.cowaxpack.entity.FvEntity 
                               || player.getVehicle() instanceof com.cowax.cowaxpack.entity.Zenit_2C6Entity;
        
        boolean isSuperWarfareVehicle = player.getVehicle() instanceof VehicleEntity;
        
        if (!isCowaxVehicle && !isSuperWarfareVehicle) return;
        
        // Рендерим только от первого лица
        if (!mc.options.getCameraType().isFirstPerson()) return;
        
        // Для машин SuperbWarfare проверяем seat index и тип транспорта
        if (isSuperWarfareVehicle) {
            VehicleEntity vehicle = (VehicleEntity) player.getVehicle();
            int seatIndex = vehicle.getSeatIndex(player);
            if (seatIndex != 0) return;
            
            // Исключаем летающий транспорт (самолёты и вертолёты)
            com.atsuishio.superbwarfare.data.vehicle.subdata.VehicleType vehicleType = vehicle.getVehicleType();
            if (vehicleType == com.atsuishio.superbwarfare.data.vehicle.subdata.VehicleType.AIRPLANE ||
                vehicleType == com.atsuishio.superbwarfare.data.vehicle.subdata.VehicleType.HELICOPTER) {
                return;
            }
        }
        
        renderAimCrosshair(guiGraphics, screenWidth, screenHeight);
    }
    
    private void renderAimCrosshair(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        PoseStack poseStack = guiGraphics.pose();
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        
        poseStack.pushPose();
        
        // Настройка рендера с прозрачностью
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        
        // Получаем позицию камеры и направление взгляда
        net.minecraft.client.Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        Vec3 lookVec = player.getLookAngle();
        
        // Делаем raycast куда смотрит камера (до 512 блоков)
        net.minecraft.world.level.ClipContext clipContext = new net.minecraft.world.level.ClipContext(
            cameraPos,
            cameraPos.add(lookVec.scale(512)),
            net.minecraft.world.level.ClipContext.Block.OUTLINE,
            net.minecraft.world.level.ClipContext.Fluid.NONE,
            player
        );
        
        net.minecraft.world.phys.BlockHitResult hitResult = player.level().clip(clipContext);
        Vec3 hitPos = hitResult.getLocation();
        
        // Проецируем точку попадания на экран
        Vec3 screenPos = com.atsuishio.superbwarfare.tools.VectorUtil.worldToScreen(hitPos);
        
        // Проверяем что точка видна на экране
        if (!com.atsuishio.superbwarfare.tools.VectorUtil.canSee(hitPos)) {
            poseStack.popPose();
            return;
        }
        
        // Используем float для точности
        float centerX = (float) screenPos.x;
        float centerY = (float) screenPos.y;
        
        // Ограничиваем координаты границами экрана
        centerX = net.minecraft.util.Mth.clamp(centerX, 0, screenWidth);
        centerY = net.minecraft.util.Mth.clamp(centerY, 0, screenHeight);
        
        // Текстура прицела (общая для всех машин)
        ResourceLocation crosshairTexture = new ResourceLocation("cowaxpack", "textures/gui/rex_circle.png");
        
        // Размер прицела (уменьшен для лучшей видимости)
        float size = 16f; // Размер текстуры прицела
        
        // Устанавливаем прозрачность (0.7 = 70% непрозрачности)
        RenderSystem.setShaderColor(0.9f, 0.9f, 0.9f, 0.6f);
        
        // Рисуем текстуру прицела по центру точки прицеливания с точными координатами
        com.atsuishio.superbwarfare.client.RenderHelper.preciseBlit(
            guiGraphics, 
            crosshairTexture, 
            centerX - size / 2f, centerY - size / 2f,  // позиция (центрируем с float точностью)
            0,                                          // z-level
            0, 0,                                       // u, v начало в текстуре
            size, size,                                 // ширина, высота на экране
            size, size);                                // ширина, высота текстуры
        
        // Возвращаем цвет обратно
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        
        poseStack.popPose();
    }
    
    /**
     * Рисует линию между двумя точками
     */
    private void drawLine(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int thickness, int color) {
        // Используем fill для рисования линии
        // Вычисляем угол и длину
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);
        double angle = Math.atan2(dy, dx);
        
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        
        // Перемещаемся в начальную точку
        poseStack.translate(x1, y1, 0);
        // Поворачиваем на угол линии
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotation((float) angle));
        
        // Рисуем прямоугольник как линию
        guiGraphics.fill(0, -thickness / 2, (int) length, thickness / 2, color);
        
        poseStack.popPose();
    }
    
    /**
     * Рисует круг (заполненный)
     */
    private void drawCircle(GuiGraphics guiGraphics, int centerX, int centerY, int radius, int color) {
        // Простая реализация через fill маленьких квадратов
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                if (x * x + y * y <= radius * radius) {
                    guiGraphics.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, color);
                }
            }
        }
    }
}
