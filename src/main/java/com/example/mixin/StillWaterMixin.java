package com.example.mixin;

import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WaterFluid.class)
public abstract class StillWaterMixin extends FlowableFluid {
    @Inject(at=@At("HEAD"), method="getFlowSpeed", cancellable = true)
    private void noThanks(WorldView world, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
        cir.cancel();
    }
}
