package com.github.galatynf.wtbl2.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Entity.class)
public abstract class NeverEndingFireMixin {
    @Shadow public abstract World getWorld();

    @ModifyArg(method="setOnFireFor", at=@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;setFireTicks(I)V"))
    private int prolongFireDuration(int fireTicks) {

        if(this.getWorld().isClient()) {
            return fireTicks;
        }
        return fireTicks * 300;
    }
}
