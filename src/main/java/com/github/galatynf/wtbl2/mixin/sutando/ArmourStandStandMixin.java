package com.github.galatynf.wtbl2.mixin.sutando;

import com.github.galatynf.wtbl2.MusicPlayer;
import com.github.galatynf.wtbl2.cardinal.MyComponents;
import com.github.galatynf.wtbl2.cardinal.StandAttackMannequinComponent;
import com.github.galatynf.wtbl2.iMixin.ISongMixin;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStandEntity.class)
public abstract class ArmourStandStandMixin extends LivingEntity implements ISongMixin {
    @Shadow public abstract void setRightArmRotation(EulerAngle angle);

    @Shadow public abstract void setLeftArmRotation(EulerAngle angle);

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    protected ArmourStandStandMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);

    }

    @Override
    public void setSong(MusicPlayer mPlayer) {
        this.musicPlayer = mPlayer;
    }

    @Unique
    private MusicPlayer musicPlayer = new MusicPlayer();

    @Inject(method = "damage", at=@At("HEAD"), cancellable = true)
    private void invincibleStand(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        World world = this.getWorld();
        if(!world.isClient()) {
            StandAttackMannequinComponent component = MyComponents.STAND_ATTACK_MANNEQUIN.get((ArmorStandEntity)(Object)this);
            if(component.getOwnerUuid() != null) {
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
            if(component.getOwnerUuid() == null)
                // Normal armor stand
                return;

            LivingEntity standOwner;
            if(component.isOwnerPlayer())
                standOwner = (LivingEntity) world.getPlayerByUuid(component.getOwnerUuid());
            else
                standOwner = (LivingEntity) world.getEntityById(component.getOwnerId());
            if (standOwner == null) {
                // Player left
                this.remove(RemovalReason.DISCARDED);
                return;
            }

            if(world.getTime() % 2 == 0) {
                this.musicPlayer.playNextNotes(world, (Entity)(Object)this, true);
            }

            if(component.getRemainingDuration() == 0) {
                this.remove(RemovalReason.DISCARDED);
                MyComponents.STAND_ATTACKER.get(standOwner).setStandAttack(-1);
            }
            else {
                Vec3d rotation = standOwner.getRotationVector().normalize();
                this.setPosition(standOwner.getX()-1.5*rotation.x, standOwner.getY()+1, standOwner.getZ()-1.5*rotation.z);
                this.setYaw(standOwner.getYaw());
                int armRotation = (int) (world.getTime()%2==0 ? 270 : 320);
                this.setLeftArmRotation(new EulerAngle(armRotation, 0, 270));
                this.setRightArmRotation(new EulerAngle(armRotation == 270 ? 320 : 270, 0, 90));
                component.decrementRemainingDuration();
                if(this.getEquippedStack(EquipmentSlot.HEAD).getItem().equals(Items.IRON_HELMET))
                    world.playSoundFromEntity(null, (Entity)(Object)this, SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, SoundCategory.PLAYERS, 0.5f, 1f);
                else
                    world.playSoundFromEntity(null, (Entity)(Object)this, SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.PLAYERS, 0.5f, 1f);
                /* ********** ATTACK *********** */
                if(world.getTime()%5 == 0) {

                    /*
                    world.createExplosion(standOwner,
                            standOwner.getX()+1.5*rotation.x, 1+standOwner.getY()+1.5*rotation.y, standOwner.getZ()+1.5*rotation.z,
                            1,
                            World.ExplosionSourceType.NONE);

                     */
                    AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(world, standOwner.getX()+1.5*rotation.x, Math.random()*1.5+1+ standOwner.getY()+1.5*rotation.y, standOwner.getZ()+1.5*rotation.z);
                    areaEffectCloudEntity.setParticleType(ParticleTypes.CRIT);
                    areaEffectCloudEntity.setDuration(1);
                    areaEffectCloudEntity.setRadius(1);
                    world.spawnEntity(areaEffectCloudEntity);

                    world.createExplosion(standOwner, world.getDamageSources().generic(), new ExplosionBehavior(),
                            standOwner.getX()+1.5*rotation.x, 1.5+ standOwner.getY()+1.5*rotation.y, standOwner.getZ()+1.5*rotation.z,
                            1, false, World.ExplosionSourceType.NONE, false);
                }
            }
        }
    }
}
