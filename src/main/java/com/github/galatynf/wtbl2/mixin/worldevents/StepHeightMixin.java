package com.github.galatynf.wtbl2.mixin.worldevents;

import com.github.galatynf.wtbl2.Tool;
import com.github.galatynf.wtbl2.enums.Wtbl2OverworldEvents;
import com.github.galatynf.wtbl2.iMixin.IServerWorldMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class StepHeightMixin extends Entity {
    public StepHeightMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "getStepHeight", at=@At("HEAD"), cancellable = true)
    private void returnOne(CallbackInfoReturnable<Float> cir) {
        World world = this.getWorld();
        if (!world.isClient() && this.getType().equals(EntityType.PLAYER) && ((IServerWorldMixin)world).getCurrentEvent().equals(Wtbl2OverworldEvents.STEP_HIGH)) {
            float step = super.getStepHeight();
            cir.setReturnValue(Math.max(step, 1.5f));
        }
    }
}
