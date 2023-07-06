package com.github.galatynf.wtbl2.mixin.worldevents;

import com.github.galatynf.wtbl2.Tool;
import com.github.galatynf.wtbl2.enums.Wtbl2OverworldEvents;
import com.github.galatynf.wtbl2.iMixin.IServerWorldMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class ProximityManagementMixin extends LivingEntity {
    @Shadow public abstract boolean isCreative();

    @Shadow protected abstract void takeShieldHit(LivingEntity attacker);

    protected ProximityManagementMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at=@At("HEAD"))
    private void manageProximity(CallbackInfo ci) {
        World world = this.getWorld();
        if(!world.isClient() && !this.isCreative()) {
            if (((IServerWorldMixin)world).getCurrentEvent().equals(Wtbl2OverworldEvents.COVID_19)) {
                boolean thereIsAPlayer = false;
                for (PlayerEntity p : world.getPlayers()) {
                    if(p.getUuid() != this.getUuid() && this.distanceTo(p)<=5 && !p.isCreative()) {
                        thereIsAPlayer = true;
                    }
                }
                if (thereIsAPlayer && this.getStatusEffect(StatusEffects.POISON)==null) {
                    Tool.addStatus((LivingEntity)(Object)this, StatusEffects.POISON, 2400, 0, false, true);
                }
            }
            else if (((IServerWorldMixin)world).getCurrentEvent().equals(Wtbl2OverworldEvents.HUGGY)) {
                boolean thereIsAPlayer = false;
                for (PlayerEntity p : world.getPlayers()) {
                    if(p.getUuid() != this.getUuid() && this.distanceTo(p)<=10 && !p.isCreative()) {
                        thereIsAPlayer = true;
                    }
                }
                if(!thereIsAPlayer) {
                    Tool.addStatus((LivingEntity)(Object)this, StatusEffects.BLINDNESS, 30, 0, false, false);
                }
            }
        }
    }
}
