package com.example.mixin;

import com.example.iMixin.IVexTntMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class TacticalVexesMixin extends LivingEntity implements IVexTntMixin {
    protected TacticalVexesMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tryAttack", at=@At("HEAD"))
    private void explodeBomb(Entity target, CallbackInfoReturnable<Boolean> cir) {
        World world = this.getWorld();
        if(!world.isClient()
                && this.getType().equals(EntityType.VEX)
                && this.getStackInHand(this.getActiveHand()).getItem().equals(Items.TNT)) {
            world.createExplosion((Entity)(Object)this, this.getX(), this.getY(), this.getZ(), 5, false, World.ExplosionSourceType.MOB);
        }
    }

    @Inject(method = "tick", at=@At("HEAD"))
    private void detonateAutomatically(CallbackInfo ci) {
        World world = this.getWorld();
        if(!world.isClient()
                && this.getType().equals(EntityType.VEX)
                && this.age == 200
                && this.getStackInHand(Hand.MAIN_HAND).getItem().equals(Items.TNT)) {
            this.kill();
            world.createExplosion((Entity)(Object)this, this.getX(), this.getY(), this.getZ(), 5, false, World.ExplosionSourceType.MOB);
        }
    }
}
