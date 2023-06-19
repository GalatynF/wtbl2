package com.github.galatynf.wtbl2.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MovementSpeedMixin extends Entity {
    public MovementSpeedMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);


    @Inject(method="tick", at=@At("HEAD"))
    private void changeSpeed(CallbackInfo ci) {
        if(!getWorld().isClient()) {
            if (this.isOnFire()) {
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 10, 2, true, false, false));
            } else if (this.getSteppingBlockState().equals(Blocks.SAND.getDefaultState())
                    || this.getSteppingBlockState().equals(Blocks.RED_SAND.getDefaultState())) {
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 10, 0, true, false, false));
            }
        }
    }
}
