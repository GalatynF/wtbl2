package com.github.galatynf.wtbl2.mixin.entity;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherSkullEntity.class)
public abstract class WitherSkullMixin extends ExplosiveProjectileEntity {
    @Shadow public abstract boolean isCharged();

    protected WitherSkullMixin(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public WitherSkullMixin(EntityType<? extends ExplosiveProjectileEntity> type, double x, double y, double z, double directionX, double directionY, double directionZ, World world) {
        super(type, x, y, z, directionX, directionY, directionZ, world);
    }

    public WitherSkullMixin(EntityType<? extends ExplosiveProjectileEntity> type, LivingEntity owner, double directionX, double directionY, double directionZ, World world) {
        super(type, owner, directionX, directionY, directionZ, world);
    }

    @Inject(method="onCollision", at=@At("TAIL"))
    private void summonPhantoms(HitResult hitResult, CallbackInfo ci) {
        World world = this.getWorld();
        if (world.isClient())
                return;

        int nbPhantoms = this.isCharged() ? 2 : (Math.random() < 0.3 ? 1 : 0);
        EntityData entityData = null;
        for (int i = 0 ; i < nbPhantoms ; ++i) {
            PhantomEntity phantomEntity = EntityType.PHANTOM.create(world);
            if (phantomEntity == null) continue;
            phantomEntity.refreshPositionAndAngles(this.getBlockPos(), 0.0f, 0.0f);
            entityData = phantomEntity.initialize((ServerWorldAccess) world, world.getLocalDifficulty(this.getBlockPos()), SpawnReason.MOB_SUMMONED, entityData, null);
            world.spawnEntity(phantomEntity);
        }
    }
}
