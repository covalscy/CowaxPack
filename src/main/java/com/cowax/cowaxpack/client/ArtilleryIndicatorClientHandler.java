package com.cowax.cowaxpack.client;

import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.init.ModItems;
import com.cowax.cowaxpack.network.ArtilleryIndicatorFireMessage;
import com.cowax.cowaxpack.network.SetFiringParametersMessage;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = CowaxPack.MODID)
public class ArtilleryIndicatorClientHandler {

    public static double artilleryIndicatorZoom = 1;
    public static double artilleryIndicatorCustomZoom = 0;
    public static int holdArtilleryIndicator = 0;
    public static boolean holdFire = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        if (event.phase == TickEvent.Phase.START) return;

        ItemStack stack = player.getMainHandItem();

        // Обработка удержания ЛКМ для стрельбы
        // if (stack.is(ModItems.ARTILLERY_INDICATOR.get()) && holdFire) {
        if (false && holdFire) {
            holdArtilleryIndicator = Mth.clamp(holdArtilleryIndicator + 1, 0, 20);
            if (holdArtilleryIndicator >= 19) {
                double distance = 500.0D;
                net.minecraft.world.phys.Vec3 eyePos = player.getEyePosition();
                net.minecraft.world.phys.Vec3 viewVec = player.getViewVector(1.0F);
                net.minecraft.world.phys.Vec3 endPos = eyePos.add(viewVec.x * distance, viewVec.y * distance, viewVec.z * distance);
                net.minecraft.world.phys.AABB aabb = player.getBoundingBox().expandTowards(viewVec.scale(distance)).inflate(1.0D, 1.0D, 1.0D);
                net.minecraft.world.phys.EntityHitResult entityHit = net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult(player, eyePos, endPos, aabb, (e) -> !e.isSpectator() && e.isPickable(), distance * distance);
                
                if (entityHit != null) {
                    net.minecraft.world.phys.Vec3 pos = entityHit.getLocation();
                    String vehicleType = "EMPTY";
                    if (entityHit.getEntity() instanceof com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity vehicle) {
                        try {
                            Object defaultData = vehicle.data().getDefault();
                            java.lang.reflect.Field typeField = defaultData.getClass().getField("type");
                            Object typeValue = typeField.get(defaultData);
                            vehicleType = ((Enum<?>)typeValue).name();
                        } catch (Exception e) {
                            vehicleType = "EMPTY";
                        }
                    }
                    CowaxPack.NETWORK.sendToServer(new com.cowax.cowaxpack.network.PingMessage(pos.x, pos.y, pos.z, entityHit.getEntity().getId(), vehicleType));
                } else {
                    net.minecraft.world.phys.HitResult result = player.pick(distance, 0.0F, false);
                    if (result.getType() != net.minecraft.world.phys.HitResult.Type.MISS) {
                        net.minecraft.world.phys.Vec3 pos = result.getLocation();
                        CowaxPack.NETWORK.sendToServer(new com.cowax.cowaxpack.network.PingMessage(pos.x, pos.y, pos.z, -1, "EMPTY"));
                    }
                }
                holdArtilleryIndicator = 0;
            }
        } else {
            holdArtilleryIndicator = 0;
        }
    }

    @SubscribeEvent
    public static void onFOVModifier(ViewportEvent.ComputeFov event) {
        Minecraft mc = Minecraft.getInstance();
        float times = (float) Math.min(mc.getDeltaFrameTime(), 1.6);
        LocalPlayer player = mc.player;
        if (player == null) return;

        ItemStack stack = player.getMainHandItem();

        double factor;

        // Зум для артиллерийского индикатора
        // if (player.isUsingItem() && player.getUseItem().is(ModItems.ARTILLERY_INDICATOR.get()) && mc.options.getCameraType() == CameraType.FIRST_PERSON) {
        if (false && player.isUsingItem() && mc.options.getCameraType() == CameraType.FIRST_PERSON) {
            factor = 4 + artilleryIndicatorCustomZoom;
        } else {
            factor = 1;
        }

        artilleryIndicatorZoom = Mth.lerp(0.3 * times, artilleryIndicatorZoom, factor);

        if (artilleryIndicatorZoom > 1.01) {
            event.setFOV(event.getFOV() / artilleryIndicatorZoom);
        }
    }

    public static void handleMouseScroll(double scroll) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        // if (player.isUsingItem() && player.getUseItem().is(ModItems.ARTILLERY_INDICATOR.get())) {
        if (false && player.isUsingItem()) {
            artilleryIndicatorCustomZoom = Mth.clamp(artilleryIndicatorCustomZoom + 0.4 * scroll, -2, 6);
        }
    }

    public static void handleMiddleClick() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack stack = player.getMainHandItem();
        // if (stack.is(ModItems.ARTILLERY_INDICATOR.get())) {
        if (false) {
            CowaxPack.NETWORK.sendToServer(SetFiringParametersMessage.INSTANCE);
        }
    }

    public static void handleFirePress() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack stack = player.getMainHandItem();
        // if (stack.is(ModItems.ARTILLERY_INDICATOR.get())) {
        if (false) {
            holdFire = true;
        }
    }

    public static void handleFireRelease() {
        holdFire = false;
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(net.minecraftforge.client.event.RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() == net.minecraftforge.client.gui.overlay.VanillaGuiOverlay.CROSSHAIR.type()) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            // if (player != null && player.isUsingItem() && player.getUseItem().is(ModItems.ARTILLERY_INDICATOR.get()) && mc.options.getCameraType() == CameraType.FIRST_PERSON) {
            if (false && player != null && player.isUsingItem() && mc.options.getCameraType() == CameraType.FIRST_PERSON) {
                
                if (holdArtilleryIndicator > 0) {
                    net.minecraft.client.gui.GuiGraphics guiGraphics = event.getGuiGraphics();
                    int width = mc.getWindow().getGuiScaledWidth();
                    int height = mc.getWindow().getGuiScaledHeight();
                    
                    int x = width / 2;
                    int y = height / 2;
                    
                    float progress = (float) holdArtilleryIndicator / 20.0f;
                    
                    int barWidth = 20;
                    int barHeight = 2;
                    int filledWidth = (int) (barWidth * progress);
                    
                    guiGraphics.fill(x - barWidth / 2, y + 10, x + barWidth / 2, y + 10 + barHeight, 0xFF000000);
                    guiGraphics.fill(x - barWidth / 2, y + 10, x - barWidth / 2 + filledWidth, y + 10 + barHeight, 0xFFFFFFFF);
                }

                event.setCanceled(true);
            }
        }
    }

    private static boolean notInGame() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return true;
        if (mc.getOverlay() != null) return true;
        if (mc.screen != null) return true;
        if (!mc.mouseHandler.isMouseGrabbed()) return true;
        return !mc.isWindowActive();
    }
}
