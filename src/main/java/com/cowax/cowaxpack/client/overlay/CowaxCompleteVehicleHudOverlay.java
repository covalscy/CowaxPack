package com.cowax.cowaxpack.client.overlay;

import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.client.animation.AnimationCurves;
import com.atsuishio.superbwarfare.client.animation.AnimationTimer;
import com.atsuishio.superbwarfare.data.gun.AmmoConsumer;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.atsuishio.superbwarfare.init.ModItems;
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
import top.theillusivec4.curios.api.CuriosApi;

import java.util.concurrent.atomic.AtomicReference;

import static com.atsuishio.superbwarfare.client.RenderHelper.preciseBlit;
import static com.atsuishio.superbwarfare.client.overlay.CrossHairOverlay.*;

/**
 * Полный кастомный HUD для FV и Zenit
 * Объединяет рамки экрана, компас, скорость, здоровье и панель оружия
 */
public class CowaxCompleteVehicleHudOverlay implements IGuiOverlay {
    
    public static final ResourceLocation ID = new ResourceLocation("cowaxpack", "complete_vehicle_hud");
    public static final int ANIMATION_TIME = 300;
    
    // Текстуры из SuperbWarfare - базовые элементы
    private static final ResourceLocation ARMOR = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/base/armor.png");
    private static final ResourceLocation ENERGY = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/base/energy.png");
    private static final ResourceLocation VALUE_BAR = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/base/value_bar.png");
    private static final ResourceLocation VALUE_FRAME = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/base/value_frame.png");
    
    // Текстуры для рамок экрана
    private static final ResourceLocation FRAME = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/tv_frame.png");
    private static final ResourceLocation COMPASS = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/base/compass.png");
    
    // Текстуры пассажиров
    private static final ResourceLocation DRIVER = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/base/driver.png");
    private static final ResourceLocation PASSENGER = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/base/passenger.png");
    
    // Текстуры оружия
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
        
