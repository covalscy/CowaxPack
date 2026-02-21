package com.cowax.cowaxpack.client.overlay;

import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.client.animation.AnimationCurves;
import com.atsuishio.superbwarfare.client.animation.AnimationTimer;
import com.atsuishio.superbwarfare.data.gun.AmmoConsumer;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModKeyMappings;
import com.cowax.cowaxpack.entity.FvEntity;
import com.cowax.cowaxpack.entity.Zenit_2C6Entity;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import static com.atsuishio.superbwarfare.client.RenderHelper.preciseBlit;
import static com.atsuishio.superbwarfare.client.overlay.CrossHairOverlay.*;

/**
 * Кастомизируемый HUD для машин CowaxPack (FV и Zenit)
 * Основан на VehicleHudOverlay из SuperbWarfare
 */
public class CowaxVehicleHudOverlay implements IGuiOverlay {
    
    public static final ResourceLocation ID = new ResourceLocation("cowaxpack", "vehicle_hud");
    public static final int ANIMATION_TIME = 300;
    
    // Текстуры из SuperbWarfare
    private static final ResourceLocation ARMOR = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/base/armor.png");
    private static final ResourceLocation ENERGY = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/base/energy.png");
    private static final ResourceLocation VALUE_BAR = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/base/value_bar.png");
    private static final ResourceLocation VALUE_FRAME = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/base/value_frame.png");
    
    private static final ResourceLocation SELECTED = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/weapon/frame/selected.png");
    private static final ResourceLocation SWITCH_AMMO = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/weapon/frame/switch_ammo.png");
    private static final ResourceLocation NUMBER = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/weapon/frame/number.png");
    
    private static final ResourceLocation[] FRAMES = {
            new ResourceLocation("superbwarfare", "textures/overlay/vehicle/weapon/frame/frame_1.png"),
            new ResourceLocation("superbwarfare", "textures/overlay/vehicle/weapon/frame/frame_2.png"),
            new ResourceLocation("superbwarfare", "textures/overlay/vehicle/weapon/frame/frame_3.png"),
            new ResourceLocation("superbwarfare", "textures/overlay/vehicle/weapon/frame/frame_4.png"),
            new ResourceLocation("superbwarfare", "textures/overlay/vehicle/weapon/frame/frame_5.png"),
            new ResourceLocation("superbwarfare", "textures/overlay/vehicle/weapon/frame/frame_6.png"),
            new ResourceLocation("superbwarfare", "textures/overlay/vehicle/weapon/frame/frame_7.png"),
            new ResourceLocation("superbwarfare", "textures/overlay/vehicle/weapon/frame/frame_8.png"),
            new ResourceLocation("superbwarfare", "textures/overlay/vehicle/weapon/frame/frame_9.png")
    };
    
    private static final AnimationTimer[] WEAPON_SLOTS_TIMER = AnimationTimer.createTimers(9, ANIMATION_TIME, AnimationCurves.EASE_OUT_CIRC);
    private static final AnimationTimer WEAPON_INDEX_UPDATE_TIMER = new AnimationTimer(ANIMATION_TIME).animation(AnimationCurves.EASE_OUT_CIRC);
    
    private static boolean wasRenderingWeapons = false;
    private static int oldWeaponIndex = 0;
    private static int oldRenderWeaponIndex = 0;
    
    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Player player = gui.getMinecraft().player;
        
        if (!shouldRenderHud(player)) {
            wasRenderingWeapons = false;
            return;
        }
        
        Entity entity = player.getVehicle();
        if (!(entity instanceof VehicleEntity vehicle)) return;
        
        // Проверяем что это наша машина
        if (!(entity instanceof FvEntity) && !(entity instanceof Zenit_2C6Entity)) return;
        
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        
        // Рендерим здоровье и энергию
        renderHealthAndEnergy(guiGraphics, vehicle, screenWidth, screenHeight);
        
        // Рендерим информацию об оружии
        renderWeaponInfo(guiGraphics, vehicle, screenWidth, screenHeight);
        
