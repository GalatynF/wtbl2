package com.example.mixin;

import com.example.Tool;
import com.example.iMixin.IEntityValuesMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public abstract class SpecialArrowsMixin extends Entity {

    @Shadow public abstract @Nullable Entity getOwner();

    public SpecialArrowsMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onCollision", at=@At("INVOKE"))
    private void arrowSpecialEffects(HitResult hitResult, CallbackInfo ci) {
        World world = this.getWorld();
        Entity owner = this.getOwner();
        HitResult.Type type = hitResult.getType();

        if(!world.isClient() && this.getCustomName() != null) {
            if(this.getCustomName().getString().equals("wtbl2_lightning") && !type.equals(HitResult.Type.MISS)) {
                LightningEntity lightningEntity;
                BlockPos blockPos = this.getBlockPos();
                SoundEvent soundEvent = SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH;
                if (this.getWorld().isSkyVisible(blockPos) && (lightningEntity = EntityType.LIGHTNING_BOLT.create(this.getWorld())) != null) {
                    lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
                    lightningEntity.setChanneler(owner instanceof ServerPlayerEntity ? (ServerPlayerEntity)owner : null);
                    this.getWorld().spawnEntity(lightningEntity);
                    soundEvent = SoundEvents.ITEM_TRIDENT_THUNDER;
                }
                this.playSound(soundEvent, 5, 1.0f);
                //this.remove(RemovalReason.DISCARDED);
            }
            if (this.getCustomName().getString().equals("wtbl2_accio") && type == HitResult.Type.ENTITY && this.getOwner()!=null) {
                Entity target = ((EntityHitResult)hitResult).getEntity();
                if(((IEntityValuesMixin) target).canBeAccioed()) {
                    Tool.dashTo(target, this.getOwner(), 0.5f);
                    this.remove(RemovalReason.DISCARDED);
                    this.playSound(SoundEvents.ENTITY_ENDER_DRAGON_FLAP, 5, 1);
                    ((IEntityValuesMixin) target).setAccioed(20);
                }
            }
        }
    }
}
