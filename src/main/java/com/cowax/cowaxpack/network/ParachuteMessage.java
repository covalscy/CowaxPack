package com.cowax.cowaxpack.network;

import com.cowax.cowaxpack.init.ModItems;
import com.cowax.cowaxpack.init.CowaxSounds;
import com.cowax.cowaxpack.item.curio.ParachuteItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.Supplier;

public enum ParachuteMessage {
    INSTANCE;

    public static void handler(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();

            if (player == null) return;

            CuriosApi.getCuriosInventory(player).ifPresent(
                    c -> c.findFirstCurio(ModItems.PARACHUTE.get()).ifPresent(
                            s -> {
                                var stack = s.stack();
                                if (!player.getCooldowns().isOnCooldown(stack.getItem())) {
                                    if (!stack.getOrCreateTag().getBoolean(ParachuteItem.TAG_OPEN) && player.getDeltaMovement().y < -0.1 && player.fallDistance > 1) {
                                        stack.getOrCreateTag().putBoolean(ParachuteItem.TAG_OPEN, true);
                                        player.getCooldowns().addCooldown(stack.getItem(), 10);
                                        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), CowaxSounds.PARACHUTE_OPEN.get(), SoundSource.PLAYERS, 1f, 1);
                                    } else if (stack.getOrCreateTag().getBoolean(ParachuteItem.TAG_OPEN)) {
                                        stack.getOrCreateTag().putBoolean(ParachuteItem.TAG_OPEN, false);
                                        player.getCooldowns().addCooldown(stack.getItem(), 10);
                                        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), CowaxSounds.PARACHUTE_CLOSE.get(), SoundSource.PLAYERS, 1f, 1);
                                    }
                                }
                            }
                    )
            );

            // Fallback: Check main inventory if not found/processed in Curios
            boolean foundInCurios = CuriosApi.getCuriosInventory(player).map(c -> c.findFirstCurio(ModItems.PARACHUTE.get()).isPresent()).orElse(false);
            
            if (!foundInCurios) {
               player.getInventory().items.stream()
                       .filter(stack -> stack.getItem() == ModItems.PARACHUTE.get())
                       .findFirst()
                       .ifPresent(stack -> {
                           if (!player.getCooldowns().isOnCooldown(stack.getItem())) {
                               if (!stack.getOrCreateTag().getBoolean(ParachuteItem.TAG_OPEN) && player.getDeltaMovement().y < -0.1 && player.fallDistance > 1) {
                                   stack.getOrCreateTag().putBoolean(ParachuteItem.TAG_OPEN, true);
                                   player.getCooldowns().addCooldown(stack.getItem(), 10);
                                   player.level().playSound(null, player.getX(), player.getY(), player.getZ(), CowaxSounds.PARACHUTE_OPEN.get(), SoundSource.PLAYERS, 1f, 1);
                               } else if (stack.getOrCreateTag().getBoolean(ParachuteItem.TAG_OPEN)) {
                                   stack.getOrCreateTag().putBoolean(ParachuteItem.TAG_OPEN, false);
                                   player.getCooldowns().addCooldown(stack.getItem(), 10);
                                   player.level().playSound(null, player.getX(), player.getY(), player.getZ(), CowaxSounds.PARACHUTE_CLOSE.get(), SoundSource.PLAYERS, 1f, 1);
                               }
                           }
                       });
            }
        });
        context.setPacketHandled(true);
    }
}
