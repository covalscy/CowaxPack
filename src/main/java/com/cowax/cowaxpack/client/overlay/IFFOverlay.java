package com.cowax.cowaxpack.client.overlay;

import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.config.ClientConfig;
import com.cowax.cowaxpack.init.ModItems;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = CowaxPack.MODID, value = Dist.CLIENT)
public class IFFOverlay implements IGuiOverlay {

    public static final String ID = CowaxPack.MODID + "_iff";

    public static final ResourceLocation FRIENDLY_INDICATOR = new ResourceLocation("superbwarfare", "textures/overlay/teammate/friendly_indicator.png");
    public static final ResourceLocation FRIENDLY_AIRCRAFT = new ResourceLocation("superbwarfare", "textures/overlay/teammate/friendly_aircraft.png");
    public static final ResourceLocation FRIENDLY_TANK = new ResourceLocation("superbwarfare", "textures/overlay/teammate/friendly_tank.png");
    public static final ResourceLocation FRIENDLY_APC = new ResourceLocation("superbwarfare", "textures/overlay/teammate/friendly_apc.png");
    public static final ResourceLocation FRIENDLY_AA = new ResourceLocation("superbwarfare", "textures/overlay/teammate/friendly_aa.png");
    public static final ResourceLocation FRIENDLY_CAR = new ResourceLocation("superbwarfare", "textures/overlay/teammate/friendly_car.png");
    public static final ResourceLocation FRIENDLY_ARTILLERY = new ResourceLocation("superbwarfare", "textures/overlay/teammate/friendly_artillery.png");
    public static final ResourceLocation FRIENDLY_BOAT = new ResourceLocation("superbwarfare", "textures/overlay/teammate/friendly_boat.png");
    public static final ResourceLocation FRIENDLY_DEFENSE = new ResourceLocation("superbwarfare", "textures/overlay/teammate/friendly_defense.png");
    public static final ResourceLocation FRIENDLY_DRONE = new ResourceLocation("superbwarfare", "textures/overlay/teammate/friendly_drone.png");
    public static final ResourceLocation FRIENDLY_HELICOPTER = new ResourceLocation("superbwarfare", "textures/overlay/teammate/friendly_helicopter.png");
    public static final ResourceLocation FRIENDLY_MINE = new ResourceLocation("superbwarfare", "textures/overlay/teammate/friendly_mine.png");

