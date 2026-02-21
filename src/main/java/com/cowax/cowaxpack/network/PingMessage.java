package com.cowax.cowaxpack.network;

import com.cowax.cowaxpack.CowaxPack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class PingMessage {
    public final double x;
    public final double y;
    public final double z;
    public final int entityId;
    public final String vehicleType;

    public PingMessage(double x, double y, double z, int entityId, String vehicleType) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.entityId = entityId;
        this.vehicleType = vehicleType;
    }

    public PingMessage(FriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.entityId = buf.readInt();
        this.vehicleType = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeInt(entityId);
        buf.writeUtf(vehicleType);
    }

    public static void handler(PingMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender != null) {
                // Рассылаем всем игрокам
                CowaxPack.NETWORK.send(PacketDistributor.ALL.noArg(), new ClientPingMessage(msg.x, msg.y, msg.z, msg.entityId, msg.vehicleType));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
