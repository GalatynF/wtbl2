package com.example.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Entity.class)
public class NeverEndingFireMixin {
    @ModifyArg(method="setOnFireFor", at=@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;setFireTicks(I)V"))
    private int prolongFireDuration(int fireTicks) {
        return fireTicks * 300;
    }
}
