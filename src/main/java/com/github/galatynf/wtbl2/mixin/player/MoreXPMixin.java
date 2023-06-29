package com.github.galatynf.wtbl2.mixin.player;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public class MoreXPMixin {
    @ModifyVariable(method = "addExperience", at=@At("HEAD"), argsOnly = true)
    private int moreXP(int value) {
        return value*3;
    }
}
