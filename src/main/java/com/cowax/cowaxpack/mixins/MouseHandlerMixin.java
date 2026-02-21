package com.cowax.cowaxpack.mixins;

import com.cowax.cowaxpack.client.ArtilleryIndicatorMouseHandler;
import net.minecraft.client.MouseHandler;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Mixin для изменения чувствительности мыши при использовании артиллерийского индикатора
 */
@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @ModifyVariable(method = "turnPlayer()V", at = @At(value = "STORE", opcode = Opcodes.DSTORE), ordinal = 2)
    private double modifySensitivity(double original) {
        return ArtilleryIndicatorMouseHandler.changeSensitivity(original);
    }
}
