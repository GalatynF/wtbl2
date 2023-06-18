package com.example.mixin.jumpscare;

import com.example.cardinal.MyComponents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RaiderEntity.class)
public abstract class WitchCurseMixin extends PatrolEntity {
    protected WitchCurseMixin(EntityType<? extends RaiderEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onDeath", at=@At("HEAD"))
    private void curseattacker(DamageSource damageSource, CallbackInfo ci) {
        World world = this.getWorld();
        if(!world.isClient()
                && this.getType().equals(EntityType.WITCH)
                && damageSource.getAttacker() != null
                && damageSource.getAttacker().getType().equals(EntityType.PLAYER)) {
            MyComponents.CURSED.get(damageSource.getAttacker()).setMannequinCursed(true);
        }
    }
}
