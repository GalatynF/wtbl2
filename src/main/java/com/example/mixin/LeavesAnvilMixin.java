package com.example.mixin;

import com.example.iMixin.IAnvilMixin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class LeavesAnvilMixin implements IAnvilMixin {

    @Override
    public void turnToAnvilAndUpdate(World world, BlockPos pos, boolean turnTo, boolean checkIfLeaf, int timesDone) {
        if (timesDone == 0 || (checkIfLeaf && !isALeaf(world.getBlockState(pos).getBlock())))
                return;
        if(turnTo)
            world.setBlockState(pos, Blocks.CHIPPED_ANVIL.getDefaultState());
        for (int i = -1 ; i <= 1 ; ++i) {
            for (int j = -1 ; j <= 1 ; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    if (!(i == 0 && j == 0 && k == 0)) {
                        BlockPos newPos = new BlockPos(pos.add(i, j, k));
                        ((IAnvilMixin) world.getBlockState(pos).getBlock()).turnToAnvilAndUpdate(world, newPos, true, true, timesDone-1);
                    }
                }
            }
        }
    }

    private boolean isALog(Block block) {
        return (block.equals(Blocks.ACACIA_LOG)
                || block.equals(Blocks.BIRCH_LOG)
                || block.equals(Blocks.CHERRY_LOG)
                || block.equals(Blocks.OAK_LOG)
                || block.equals(Blocks.JUNGLE_LOG)
                || block.equals(Blocks.SPRUCE_LOG)
                || block.equals(Blocks.MANGROVE_LOG)
                || block.equals(Blocks.DARK_OAK_LOG));
    }

    private boolean isALeaf(Block block) {
        return (block.equals(Blocks.ACACIA_LEAVES)
                || block.equals(Blocks.BIRCH_LEAVES)
                || block.equals(Blocks.CHERRY_LEAVES)
                || block.equals(Blocks.OAK_LEAVES)
                || block.equals(Blocks.JUNGLE_LEAVES)
                || block.equals(Blocks.SPRUCE_LEAVES)
                || block.equals(Blocks.MANGROVE_LEAVES)
                || block.equals(Blocks.DARK_OAK_LEAVES));
    }

    @Inject(method="onBroken", at=@At("TAIL"))
    private void toAnvils(WorldAccess world, BlockPos pos, BlockState state, CallbackInfo ci) {
        if(world.isClient() || !this.isALog(state.getBlock())) {
            return;
        }

        turnToAnvilAndUpdate((World) world, pos, false, false, 5);
    }
}
