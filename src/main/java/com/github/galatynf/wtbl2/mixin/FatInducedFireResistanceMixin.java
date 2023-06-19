package com.github.galatynf.wtbl2.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class FatInducedFireResistanceMixin extends LivingEntity {
    @Shadow public abstract HungerManager getHungerManager();


    protected FatInducedFireResistanceMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method="tick", at=@At("INVOKE"))
    private void fatProtectsAgainstFire(CallbackInfo ci) {
        if(this.getWorld().isClient()) {
            return;
        }
        if(this.getHungerManager().getFoodLevel() >= 17) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 30, 0, true, false, false));
        }
        if(this.getHungerManager().getSaturationLevel() > 0) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 30, 0, true, false, false));
        }
    }
}
