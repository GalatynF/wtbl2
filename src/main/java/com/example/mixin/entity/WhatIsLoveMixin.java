package com.example.mixin.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieEntity.class)
public abstract class WhatIsLoveMixin extends HostileEntity {
    protected WhatIsLoveMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract boolean isBaby();

    @Inject(at = @At("HEAD"), method = "tick")
    private void babyDontHurtMe(CallbackInfo ci) {
        if (this.isBaby() && !getWorld().isClient()) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 30, 1, true, false, false));
        }
    }
}