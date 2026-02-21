package com.cowax.cowaxpack.item.curio;

import com.cowax.cowaxpack.CowaxPack;
import com.cowax.cowaxpack.network.ParachuteMessage;
import com.cowax.cowaxpack.init.ModItems;
import com.cowax.cowaxpack.init.CowaxSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class ParachuteItem extends Item implements ICurioItem {
    public static final String TAG_OPEN = "Open";
    private static boolean wasJumping = false; // Для отслеживания предыдущего состояния клавиши

    public ParachuteItem() {
        super(new Properties().stacksTo(1).durability(600));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack pStack, ItemStack pRepairCandidate) {
        return pRepairCandidate.is(Items.PHANTOM_MEMBRANE);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return CuriosApi.getCuriosInventory(slotContext.entity())
                .resolve()
                .flatMap(c -> c.findFirstCurio(this))
                .isEmpty();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        updateParachute(stack, slotContext.entity());
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;

            // Ищем парашют сначала в Curios, потом в инвентаре
            ItemStack parachute = ItemStack.EMPTY;
            
            // Проверяем Curios слот
            var curiosResult = CuriosApi.getCuriosInventory(player)
                    .resolve()
                    .flatMap(c -> c.findFirstCurio(ModItems.PARACHUTE.get()));
            
            if (curiosResult.isPresent()) {
                parachute = curiosResult.get().stack();
            } else {
                // Если не в Curios, ищем в обычном инвентаре
                parachute = player.getInventory().items.stream()
                        .filter(stack -> stack.getItem() == ModItems.PARACHUTE.get())
                        .findFirst()
                        .orElse(ItemStack.EMPTY);
            }

            if (!parachute.isEmpty()) {
                // Переключаем парашют при нажатии пробела (только на клиенте)
                if (player.level().isClientSide && player instanceof net.minecraft.client.player.LocalPlayer localPlayer) {
                    boolean isJumping = localPlayer.input.jumping;
                    boolean isOpen = parachute.getOrCreateTag().getBoolean(TAG_OPEN);
                    
                    // Обнаруживаем новое нажатие (было не нажато, стало нажато)
                    if (isJumping && !wasJumping) {
                        // Проверяем условия для открытия/закрытия
                        if (!player.onGround() && 
                            !player.isInWater() && 
                            !player.isFallFlying() && 
                            !player.getAbilities().flying &&
                            !isPlayerBleeding(player)) { // Проверка на состояние "ранен"
                            
                            if (!isOpen) {
                                // Открываем парашют только если падаем достаточно быстро
                                if (player.getDeltaMovement().y < -0.6 && player.fallDistance > 1) {
                                    parachute.getOrCreateTag().putBoolean(TAG_OPEN, true);
                                    player.level().playSound(player, player.getX(), player.getY(), player.getZ(), 
                                        CowaxSounds.PARACHUTE_OPEN.get(), SoundSource.PLAYERS, 1f, 1);
                                    CowaxPack.NETWORK.sendToServer(ParachuteMessage.INSTANCE);
                                }
                            } else {
                                // Закрываем парашют
                                parachute.getOrCreateTag().putBoolean(TAG_OPEN, false);
                                player.level().playSound(player, player.getX(), player.getY(), player.getZ(), 
                                    CowaxSounds.PARACHUTE_CLOSE.get(), SoundSource.PLAYERS, 1f, 1);
                                CowaxPack.NETWORK.sendToServer(ParachuteMessage.INSTANCE);
                            }
                        }
                    }
                    
                    wasJumping = isJumping;
                }
                
                updateParachute(parachute, player);
            }
        }
    }
    
    // Проверка состояния "ранен" из PlayerRevive
    private boolean isPlayerBleeding(Player player) {
        try {
            // Пытаемся получить capability из PlayerRevive
            Class<?> playerReviveServerClass = Class.forName("team.creative.playerrevive.server.PlayerReviveServer");
            java.lang.reflect.Method isBleedingMethod = playerReviveServerClass.getMethod("isBleeding", Player.class);
            return (boolean) isBleedingMethod.invoke(null, player);
        } catch (Exception e) {
            // Если PlayerRevive не установлен или произошла ошибка, возвращаем false
            return false;
        }
    }

    private void updateParachute(ItemStack stack, LivingEntity entity) {
        if (stack.getOrCreateTag().getBoolean(TAG_OPEN)) {
            if ((entity.onGround() || entity.isInWater()) || entity.isFallFlying() || entity.getVehicle() != null || (entity instanceof Player player && player.getAbilities().flying)) {
                stack.getOrCreateTag().putBoolean(TAG_OPEN, false);
                entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), CowaxSounds.PARACHUTE_CLOSE.get(), SoundSource.PLAYERS, 1f, 1);
            }
            if (entity instanceof Player player) {
                // Применяем эффект парашюта только на клиенте, как в SuperbWarfare
                if (player.level().isClientSide) {
                    player.addDeltaMovement(new Vec3(player.getLookAngle().x, 0, player.getLookAngle().z).normalize().scale(0.05));
                    // Замедляем горизонтальное движение сильнее (0.85), вертикальное замедляем меньше (0.85 вместо 0.75) для более быстрого падения
                    player.setDeltaMovement(player.getDeltaMovement().multiply(0.85, 0.87, 0.85));
                }
            }
            if (entity.tickCount % 40 == 0) {
                stack.hurtAndBreak(1, entity, p -> {});
            }
            entity.resetFallDistance();
        }
    }

    public static boolean isParachuteOpen(LivingEntity entity) {
        // Check Curios first
        boolean inCurios = CuriosApi.getCuriosInventory(entity)
                .map(c -> c.findFirstCurio(ModItems.PARACHUTE.get())
                        .map(slotResult -> slotResult.stack().getOrCreateTag().getBoolean(ParachuteItem.TAG_OPEN))
                        .orElse(false)
                ).orElse(false);

        if (inCurios) return true;

        // Check inventory
        if (entity instanceof Player player) {
            return player.getInventory().items.stream()
                    .anyMatch(stack -> stack.getItem() == ModItems.PARACHUTE.get() && 
                                     stack.getOrCreateTag().getBoolean(TAG_OPEN));
        }

        return false;
    }

    public static boolean isParachuteVisible(LivingEntity entity) {
        // Check Curios first
        boolean inCurios = CuriosApi.getCuriosInventory(entity)
                .map(c -> c.findFirstCurio(ModItems.PARACHUTE.get())
                        .map(slotResult -> slotResult.slotContext().visible())
                        .orElse(false)
                ).orElse(false);

        if (inCurios) return true;

        // If not in Curios, it's visible from inventory
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
