package com.example.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExplosiveProjectileEntity.class)
public abstract class EnderThunderMixin extends ProjectileEntity {

    public EnderThunderMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at=@At("HEAD"))
    private void summonLightning(CallbackInfo ci) {
        World world = this.getWorld();
        if(!world.isClient() && world.getTime()%5 == 0 && this.getType().equals(EntityType.DRAGON_FIREBALL)) {
            LightningEntity lightningEntity;
            BlockPos blockPos = this.getBlockPos();
            SoundEvent soundEvent = SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH;
            if((lightningEntity = EntityType.LIGHTNING_BOLT.create(this.getWorld())) != null) {
                lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos.add(-3*this.getMovementDirection().getOffsetX(), -5, -3*this.getMovementDirection().getOffsetZ())));
                this.getWorld().spawnEntity(lightningEntity);
                soundEvent = SoundEvents.ITEM_TRIDENT_THUNDER;
            }
            this.playSound(soundEvent, 5, 1.0f);
        }
    }
}
