package com.example.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherPortalBlock.class)
public abstract class NoPortalsForYouMixin extends Block {
    public NoPortalsForYouMixin(Settings settings) {
        super(settings);
    }

    @Inject(method="onEntityCollision", at=@At("INVOKE"), cancellable = true)
    private void cantHaveShitInOverworld(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (!world.isClient() && entity.isPlayer()) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            ci.cancel();
        }
    }

}
