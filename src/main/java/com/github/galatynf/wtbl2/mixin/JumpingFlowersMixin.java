package com.github.galatynf.wtbl2.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public class JumpingFlowersMixin {

    @Inject(at = @At("HEAD"), method = "onEntityCollision")
    private void boom(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if(world.isClient()) {
            return;
        }
        if (((AbstractBlock) (Object) this) instanceof FlowerBlock
                && entity instanceof PlayerEntity
                && !entity.isSneaking()) {
            ((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 20, 20, true, false, false));
        }
    }
}