package com.cowax.cowaxpack.network;

// import com.atsuishio.superbwarfare.entity.vehicle.base.RemoteControllableTurret;
import com.atsuishio.superbwarfare.tools.EntityFindUtil;
import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.init.ModItems;
import com.cowax.cowaxpack.item.common.ArtilleryIndicator;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static com.cowax.cowaxpack.item.common.ArtilleryIndicator.TAG_CANNON;

public enum ArtilleryIndicatorFireMessage {
    INSTANCE;

    public static void handler(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getSender() != null) {
                Player player = context.getSender();

                ItemStack stack = player.getMainHandItem();

                // if (stack.is(ModItems.ARTILLERY_INDICATOR.get())) {
                if (false) {
                    ListTag tags = stack.getOrCreateTag().getList(TAG_CANNON, Tag.TAG_COMPOUND);
                    if (tags.isEmpty()) {
                        stack.getOrCreateTag().remove(ArtilleryIndicator.TAG_TYPE);
                        return;
                    }

                    for (int i = 0; i < tags.size(); i++) {
                        var tag = tags.getCompound(i);
                        Entity entity = EntityFindUtil.findEntity(player.level(), tag.getString("UUID"));

                        // TODO: Reimplement artillery fire logic. RemoteControllableTurret was removed.
                        /*
                        if (entity instanceof RemoteControllableTurret turret && turret.canRemoteFire()) {
                            // Задержка между выстрелами разных орудий
                            int delay = i % 5 + 1;
                            scheduleTask(delay, () -> turret.remoteFire(player));
                        }
                        */
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }

    private static void scheduleTask(int delay, Runnable task) {
        // Простая реализация задержки через сервер
        new Thread(() -> {
            try {
                Thread.sleep(delay * 50L); // 50ms на тик
                task.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
