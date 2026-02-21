package com.cowax.cowaxpack.network;

import com.cowax.cowaxpack.client.PingHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientPingMessage {
    public final double x;
    public final double y;
    public final double z;
    public final int entityId;
    public final String vehicleType;

    public ClientPingMessage(double x, double y, double z, int entityId, String vehicleType) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.entityId = entityId;
        this.vehicleType = vehicleType;
    }

    public ClientPingMessage(FriendlyByteBuf buf) {
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

    public static void handler(ClientPingMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Клиент получил пинг. Добавляем в рендер.
            PingHandler.addPing(msg.x, msg.y, msg.z, msg.entityId, msg.vehicleType);
        });
        ctx.get().setPacketHandled(true);
    }
}
