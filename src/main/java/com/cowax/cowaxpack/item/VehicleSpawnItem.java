package com.cowax.cowaxpack.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.function.Supplier;

public class VehicleSpawnItem<T extends Entity> extends Item {
    private final Supplier<EntityType<T>> entityType;

    public VehicleSpawnItem(Supplier<EntityType<T>> entityType, Properties properties) {
        super(properties);
        this.entityType = entityType;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockPos clickedPos = context.getClickedPos();
        BlockPos spawnPos = clickedPos.relative(context.getClickedFace());
        EntityType<T> type = entityType.get();
        if (type != null) {
            T entity = type.create(level);
            if (entity != null) {
                entity.moveTo(
                        spawnPos.getX() + 0.5,
                        spawnPos.getY(),
                        spawnPos.getZ() + 0.5,
                        context.getPlayer() != null ? context.getPlayer().getYRot() : 0.0f,
                        0.0f
                );
                level.addFreshEntity(entity);
                level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, spawnPos);
                if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.FAIL;
    }
}
