package com.example.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermanEntity.class)
public abstract class ClassyTerroristsMixin extends HostileEntity {
    protected ClassyTerroristsMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/EndermanEntity;teleport(DDDZ)Z"), method = "teleportTo(DDD)Z")
    private void aaahBOMBE(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (!this.getWorld().isClient) {
            TntEntity tntEntity = new TntEntity(this.getWorld(), this.getX() + 0.5D, this.getY(), this.getZ() + 0.5D, (EndermanEntity) (Object) this);
            this.getWorld().spawnEntity(tntEntity);
            this.getWorld().playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);

        }
    }
}