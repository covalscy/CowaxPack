package com.cowax.cowaxpack.network;

import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.init.CowaxSounds;
import com.cowax.cowaxpack.init.ModItems;
import com.cowax.cowaxpack.item.curio.ParachuteItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import top.theillusivec4.curios.api.CuriosApi;

public record ParachuteMessage() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ParachuteMessage> TYPE = new CustomPacketPayload.Type<>(CowaxPack.loc("parachute"));
    public static final StreamCodec<ByteBuf, ParachuteMessage> STREAM_CODEC = StreamCodec.unit(new ParachuteMessage());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ParachuteMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                CuriosApi.getCuriosInventory(player)
                        .flatMap(c -> c.findFirstCurio(ModItems.PARACHUTE.get()))
                        .ifPresent(s -> {
                            ItemStack stack = s.stack();
                            if (player.getCooldowns().isOnCooldown(stack.getItem())) return;

                            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                            if (!tag.getBoolean(ParachuteItem.TAG_OPEN) && player.getDeltaMovement().y < -0.6 && player.fallDistance > 4) {
                                tag.putBoolean(ParachuteItem.TAG_OPEN, true);
                                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                                player.getCooldowns().addCooldown(stack.getItem(), 10);
                                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                        CowaxSounds.PARACHUTE_OPEN.get(), SoundSource.PLAYERS, 1f, 1f);
                            } else if (tag.getBoolean(ParachuteItem.TAG_OPEN)) {
                                tag.putBoolean(ParachuteItem.TAG_OPEN, false);
                                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                                player.getCooldowns().addCooldown(stack.getItem(), 10);
                                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                        CowaxSounds.PARACHUTE_CLOSE.get(), SoundSource.PLAYERS, 1f, 1f);
                            }
                        });
            }
        });
    }
}
