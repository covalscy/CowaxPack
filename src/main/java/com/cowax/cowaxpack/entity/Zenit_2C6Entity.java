package com.cowax.cowaxpack.entity;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class Zenit_2C6Entity extends GeoVehicleEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    public Zenit_2C6Entity(EntityType<? extends GeoVehicleEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        int seatsCount = computed().seats().size();
        int maxPass = getMaxPassengers();
        int curPass = getPassengers().size();
        boolean isClient = level().isClientSide();

        String debugMsg = String.format("[2C6 DEBUG interact] Side: %s, maxPass: %d, seats: %d, curPass: %d, shift: %b, canAdd: %b",
                isClient ? "CLIENT" : "SERVER", maxPass, seatsCount, curPass, player.isShiftKeyDown(), canAddPassenger(player));

        LOGGER.info(debugMsg);
        player.displayClientMessage(Component.literal(debugMsg), false);

        InteractionResult result = super.interact(player, hand);

        String resultMsg = String.format("[2C6 DEBUG result] Side: %s -> %s, playerVehicle: %s",
                isClient ? "CLIENT" : "SERVER", result, player.getVehicle() != null ? player.getVehicle().getName().getString() : "null");
        LOGGER.info(resultMsg);
        player.displayClientMessage(Component.literal(resultMsg), false);

        return result;
    }

    @Override
    protected boolean canAddPassenger(Entity pPassenger) {
        boolean can = super.canAddPassenger(pPassenger);
        LOGGER.info("[2C6 DEBUG canAddPassenger] passenger: {}, result: {}", pPassenger.getName().getString(), can);
        return can;
    }

    @Override
    protected void addPassenger(Entity pPassenger) {
        LOGGER.info("[2C6 DEBUG addPassenger] adding: {}", pPassenger.getName().getString());
        super.addPassenger(pPassenger);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> getSourceAngle(source, 0.4f) * damage);
    }

    private PlayState cannonShootPredicate(AnimationState<Zenit_2C6Entity> event) {
        if (getShootAnimationTimer(0, 0) > 0) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.2c6.fire"));
        }
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.2c6.idle"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "cannon", 0, this::cannonShootPredicate));
    }
}
