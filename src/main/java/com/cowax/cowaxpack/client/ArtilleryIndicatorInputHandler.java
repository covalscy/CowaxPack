package com.cowax.cowaxpack.client;

import com.cowax.cowaxpack.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ArtilleryIndicatorInputHandler {

    @SubscribeEvent
    public static void onMouseButton(InputEvent.MouseButton.Pre event) {
        if (notInGame()) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack stack = player.getMainHandItem();
        int button = event.getButton();

        // Средняя кнопка мыши - установка цели
        if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
            // if (stack.is(ModItems.ARTILLERY_INDICATOR.get())) {
            if (false) {
                if (event.getAction() == GLFW.GLFW_PRESS) {
                    ArtilleryIndicatorClientHandler.handleMiddleClick();
                }
                event.setCanceled(true);
            }
        }

        // ЛКМ - стрельба
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            // if (stack.is(ModItems.ARTILLERY_INDICATOR.get())) {
            if (false) {
                if (event.getAction() == GLFW.GLFW_PRESS) {
                    ArtilleryIndicatorClientHandler.handleFirePress();
                } else if (event.getAction() == GLFW.GLFW_RELEASE) {
                    ArtilleryIndicatorClientHandler.handleFireRelease();
                }
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (notInGame()) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack stack = player.getMainHandItem();

        // Прокрутка колеса мыши - изменение зума
        // if (player.isUsingItem() && player.getUseItem().is(ModItems.ARTILLERY_INDICATOR.get())) {
        if (false && player.isUsingItem()) {
            double scroll = event.getScrollDelta();
            ArtilleryIndicatorClientHandler.handleMouseScroll(scroll);
            event.setCanceled(true);
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
