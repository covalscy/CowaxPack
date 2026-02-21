package com.cowax.cowaxpack.mixin;

import com.atsuishio.superbwarfare.client.overlay.VehicleHudOverlay;
import com.cowax.cowaxpack.entity.FvEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin для отключения SuperbWarfare HUD когда игрок в FV
 */
@Mixin(VehicleHudOverlay.class)
public class VehicleHudOverlayMixin {
    
    @Inject(method = "shouldRenderHud", at = @At("HEAD"), cancellable = true, remap = false)
    private static void disableHudForCowaxVehicles(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (player == null) return;
        
        // Отключаем SuperbWarfare HUD только для FV
        // Для Zenit оставляем включенным чтобы показывать квадрат захвата цели
        if (player.getVehicle() instanceof FvEntity) {
            cir.setReturnValue(false);
        }
    }
}
