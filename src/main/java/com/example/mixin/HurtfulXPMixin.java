package com.example.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbEntity.class)
public abstract class HurtfulXPMixin extends Entity {
    public HurtfulXPMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method="onPlayerCollision", at=@At("HEAD"))
    private void xpBad(PlayerEntity player, CallbackInfo ci) {
        if(!this.getWorld().isClient() && player.experiencePickUpDelay == 0) {
            player.damage(this.getWorld().getDamageSources().magic(), 0.5f);
        }
    }
}
