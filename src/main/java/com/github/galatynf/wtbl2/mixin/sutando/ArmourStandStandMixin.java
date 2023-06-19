package com.github.galatynf.wtbl2.mixin.sutando;

import com.github.galatynf.wtbl2.Tool;
import com.github.galatynf.wtbl2.cardinal.MyComponents;
import com.github.galatynf.wtbl2.cardinal.StandAttackMannequinComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStandEntity.class)
public abstract class ArmourStandStandMixin extends LivingEntity {
    protected ArmourStandStandMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "damage", at=@At("HEAD"), cancellable = true)
    private void invincibleStand(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        World world = this.getWorld();
        if(!world.isClient()) {
            StandAttackMannequinComponent component = MyComponents.STAND_ATTACK_MANNEQUIN.get((ArmorStandEntity)(Object)this);
            if(component.getOwner() != null) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }

    @Inject(method = "tick", at=@At("HEAD"))
    private void standAttack(CallbackInfo ci) {
        World world = this.getWorld();
        if(!world.isClient()) {
            StandAttackMannequinComponent component = MyComponents.STAND_ATTACK_MANNEQUIN.get((ArmorStandEntity)(Object)this);
            if(component.getOwner() == null)
                // Normal armor stand
                return;
            PlayerEntity playerOwner = world.getPlayerByUuid(component.getOwner());
            if(playerOwner == null) {
                // Player left
                this.remove(RemovalReason.DISCARDED);
                return;
            }

            if(world.getTime() % 3 == 0) {
                boolean songHasFinished = Tool.playSong(world,
                        (Entity) (Object) this,
                        SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(),
                        MyComponents.MUSIC_PLAYER.get((Entity) (Object) this).nextNote());
            }

            if(component.getRemainingDuration() == 0) {
                this.remove(RemovalReason.DISCARDED);
                MyComponents.STAND_ATTACKER.get(playerOwner).setStandAttack(-1);
            }
            else {
                Vec3d rotation = playerOwner.getRotationVector().normalize();
                this.setPosition(playerOwner.getX()-1.5*rotation.x, playerOwner.getY()+1, playerOwner.getZ()-1.5*rotation.z);
                this.setYaw(playerOwner.getYaw());
                component.decrementRemainingDuration();

                /* ********** ATTACK *********** */
                if(world.getTime()%5 == 0) {
                    /*world.createExplosion(playerOwner,
                            playerOwner.getX()+1.5*rotation.x, 1+playerOwner.getY()+1.5*rotation.y, playerOwner.getZ()+1.5*rotation.z,
                            1,
                            World.ExplosionSourceType.NONE);
                    */
                }
            }
        }
    }
}
