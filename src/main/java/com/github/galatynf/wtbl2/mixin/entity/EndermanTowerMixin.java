package com.github.galatynf.wtbl2.mixin.entity;

import com.github.galatynf.wtbl2.Tool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class EndermanTowerMixin extends LivingEntity {


    @Shadow public abstract boolean startRiding(Entity entity, boolean force);

    protected EndermanTowerMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at=@At("TAIL"))
    private void rideFriend(CallbackInfo ci) {
        World world = this.getWorld();
        if(!world.isClient() && this.getType().equals(EntityType.ENDERMAN)) {
            EndermanEntity enderman = world.getClosestEntity(EndermanEntity.class, TargetPredicate.DEFAULT, this, this.getX(), this.getY(), this.getZ(), this.getBoundingBox());
            if(enderman != null) {
                this.startRiding(enderman);
                Tool.addStatus(enderman, StatusEffects.SPEED, 200, 0, false, false);
            }
        }
    }
}
