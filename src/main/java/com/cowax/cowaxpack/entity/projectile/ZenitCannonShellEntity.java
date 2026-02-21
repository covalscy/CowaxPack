package com.cowax.cowaxpack.entity.projectile;

import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.NetworkRegistry;
import com.atsuishio.superbwarfare.network.message.receive.ClientIndicatorMessage;
import com.atsuishio.superbwarfare.tools.DamageHandler;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;

public class ZenitCannonShellEntity extends FastThrowableProjectile implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ZenitCannonShellEntity(EntityType<? extends ZenitCannonShellEntity> type, Level level) {
        super(type, level);
        this.noCulling = true;
        this.damage = 40f;
        this.explosionDamage = 0f;
        this.explosionRadius = 0f;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isColliding(BlockPos pPos, BlockState pState) {
        return true;
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.SMALL_SHELL.get();
    }

    @Override
    public void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        // No explosion logic
        this.discard();
    }

    @Override
    public void onHitEntity(@NotNull EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (this.level() instanceof ServerLevel) {
            Entity entity = entityHitResult.getEntity();
            if (this.getOwner() != null && entity == this.getOwner().getVehicle())
                return;

            DamageHandler.doDamage(entity, ModDamageTypes.causeProjectileHitDamage(this.level().registryAccess(), this, this.getOwner()), this.damage);

            if (entity instanceof LivingEntity) {
                entity.invulnerableTime = 0;
            }

            if (this.getOwner() instanceof LivingEntity living) {
                if (!living.level().isClientSide() && living instanceof ServerPlayer player) {
                    living.level().playSound(null, living.blockPosition(), ModSounds.INDICATION.get(), SoundSource.VOICE, 1, 1);
                    NetworkRegistry.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new ClientIndicatorMessage(0, 5));
                }
            }

            ParticleTool.cannonHitParticles(this.level(), this.position(), this);
            // No causeExplode
            this.discard();
        }
    }

    private boolean aa;

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount > 600) {
            // No explosion on timeout
            this.discard();
        }

        if (aa) {
            crushProjectile(getDeltaMovement());
        }
    }

    public void crushProjectile(Vec3 velocity) {
        if (this.level() instanceof ServerLevel) {
            var frontBox = getBoundingBox().inflate(0.5).expandTowards(velocity);

            java.util.Optional<net.minecraft.world.entity.projectile.Projectile> target = level().getEntities(
                            net.minecraft.world.level.entity.EntityTypeTest.forClass(net.minecraft.world.entity.projectile.Projectile.class), frontBox, entity -> entity != this).stream()
                    .filter(entity -> !(entity instanceof ZenitCannonShellEntity) && (entity.getBbWidth() >= 0.3 || entity.getBbHeight() >= 0.3))
                    .min(java.util.Comparator.comparingDouble(e -> e.position().distanceTo(position())));
            if (target.isPresent()) {
                // No explosion, just hit effect
                ParticleTool.cannonHitParticles(this.level(), target.get().position(), this);
                
                if (target.get() instanceof com.atsuishio.superbwarfare.entity.projectile.DestroyableProjectile destroyableProjectile) {
                    if (this.getOwner() instanceof LivingEntity living) {
                        if (!living.level().isClientSide() && living instanceof ServerPlayer player) {
                            living.level().playSound(null, living.blockPosition(), ModSounds.INDICATION.get(), SoundSource.VOICE, 1, 1);
                            NetworkRegistry.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new ClientIndicatorMessage(0, 5));
                        }
                    }
                    DamageHandler.doDamage(destroyableProjectile, ModDamageTypes.causeProjectileHitDamage(this.level().registryAccess(), this, this.getOwner()), damage);
                } else {
                    target.get().discard();
                }

                this.discard();
            }
        }
    }

    public void antiAir(boolean antiAir) {
        this.aa = antiAir;
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
        return false;
    }
}