        poseStack.popPose();
    }
    
    private static boolean shouldRenderHud(Player player) {
        if (player == null) return false;
        Entity vehicle = player.getVehicle();
        return !player.isSpectator() && (vehicle instanceof FvEntity || vehicle instanceof Zenit_2C6Entity);
    }
    
    private static void renderHealthAndEnergy(GuiGraphics guiGraphics, VehicleEntity vehicle, int w, int h) {
        // Энергия (если есть)
        if (vehicle.hasEnergyStorage()) {
            float energy = vehicle.getEnergy();
            float maxEnergy = vehicle.getMaxEnergy();
            
            preciseBlit(guiGraphics, ENERGY, 10, h - 22, 100, 0, 0, 8, 8, 8, 8);
            preciseBlit(guiGraphics, VALUE_FRAME, 20, h - 21, 100, 0, 0, 60, 6, 60, 6);
            preciseBlit(guiGraphics, VALUE_BAR, 20, h - 21, 100, 0, 0, (int) (60 * energy / maxEnergy), 6, 60, 6);
        }
        
        // Здоровье
        float health = vehicle.getHealth();
        float maxHealth = vehicle.getMaxHealth();
        
        preciseBlit(guiGraphics, ARMOR, 10, h - 13, 100, 0, 0, 8, 8, 8, 8);
        preciseBlit(guiGraphics, VALUE_FRAME, 20, h - 12, 100, 0, 0, 60, 6, 60, 6);
        preciseBlit(guiGraphics, VALUE_BAR, 20, h - 12, 100, 0, 0, (int) (60 * health / maxHealth), 6, 60, 6);
    }
    
    private static void renderWeaponInfo(GuiGraphics guiGraphics, VehicleEntity vehicle, int w, int h) {
        Player player = Minecraft.getInstance().player;
        
        if (!vehicle.banHand(player)) return;
        if (!vehicle.hasWeapon()) return;
        
        var temp = wasRenderingWeapons;
        wasRenderingWeapons = false;
        
        assert player != null;
        
        int index = vehicle.getSeatIndex(player);
        if (index == -1) return;
        
        var weapons = vehicle.computed().seats().get(index).weapons().stream().map(vehicle::getGunData).toList();
        if (weapons.isEmpty()) return;
        
        int weaponIndex = vehicle.getWeaponIndex(index);
        if (weaponIndex == -1) return;
        
        wasRenderingWeapons = temp;
        
        var currentTime = System.currentTimeMillis();
        
        // Инициализация анимации
        if (!wasRenderingWeapons) {
            WEAPON_SLOTS_TIMER[weaponIndex].beginForward(currentTime);
            
            if (oldWeaponIndex != weaponIndex) {
                WEAPON_SLOTS_TIMER[oldWeaponIndex].endBackward(currentTime);
                oldWeaponIndex = weaponIndex;
                oldRenderWeaponIndex = weaponIndex;
            }
            
            WEAPON_INDEX_UPDATE_TIMER.beginForward(currentTime);
        }
        
        // Обновление анимации при смене оружия
        if (weaponIndex != oldWeaponIndex) {
            WEAPON_SLOTS_TIMER[weaponIndex].forward(currentTime);
            WEAPON_SLOTS_TIMER[oldWeaponIndex].backward(currentTime);
            
            oldRenderWeaponIndex = oldWeaponIndex;
            oldWeaponIndex = weaponIndex;
            
            WEAPON_INDEX_UPDATE_TIMER.beginForward(currentTime);
        }
        
        var pose = guiGraphics.pose();
        pose.pushPose();
        
        int frameIndex = 0;
        
        for (int i = weapons.size() - 1; i >= 0 && i < 9; i--) {
            var weapon = weapons.get(i);
            var frame = FRAMES[i];
            
            pose.pushPose();
            
            float xOffset;
            var maxXOffset = 37;
            
            var currentSlotTimer = WEAPON_SLOTS_TIMER[i];
            var progress = currentSlotTimer.getProgress(currentTime);
            
            RenderSystem.setShaderColor(1, 1, 1, Mth.lerp(progress, 0.2f, 1));
            xOffset = Mth.lerp(progress, maxXOffset, 0);
            
            preciseBlit(guiGraphics, frame, w - 85 + xOffset, h - frameIndex * 18 - 20, 100, 0, 0, 75, 16, 75, 16);
            
            var data = vehicle.getGunData(vehicle.getSeatIndex(player), i);
            if (data == null) {
                pose.popPose();
                continue;
            }
            
            boolean selected = i == weaponIndex;
            
            // Индикатор выбранного оружия
            if (selected) {
                var startY = Mth.lerp(progress,
                        h - (weapons.size() - 1 - oldRenderWeaponIndex) * 18 - 16,
                        h - (weapons.size() - 1 - weaponIndex) * 18 - 16
                );
                
                preciseBlit(guiGraphics, SELECTED, w - 95, startY, 100, 0, 0, 8, 8, 8, 8);
                
                // Количество патронов
                var ammoCount = vehicle.getAmmoCount(player);
                
                if (ammoCount == Integer.MAX_VALUE) {
                    preciseBlit(guiGraphics, NUMBER, w - 28 + xOffset, h - frameIndex * 18 - 15, 100, 58, 0, 10, 7.5f, 75, 7.5f);
                } else {
                    boolean percent = data.selectedAmmoConsumer().type == AmmoConsumer.AmmoConsumeType.ENERGY;
                    if (percent) {
                        ammoCount /= (int) Math.max(1, (double) vehicle.getMaxEnergy() / 100);
                    }
                    renderNumber(guiGraphics, ammoCount, percent, w - 20 + xOffset, h - frameIndex * 18 - 15.5f, 0.25f);
                }
            }
            
            // Иконка оружия
            preciseBlit(guiGraphics, weapon.compute().icon, w - 85 + xOffset, h - frameIndex * 18 - 20, 100, 0, 0, 75, 16, 75, 16);
            
            // Индикатор смены типа боеприпасов
            int size = data.getDefault().getAmmoConsumers().size();
            if (selected && size > 1) {
                preciseBlit(guiGraphics, SWITCH_AMMO, w - 13 + xOffset, h - frameIndex * 18 - 20, 0, 0, 0, 16, 16, 16, 16);
                
                String string = "[" + ModKeyMappings.FIRE_MODE.getKey().getDisplayName().getString() + "]";
                int width = Minecraft.getInstance().font.width(string);
                
                pose.pushPose();
                pose.scale(0.6f, 0.6f, 1.0f);
                float xPos = w - 6f + xOffset;
                
                if (width >= 7 / 0.6f) {
                    RenderHelper.renderScrollingString(guiGraphics, Minecraft.getInstance().font,
                            Component.literal(string),
                            0.6f,
                            (int) ((xPos - 3f) / 0.6f), (int) ((h - frameIndex * 18 - 14f) / 0.6f),
                            (int) ((xPos + 5f) / 0.6f), (int) ((h - frameIndex * 18 - 4f) / 0.6f),
                            0xFFFFFF);
                } else {
                    guiGraphics.drawString(
                            Minecraft.getInstance().font,
                            string,
                            (xPos + 3f - width / 2f) / 0.6f,
                            (h - frameIndex * 18 - 14f) / 0.6f,
                            0xFFFFFF,
                            false
                    );
                }
                
                pose.popPose();
            }
            
            // Индикатор перезарядки
            var computed = data.compute();
            if (data.reloading()) {
                int totalReloadTime, currentReloadTime;
                totalReloadTime = data.reload.empty() ? computed.emptyReloadTime : computed.normalReloadTime;
                currentReloadTime = data.reload.reloadTimer.get();
                
                float reloadProgress = (float) (totalReloadTime - currentReloadTime) / totalReloadTime;
                float alpha = Mth.lerp(progress, 0.4f, 1);
                
                if (currentReloadTime > 0 && currentReloadTime < totalReloadTime) {
                    RenderHelper.renderCircularRing(
                            guiGraphics,
                            w - 102 + xOffset, h - frameIndex * 18 - 12,
                            0.014f, 0.010f,
                            new float[]{0f, 0f, 0f, 0.4f * alpha},
                            new float[]{1f, 1f, 1f, alpha},
                            reloadProgress,
                            true
                    );
                }
            }
            
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            
            pose.popPose();
            
            frameIndex++;
        }
        
        RenderSystem.setShaderColor(1, 1, 1, 1);
        pose.popPose();
        
        // Обновление индекса после завершения анимации
        if (oldWeaponIndex != oldRenderWeaponIndex && WEAPON_INDEX_UPDATE_TIMER.finished(currentTime)) {
            oldRenderWeaponIndex = oldWeaponIndex;
        }
        wasRenderingWeapons = true;
    }
    
    private static void renderNumber(GuiGraphics guiGraphics, int number, boolean percent, float x, float y, float scale) {
        float pX = x;
        if (percent) {
            pX -= 32 * scale;
            preciseBlit(guiGraphics, NUMBER, pX + 20 * scale, y, 100,
                    200 * scale, 0, 32 * scale, 30 * scale, 300 * scale, 30 * scale);
        }
        
        int index = 0;
        if (number == 0) {
            preciseBlit(guiGraphics, NUMBER, pX, y, 100,
                    0, 0, 20 * scale, 30 * scale, 300 * scale, 30 * scale);
        }
        
        while (number > 0) {
            int digit = number % 10;
            preciseBlit(guiGraphics, NUMBER, pX - index * 20 * scale, y, 100,
                    digit * 20 * scale, 0, 20 * scale, 30 * scale, 300 * scale, 30 * scale);
            number /= 10;
            index++;
        }
    }
}