        VehicleEntity vehicle = (VehicleEntity) player.getVehicle();
        
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        
        // 1. Рамка экрана (только от первого лица)
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson() || ClientEventHandler.zoomVehicle) {
            renderFrame(guiGraphics, screenWidth, screenHeight);
            renderCompass(guiGraphics, player, screenWidth, screenHeight, getHudColor(vehicle));
            renderSpeed(guiGraphics, vehicle, screenWidth, screenHeight, getHudColor(vehicle));
            renderHealthText(guiGraphics, vehicle, screenWidth, screenHeight, getHudColor(vehicle));
        }
        
        // 2. Здоровье и энергия (всегда)
        renderHealthAndEnergy(guiGraphics, vehicle, screenWidth, screenHeight);
        
        // 3. Информация о пассажирах (всегда)
        renderPassengerInfo(guiGraphics, vehicle, screenWidth, screenHeight);
        
        // 4. Информация об оружии (всегда)
        renderWeaponInfo(guiGraphics, vehicle, screenWidth, screenHeight);
        
        poseStack.popPose();
    }
    
    private static boolean shouldRenderHud(Player player) {
        if (player == null) return false;
        if (!(player.getVehicle() instanceof VehicleEntity)) return false;
        // Рендерим кастомный HUD только для FV
        // Для Zenit используется стандартный SuperbWarfare HUD с квадратом захвата
        return player.getVehicle() instanceof FvEntity;
    }
    
    private void renderFrame(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        int addW = (screenWidth / screenHeight) * 48;
        int addH = (screenWidth / screenHeight) * 27;
        
        guiGraphics.blit(FRAME, 
            -addW / 2, -addH / 2, 
            0, 0, 
            screenWidth + addW, screenHeight + addH, 
            screenWidth + addW, screenHeight + addH);
    }
    
    private void renderCompass(GuiGraphics guiGraphics, Player player, int screenWidth, int screenHeight, int color) {
        float yaw = player.getYRot();
        
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
    
    private void renderSpeed(GuiGraphics guiGraphics, VehicleEntity vehicle, int screenWidth, int screenHeight, int color) {
        double speed = vehicle.getDeltaMovement().length() * 20 * 3.6;
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
    
    private void renderHealthText(GuiGraphics guiGraphics, VehicleEntity vehicle, int screenWidth, int screenHeight, int color) {
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
    
    private static void renderHealthAndEnergy(GuiGraphics guiGraphics, VehicleEntity vehicle, int w, int h) {
        if (vehicle.hasEnergyStorage()) {
            float energy = vehicle.getEnergy();
            float maxEnergy = vehicle.getMaxEnergy();
            
            preciseBlit(guiGraphics, ENERGY, 10, h - 22, 100, 0, 0, 8, 8, 8, 8);
            preciseBlit(guiGraphics, VALUE_FRAME, 20, h - 21, 100, 0, 0, 60, 6, 60, 6);
            preciseBlit(guiGraphics, VALUE_BAR, 20, h - 21, 100, 0, 0, (int) (60 * energy / maxEnergy), 6, 60, 6);
        }
        
        float health = vehicle.getHealth();
        float maxHealth = vehicle.getMaxHealth();
        
        preciseBlit(guiGraphics, ARMOR, 10, h - 13, 100, 0, 0, 8, 8, 8, 8);
        preciseBlit(guiGraphics, VALUE_FRAME, 20, h - 12, 100, 0, 0, 60, 6, 60, 6);
        preciseBlit(guiGraphics, VALUE_BAR, 20, h - 12, 100, 0, 0, (int) (60 * health / maxHealth), 6, 60, 6);
    }
    
    private static void renderPassengerInfo(GuiGraphics guiGraphics, VehicleEntity vehicle, int w, int h) {
        var passengers = vehicle.getOrderedPassengers();
        
        int index = 0;
        for (int i = passengers.size() - 1; i >= 0; i--) {
            var passenger = passengers.get(i);
            
            int y = h - 35 - index * 12;
            AtomicReference<String> name = new AtomicReference<>("---");
            
            if (passenger != null) {
                name.set(passenger.getName().getString());
            }
            
            if (passenger instanceof Player player) {
                CuriosApi.getCuriosInventory(player).ifPresent(
                        c -> c.findFirstCurio(ModItems.DOG_TAG.get()).ifPresent(
                                s -> {
                                    if (s.stack().hasCustomHoverName()) {
                                        name.set(s.stack().getHoverName().getString());
                                    }
                                }
                        )
                );
            }
            
            guiGraphics.drawString(Minecraft.getInstance().font, name.get(), 42, y, 0x66ff00, true);
            
            String num = "[" + (i + 1) + "]";
            guiGraphics.drawString(Minecraft.getInstance().font, num, 25 - Minecraft.getInstance().font.width(num), y, 0x66ff00, true);
            
            preciseBlit(guiGraphics, index == passengers.size() - 1 ? DRIVER : PASSENGER, 30, y, 100, 0, 0, 8, 8, 8, 8);
            index++;
        }
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
        
        if (!wasRenderingWeapons) {
            WEAPON_SLOTS_TIMER[weaponIndex].beginForward(currentTime);
            
            if (oldWeaponIndex != weaponIndex) {
                WEAPON_SLOTS_TIMER[oldWeaponIndex].endBackward(currentTime);
                oldWeaponIndex = weaponIndex;
                oldRenderWeaponIndex = weaponIndex;
            }
            
            WEAPON_INDEX_UPDATE_TIMER.beginForward(currentTime);
        }
        
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
            
            if (selected) {
                var startY = Mth.lerp(progress,
                        h - (weapons.size() - 1 - oldRenderWeaponIndex) * 18 - 16,
                        h - (weapons.size() - 1 - weaponIndex) * 18 - 16
                );
                
                preciseBlit(guiGraphics, SELECTED, w - 95, startY, 100, 0, 0, 8, 8, 8, 8);
                
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
            
            preciseBlit(guiGraphics, weapon.compute().icon, w - 85 + xOffset, h - frameIndex * 18 - 20, 100, 0, 0, 75, 16, 75, 16);
            
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
    
    private int getHudColor(VehicleEntity vehicle) {
        return 0x66FF00; // Зелёный
    }
}
