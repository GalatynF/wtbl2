package com.github.galatynf.wtbl2.mixin;

import com.github.galatynf.wtbl2.Tool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class IBelieveICanBeaconFlyMixin {

    @Shadow public abstract BlockState getDefaultState();

    @Inject(method="onSteppedOn", at=@At("HEAD"))
    private void IBelieveICanTouchTheSky(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
        if(!world.isClient() && this.getDefaultState().equals(Blocks.BEACON.getDefaultState()) && entity instanceof LivingEntity) {
            Tool.addStatus((LivingEntity) entity, StatusEffects.LEVITATION, 500, 60, false, false);
        }
    }
}
