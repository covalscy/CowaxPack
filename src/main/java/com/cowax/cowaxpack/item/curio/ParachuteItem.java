package com.cowax.cowaxpack.item.curio;

import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.init.CowaxSounds;
import com.cowax.cowaxpack.init.ModItems;
import com.cowax.cowaxpack.network.ParachuteMessage;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class ParachuteItem extends Item implements ICurioItem {
    public static final String TAG_OPEN = "Open";
    private static boolean wasJumping = false;

    public ParachuteItem() {
        super(new Properties().stacksTo(1).durability(600));
        NeoForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack pStack, ItemStack pRepairCandidate) {
        return pRepairCandidate.is(Items.PHANTOM_MEMBRANE);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return CuriosApi.getCuriosInventory(slotContext.entity())
                .map(c -> c.findFirstCurio(this).isEmpty())
                .orElse(true);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        updateParachute(stack, slotContext.entity());
    }

    public static boolean isOpen(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.getBoolean(TAG_OPEN);
    }

    public static void setOpen(ItemStack stack, boolean open) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putBoolean(TAG_OPEN, open);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        ItemStack parachute = ItemStack.EMPTY;
        var curiosResult = CuriosApi.getCuriosInventory(player)
                .flatMap(c -> c.findFirstCurio(ModItems.PARACHUTE.get()));

        if (curiosResult.isPresent()) {
            parachute = curiosResult.get().stack();
        } else {
            parachute = player.getInventory().items.stream()
                    .filter(stack -> stack.getItem() == ModItems.PARACHUTE.get())
                    .findFirst()
                    .orElse(ItemStack.EMPTY);
        }

        if (!parachute.isEmpty()) {
            if (player.level().isClientSide && player instanceof net.minecraft.client.player.LocalPlayer localPlayer) {
                boolean isJumping = localPlayer.input.jumping;
                boolean open = isOpen(parachute);

                if (isJumping && !wasJumping) {
                    if (!player.onGround() &&
                            !player.isInWater() &&
                            !player.isFallFlying() &&
                            !player.getAbilities().flying) {

                        if (!open) {
                            if (player.getDeltaMovement().y < -0.6 && player.fallDistance > 1) {
                                setOpen(parachute, true);
                                player.level().playSound(player, player.getX(), player.getY(), player.getZ(),
                                        CowaxSounds.PARACHUTE_OPEN.get(), SoundSource.PLAYERS, 1f, 1);
                                PacketDistributor.sendToServer(new ParachuteMessage());
                            }
                        } else {
                            setOpen(parachute, false);
                            player.level().playSound(player, player.getX(), player.getY(), player.getZ(),
                                    CowaxSounds.PARACHUTE_CLOSE.get(), SoundSource.PLAYERS, 1f, 1);
                            PacketDistributor.sendToServer(new ParachuteMessage());
                        }
                    }
                }

                wasJumping = isJumping;
            }

            updateParachute(parachute, player);
        }
    }

    private void updateParachute(ItemStack stack, LivingEntity entity) {
        if (isOpen(stack)) {
            if ((entity.onGround() || entity.isInWater()) || entity.isFallFlying() || entity.getVehicle() != null || (entity instanceof Player player && player.getAbilities().flying)) {
                setOpen(stack, false);
                entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), CowaxSounds.PARACHUTE_CLOSE.get(), SoundSource.PLAYERS, 1f, 1);
            }
            if (entity instanceof Player player) {
                if (player.level().isClientSide) {
                    player.addDeltaMovement(new Vec3(player.getLookAngle().x, 0, player.getLookAngle().z).normalize().scale(0.05));
                    player.setDeltaMovement(player.getDeltaMovement().multiply(0.85, 0.87, 0.85));
                }
            }
            if (entity.tickCount % 40 == 0 && entity.level() instanceof ServerLevel serverLevel) {
                stack.hurtAndBreak(1, serverLevel, null, item -> {});
            }
            entity.resetFallDistance();
        }
    }

    public static boolean isParachuteOpen(LivingEntity entity) {
        boolean inCurios = CuriosApi.getCuriosInventory(entity)
                .map(c -> c.findFirstCurio(ModItems.PARACHUTE.get())
                        .map(slotResult -> isOpen(slotResult.stack()))
                        .orElse(false)
                ).orElse(false);

        if (inCurios) return true;

        if (entity instanceof Player player) {
            return player.getInventory().items.stream()
                    .anyMatch(stack -> stack.getItem() == ModItems.PARACHUTE.get() && isOpen(stack));
        }

        return false;
    }

    public static boolean isParachuteVisible(LivingEntity entity) {
        boolean inCurios = CuriosApi.getCuriosInventory(entity)
                .map(c -> c.findFirstCurio(ModItems.PARACHUTE.get())
                        .map(slotResult -> slotResult.slotContext().visible())
                        .orElse(false)
                ).orElse(false);

        if (inCurios) return true;

        if (entity instanceof Player player) {
            for (ItemStack stack : player.getInventory().items) {
                if (stack.getItem() == ModItems.PARACHUTE.get()) {
                    return true;
                }
            }
        }

        return false;
    }
}
