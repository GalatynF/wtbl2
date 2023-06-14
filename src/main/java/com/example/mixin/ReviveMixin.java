package com.example.mixin;

import com.example.Tool;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class ReviveMixin extends Entity {
    public ReviveMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract boolean isDead();

    @Shadow
    public abstract void setHealth(float health);

    @Shadow public abstract boolean clearStatusEffects();

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @Shadow public abstract void kill();

    @Shadow protected abstract boolean tryUseTotem(DamageSource source);

    @Shadow protected abstract @Nullable SoundEvent getDeathSound();

    @Shadow protected abstract float getSoundVolume();

    @Shadow public abstract float getSoundPitch();

    @Shadow public abstract void onDeath(DamageSource damageSource);

    @Shadow public abstract @Nullable DamageSource getRecentDamageSource();

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow public abstract void setSleepingPosition(BlockPos pos);

    @Unique boolean wtbl2_isDying = false;
    @Unique int wtbl2_reviveTicks = 0;
    @Unique int wtbl2_ticksBeforeDying = 200;
    @Unique DamageSource wtbl2_dyingSource = null;

    public boolean isDying() {
        return this.wtbl2_isDying;
    }

    public void addReviveTicks(int ticks) {
        this.wtbl2_reviveTicks += ticks;
    }


    public void tickBeforeDying() {
        this.wtbl2_ticksBeforeDying--;
    }


    public int getTicksBeforeDying() {
        return this.wtbl2_ticksBeforeDying;
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isDead()Z"), cancellable = true)
    private void enterDyingState(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        World world = this.getWorld();
        if (!world.isClient()
                && this.getType().equals(EntityType.PLAYER)
                && this.isDead()
                && !this.wtbl2_isDying
                && !Objects.equals(source.getType().msgId(), "genericKill")) {
            ServerPlayerEntity player = (ServerPlayerEntity) world.getPlayerByUuid(this.getUuid());
            if (player == null) return;

            this.wtbl2_isDying = true;
            this.wtbl2_dyingSource = source;
            this.setHealth(0.5F);
            ((PlayerEntity) (Object) this).getHungerManager().setFoodLevel(2);
            this.clearStatusEffects();
            Tool.addStatus((LivingEntity) (Object) this, StatusEffects.BLINDNESS, this.wtbl2_ticksBeforeDying, 0, false, false);
            Tool.addStatus((LivingEntity) (Object) this, StatusEffects.RESISTANCE, this.wtbl2_ticksBeforeDying, 10, false, false);
            Tool.addStatus((LivingEntity) (Object) this, StatusEffects.SLOWNESS, this.wtbl2_ticksBeforeDying, 50, false, false);
            Tool.addStatus((LivingEntity) (Object) this, StatusEffects.WEAKNESS, this.wtbl2_ticksBeforeDying, 10, false, false);
            Tool.addStatus((LivingEntity) (Object) this, StatusEffects.MINING_FATIGUE, this.wtbl2_ticksBeforeDying, 10, false, false);

            AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(world, this.getX(), this.getY(), this.getZ());
            areaEffectCloudEntity.setRadius(5);
            areaEffectCloudEntity.setDuration(this.wtbl2_ticksBeforeDying);
            areaEffectCloudEntity.setRadiusGrowth(-0.05f);
            areaEffectCloudEntity.setParticleType(ParticleTypes.HAPPY_VILLAGER);
            world.spawnEntity(areaEffectCloudEntity);
            cir.setReturnValue(false);
        }
    }

    @Inject(method="damage", at = @At("HEAD"), cancellable = true)
    private void noDamageIfDying(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        World world = this.getWorld();
        if(world.isClient() || !this.getType().equals(EntityType.PLAYER)) {
            return;
        }
        if(this.isDying() && !this.isDying()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method="tick", at=@At("HEAD"))
    private void dieIfNotTreated(CallbackInfo ci) {
        World world = this.getWorld();
        if(world.isClient() || !this.getType().equals(EntityType.PLAYER)) {
            return;
        }
        if (this.isDying()) {
            this.setHealth(0.5F);
            ((PlayerEntity) (Object) this).getHungerManager().setFoodLevel(2);
            this.tickBeforeDying();
            if (this.wtbl2_reviveTicks == 0 && this.getTicksBeforeDying() <= 0) {
                this.clearStatusEffects();
                this.damage(this.wtbl2_dyingSource, 1000);
            }

            if(world.getClosestPlayer((Entity)(Object)this, 3) != null
                && world.getClosestPlayer((Entity)(Object)this, 3).getUuid() != this.getUuid()) {
                this.addReviveTicks(1);
            }
            else {
                this.addReviveTicks(this.wtbl2_reviveTicks == 0 ? 0 : -1);
            }
            if(this.wtbl2_reviveTicks >= 100) {
                this.wtbl2_isDying = false;
                this.wtbl2_reviveTicks = 0;
                this.wtbl2_ticksBeforeDying = 200;
                this.wtbl2_dyingSource = null;
                Tool.addStatus((LivingEntity) (Object)this, StatusEffects.SPEED, 60, 1, false, false);
            }
        }
    }
}
