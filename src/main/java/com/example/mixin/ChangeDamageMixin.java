package com.example.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(LivingEntity.class)
public abstract class ChangeDamageMixin extends Entity {
    public ChangeDamageMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyArg(method = "damage", at=@At(value="INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"))
    private float multiplyDamageBySpeed(DamageSource source, float amount) {
        if (source.getAttacker() != null && source.getAttacker() instanceof LivingEntity) {
            amount = amount * 10 * ((LivingEntity)source.getAttacker()).getMovementSpeed();
        }
        return amount;
    }
}
