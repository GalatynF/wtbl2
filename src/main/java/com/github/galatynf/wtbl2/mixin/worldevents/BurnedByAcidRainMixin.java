package com.github.galatynf.wtbl2.mixin.worldevents;

import com.github.galatynf.wtbl2.enums.Wtbl2OverworldEvents;
import com.github.galatynf.wtbl2.iMixin.IServerWorldMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class BurnedByAcidRainMixin {


    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow public abstract World getEntityWorld();

    @Shadow protected abstract boolean isBeingRainedOn();

    @Shadow public abstract EntityType<?> getType();

    @Inject(method = "tick", at=@At("HEAD"))
    private void getAcidBurn(CallbackInfo ci) {
        World world = this.getEntityWorld();
        if(!world.isClient()
                && this.getType().equals(EntityType.PLAYER)
                && this.isBeingRainedOn()
                && ((IServerWorldMixin)world).getCurrentEvent().equals(Wtbl2OverworldEvents.ACID_RAIN)) {
            this.damage(world.getDamageSources().generic(), 1);
        }
    }
}
