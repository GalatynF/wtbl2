package com.example.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ChokeInEndMixin extends Entity {

    @Shadow public abstract boolean canBreatheInWater();

    @Shadow protected abstract int getNextAirUnderwater(int air);

    public ChokeInEndMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "getNextAirOnLand", at=@At("HEAD"), cancellable = true)
    private void chokeInBadAirWhenNotFull(int air, CallbackInfoReturnable<Integer> cir) {
        World world = this.getWorld();
        if(!world.isClient()
                && world.getRegistryKey().getValue().toString().equals("minecraft:the_end")
                && !this.canBreatheInWater()
                && !StatusEffectUtil.hasWaterBreathing((LivingEntity)(Entity) this)
                && this.isPlayer()
                && !((PlayerEntity)(Entity)this).getAbilities().invulnerable) {
            cir.setReturnValue(
                    world.getTime()%3 == 0?this.getNextAirUnderwater(air):this.getAir()
            );
        }
    }

    @Inject(method = "baseTick", at=@At("TAIL"))
    private void chokeInBadAirWhenFull(CallbackInfo ci) {
        World world = this.getWorld();
        if(!world.isClient()
                && world.getRegistryKey().getValue().toString().equals("minecraft:the_end")
                && this.getAir() == 300) {
            this.setAir(this.getNextAirUnderwater(this.getAir()));
        }

        // If no air left, drown
        if (this.getAir() <= -20) {
            this.setAir(0);
            this.damage(world.getDamageSources().drown(), 1.0F);
        }
    }
}
