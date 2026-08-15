package com.cowax.cowaxpack.entity.projectile;

import com.atsuishio.superbwarfare.entity.projectile.DestroyableProjectile;
import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ClientIndicatorMessage;
import com.atsuishio.superbwarfare.network.message.receive.ClientMotionSyncMessage;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.DamageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Optional;

public class ZenitCannonShellEntity extends FastThrowableProjectile implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean aa;

    public ZenitCannonShellEntity(EntityType<? extends ZenitCannonShellEntity> type, Level level) {
        super(type, level);
        this.noCulling = true;
        this.setDamageValue(40f);
        this.setExplosionDamageValue(80f);
        this.setExplosionRadiusValue(5f);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isColliding(BlockPos pPos, BlockState pState) {
        return true;
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.SMALL_SHELL_AA.get();
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("AA", this.aa);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("AA")) this.aa = pCompound.getBoolean("AA");
    }

    @Override
    public void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        BlockPos resultPos = blockHitResult.getBlockPos();
        BlockState state = this.level().getBlockState(resultPos);

        // Пробитие мягкой растительности: снаряд ломает листву и брёвна, не взрываясь об них.
        if (state.is(BlockTags.LEAVES)
                || state.is(BlockTags.LOGS)
                || state.is(BlockTags.FENCES)
                || state.is(BlockTags.FENCE_GATES)
                || state.is(BlockTags.WOODEN_DOORS)
                || state.is(BlockTags.WOODEN_STAIRS)
                || state.is(BlockTags.WOODEN_SLABS)
                || state.is(BlockTags.PLANKS)
                || state.is(Tags.Blocks.GLASS_BLOCKS)
                || state.is(Tags.Blocks.GLASS_PANES)) {
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.destroyBlock(resultPos, false, this);
            }
            return;
        }

        if (state.getBlock() instanceof BellBlock bell) {
            bell.attemptToRing(this.level(), resultPos, blockHitResult.getDirection());
        }
        if (this.level() instanceof ServerLevel) {
            causeExplode(blockHitResult.getLocation(), false);
        }
        this.discard();
    }

    @Override
    public void onHitEntity(@NotNull EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity entity = entityHitResult.getEntity();

        if (this.getOwner() != null && this.getOwner().getVehicle() != null && entity == this.getOwner().getVehicle())
            return;
        if (this.level() instanceof ServerLevel) {
            DamageHandler.doDamage(entity, ModDamageTypes.causeProjectileHitDamage(this.level().registryAccess(), this, this.getOwner()), getDamageValue());

            if (entity instanceof LivingEntity) {
                entity.invulnerableTime = 0;
            }

            if (this.tickCount > 0) {
                causeExplode(entityHitResult.getLocation(), true);
            }
            this.discard();
        }
    }

    @Override
    public CustomExplosion.Builder buildExplosion(Vec3 vec3) {
        return super.buildExplosion(vec3)
                .destroyBlock(() -> Explosion.BlockInteraction.KEEP);
    }

    private void causeExplode(Vec3 vec3, boolean hitEntity) {
        new CustomExplosion.Builder(this)
                .attacker(this.getOwner())
                .damage(getExplosionDamageValue())
                .radius(getExplosionRadiusValue())
                .position(vec3)
                .withParticleType(explosionParticleType(getExplosionRadiusValue()))
                // В этом паке снаряд НИКОГДА не ломает блоки
                .destroyBlock(() -> Explosion.BlockInteraction.KEEP)
                .damageMultiplier(1.25F)
                .explode();
    }

    @Override
    public void tick() {
        super.tick();
        if (aa) {
            crushProjectile(getDeltaMovement());
        }
        if (getOwner() != null && distanceToSqr(getOwner()) > 1048576) {
            if (level() instanceof ServerLevel) {
                causeExplode(position());
            }
            this.discard();
        }
    }

    public void crushProjectile(Vec3 velocity) {
        if (this.level() instanceof ServerLevel) {
            var frontBox = getBoundingBox().inflate(0.5).expandTowards(velocity);

            Optional<Projectile> target = level().getEntities(
                            EntityTypeTest.forClass(Projectile.class), frontBox, entity -> entity != this).stream()
                    .filter(entity -> !(entity instanceof ZenitCannonShellEntity) && (entity.getBbWidth() >= 0.3 || entity.getBbHeight() >= 0.3))
                    .min(Comparator.comparingDouble(e -> e.position().distanceTo(position())));
            if (target.isPresent()) {
                causeExplode(target.get().position(), false);
                if (target.get() instanceof DestroyableProjectile destroyableProjectile) {
                    if (this.getOwner() instanceof LivingEntity living) {
                        if (!living.level().isClientSide() && living instanceof ServerPlayer player) {
                            living.level().playSound(null, living.blockPosition(), ModSounds.INDICATION.get(), SoundSource.VOICE, 1, 1);
                            PacketDistributor.sendToPlayer(player, new ClientIndicatorMessage(0, 5));
                        }
                    }
                    DamageHandler.doDamage(destroyableProjectile, ModDamageTypes.causeProjectileHitDamage(this.level().registryAccess(), this, this.getOwner()), getDamageValue());
                } else {
                    target.get().discard();
                }

                this.discard();
            }
        }
    }

    @Override
    public void syncMotion() {
        if (!this.level().isClientSide) {
            PacketDistributor.sendToPlayersTrackingEntity(this, new ClientMotionSyncMessage(this));
        }
    }

    @Override
    public boolean discardAfterExplode() {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public void antiAir(boolean antiAir) {
        this.aa = antiAir;
    }

    @Override
    public boolean isFastMoving() {
        return false;
    }

    @Override
    public @NotNull SoundEvent getSound() {
        return ModSounds.SHELL_FLY.get();
    }

    @Override
    public float getVolume() {
        return 0.07f;
    }

    @Override
    public boolean forceLoadChunk() {
        return true;
    }
}