    private static Matrix4f lastModelViewMatrix;
    private static Matrix4f lastProjectionMatrix;

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            lastModelViewMatrix = new Matrix4f(event.getPoseStack().last().pose());
            lastProjectionMatrix = event.getProjectionMatrix();
        }
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = gui.getMinecraft();
        Player player = mc.player;
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();

        if (player == null) return;

        boolean hasIff = false;
        
        // Check Curios
        if (CuriosApi.getCuriosInventory(player).resolve().flatMap(c -> c.findFirstCurio(ModItems.IFF.get())).isPresent()) {
            hasIff = true;
        }
        
        // Check Inventory
        if (!hasIff) {
            for (ItemStack stack : player.getInventory().items) {
                if (stack.getItem() == ModItems.IFF.get()) {
                    hasIff = true;
                    break;
                }
            }
        }

        if (hasIff) {
             double renderRange = ClientConfig.IFF_RENDER_RANGE.get();
             List<Entity> entities = findFriendlyEntities(player, renderRange);
             Set<Integer> renderedTargets = new HashSet<>();

            for (var e : entities) {
                if (e != null && e != player && e != player.getVehicle()) {
                    Entity team = resolveMarkerTarget(e);
                    if (!renderedTargets.add(team.getId())) {
                        continue;
                    }

                    RenderSystem.disableDepthTest();
                    RenderSystem.depthMask(false);
                    RenderSystem.enableBlend();
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                    if (checkNoClip(player, team, cameraPos)) {
                        RenderSystem.setShaderColor(1, 1, 1, 1);
                    } else {
                        RenderSystem.setShaderColor(1, 1, 1, 0.4f);
                    }

                    Vec3 pos = team.getBoundingBox().getCenter();
                    ScreenPos point = worldToScreen(pos, mc);

                    if (point == null) {
                        continue;
                    }

                    // Fix: Hide marker if behind camera or off-screen
                    if (point.isBehindCamera() || point.x < 0 || point.x > screenWidth || point.y < 0 || point.y > screenHeight) {
                        continue;
                    }

                    float xf = point.x;
                    float yf = point.y;
                    ResourceLocation icon = getResourceLocation(team);

                    RenderHelper.preciseBlit(guiGraphics, icon, Mth.clamp(xf - 6, 0, screenWidth - 12), Mth.clamp(yf - 6, 0, screenHeight - 12), 0, 0, 12, 12, 12, 12);
                }
            }
        }
    }

    private static ResourceLocation getResourceLocation(Entity entity) {
        ResourceLocation icon = FRIENDLY_INDICATOR;

        if (entity instanceof Boat) {
            icon = FRIENDLY_BOAT;
        } else if (entity instanceof VehicleEntity vehicle) {
            icon = switch (vehicle.getVehicleType()) {
                case AIRPLANE -> FRIENDLY_AIRCRAFT;
                case HELICOPTER -> FRIENDLY_HELICOPTER;
                case APC -> FRIENDLY_APC;
                case CAR -> FRIENDLY_CAR;
                case AA -> FRIENDLY_AA;
                case TANK -> FRIENDLY_TANK;
                case ARTILLERY -> FRIENDLY_ARTILLERY;
                case DRONE -> FRIENDLY_DRONE;
                case BOAT -> FRIENDLY_BOAT;
                case DEFENSE -> FRIENDLY_DEFENSE;
                default -> FRIENDLY_INDICATOR;
            };
        } else if (entity.getType().toString().contains("mine")) {
            icon = FRIENDLY_MINE;
        }
        return icon;
    }

    private static Entity resolveMarkerTarget(Entity entity) {
        Entity root = entity;
        while (root.getVehicle() != null) {
            root = root.getVehicle();
        }

        if (root instanceof VehicleEntity || root instanceof Boat) {
            return root;
        }

        return entity;
    }

    private static List<Entity> findFriendlyEntities(Player player, double range) {
        List<Entity> result = new ArrayList<>();
        double rangeSqr = range * range;

        for (Entity candidate : player.level().getEntities(player, player.getBoundingBox().inflate(range))) {
            if (candidate == null || candidate == player) continue;
            if (!candidate.isAlive()) continue;
            if (candidate.distanceToSqr(player) > rangeSqr) continue;
            if (!isValidIFFTarget(candidate)) continue;
            if (!isFriendlyTo(player, candidate)) continue;

            result.add(candidate);
        }

        return result;
    }

    private static boolean isValidIFFTarget(Entity entity) {
        if (entity instanceof Projectile) return false;
        return !(entity instanceof net.minecraft.world.entity.decoration.HangingEntity);
    }

    private static boolean isFriendlyTo(Player player, Entity target) {
        if (player == target) return true;

        if (target.getTeam() != null && target.getTeam() == player.getTeam()) {
            return true;
        }

        if (target instanceof OwnableEntity ownableEntity) {
            Entity owner = ownableEntity.getOwner();
            if (owner != null && owner.getTeam() != null && owner.getTeam() == player.getTeam()) {
                return true;
            }
        }

        for (Entity passenger : target.getPassengers()) {
            if (passenger.getTeam() != null && passenger.getTeam() == player.getTeam()) {
                return true;
            }
        }

        if (target.getVehicle() != null) {
            Entity vehicle = target.getVehicle();
            if (vehicle.getTeam() != null && vehicle.getTeam() == player.getTeam()) {
                return true;
            }
            for (Entity passenger : vehicle.getPassengers()) {
                if (passenger.getTeam() != null && passenger.getTeam() == player.getTeam()) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean checkNoClip(Player player, Entity teammate, Vec3 pos) {
        return player.level().clip(new ClipContext(pos, teammate.position(),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, player)).getType() != HitResult.Type.BLOCK;
    }

    private static ScreenPos worldToScreen(Vec3 worldPos, Minecraft mc) {
        if (lastModelViewMatrix == null || lastProjectionMatrix == null) {
            return null;
        }

        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        Vec3 relativePos = cameraPos.reverse().add(worldPos);
        Vector4f worldPosRel = new Vector4f((float) relativePos.x, (float) relativePos.y, (float) relativePos.z, 1f);

        worldPosRel.mul(lastModelViewMatrix);
        worldPosRel.mul(lastProjectionMatrix);

        float depth = worldPosRel.w;
        if (depth != 0) {
            worldPosRel.div(depth);
        }

        return new ScreenPos(
                mc.getWindow().getGuiScaledWidth() * (0.5f + worldPosRel.x * 0.5f),
                mc.getWindow().getGuiScaledHeight() * (0.5f - worldPosRel.y * 0.5f),
                depth
        );
    }

    private static class ScreenPos {
        final float x;
        final float y;
        final float depth;

        ScreenPos(float x, float y, float depth) {
            this.x = x;
            this.y = y;
            this.depth = depth;
        }

        boolean isBehindCamera() {
            return depth <= 0;
        }
    }
}
